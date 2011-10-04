package de.codeinfection.quickwango.HideMe;

import de.codeinfection.quickwango.HideMe.commands.SeehiddensCommand;
import de.codeinfection.quickwango.HideMe.commands.UnhideCommand;
import de.codeinfection.quickwango.HideMe.commands.ListhiddensCommand;
import de.codeinfection.quickwango.HideMe.commands.HideCommand;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
//import net.minecraft.server.Packet3Chat;
import net.minecraft.server.ServerConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.packet.PacketManager;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

public class HideMe extends JavaPlugin
{
    protected static final Logger log = Logger.getLogger("Minecraft");
    public static boolean debugMode = true;

    public final List<Player> hiddenPlayers = Collections.synchronizedList(new ArrayList<Player>());
    public final List<Player> canSeeHiddens = Collections.synchronizedList(new ArrayList<Player>());
    
    public Server server;
    public CraftServer cserver;
    public ServerConfigurationManager mojangServer;
    protected PluginManager pm;
    protected Configuration config;
    protected File dataFolder;
    protected PacketManager packetManager;

    public HideMe()
    {}

    public void onEnable()
    {
        this.server = this.getServer();
        this.cserver = (CraftServer)this.server;
        this.mojangServer = this.cserver.getHandle();
        this.pm = this.server.getPluginManager();
        this.config = this.getConfiguration();
        this.dataFolder = this.getDataFolder();
        this.packetManager = SpoutManager.getPacketManager();

        this.dataFolder.mkdirs();
        // Create default config if it doesn't exist.
        if (!(new File(this.dataFolder, "config.yml")).exists())
        {
            this.defaultConfig();
        }
        this.loadConfig();

        this.getCommand("hide").setExecutor(new HideCommand(this));
        this.getCommand("unhide").setExecutor(new UnhideCommand(this));
        this.getCommand("seehiddens").setExecutor(new SeehiddensCommand(this));
        this.getCommand("listhiddens").setExecutor(new ListhiddensCommand(this));

        HideMePlayerListener playerListener = new HideMePlayerListener(this);
        HideMeEntityListener entityListener = new HideMeEntityListener(this);
        
        this.pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Highest, this);
        this.pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Highest, this);
        this.pm.registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Highest, this);
        this.pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);
        this.pm.registerEvent(Type.ENTITY_TARGET, entityListener, Priority.Highest, this);

        // Spout PacketListener's
        this.packetManager.addListener(20, new HideMePacketListener(this));

        System.out.println(this.getDescription().getName() + " (v" + this.getDescription().getVersion() + ") enabled");
    }

    public void onDisable()
    {
        System.out.println(this.getDescription().getName() + " Disabled");
    }

    private void loadConfig()
    {
        this.config.load();
    }

    private void defaultConfig()
    {
        this.config.save();
    }

    public static void log(String msg)
    {
        log.log(Level.INFO, "[HideMe] " + msg);
    }

    public static void error(String msg)
    {
        log.log(Level.SEVERE, "[HideMe] " + msg);
    }

    public static void error(String msg, Throwable t)
    {
        log.log(Level.SEVERE, "[HideMe] " + msg, t);
    }

    public static void debug(String msg)
    {
        if (debugMode)
        {
            log("[debug] " + msg);
        }
    }

    public void addPlayerEntity(String toHide, String to)
    {
        Player playerToHide = this.server.getPlayer(toHide);
        Player hideFromPlayer = this.server.getPlayer(to);
        if (playerToHide == null || hideFromPlayer == null)
        {
            return;
        }
        this.addPlayerEntity(playerToHide, hideFromPlayer);
    }
    public void addPlayerEntity(Player add, Player to)
    {
        this.addPlayerEntity((CraftPlayer)add, (CraftPlayer)to);
    }
    public void addPlayerEntity(CraftPlayer add, CraftPlayer to)
    {
        this.addPlayerEntity(add.getHandle(), to.getHandle());
    }
    public void addPlayerEntity(EntityPlayer add, EntityPlayer to)
    {
        to.netServerHandler.sendPacket(new Packet20NamedEntitySpawn(add));
    }

    public void removePlayerEntity(String remove, String from)
    {
        Player playerToHide = server.getPlayer(remove);
        Player hideFromPlayer = server.getPlayer(from);
        if (playerToHide == null || hideFromPlayer == null)
        {
            return;
        }
        this.removePlayerEntity(playerToHide, hideFromPlayer);
    }
    public void removePlayerEntity(Player remove, Player from)
    {
        this.removePlayerEntity((CraftPlayer)remove, (CraftPlayer)from);
    }
    public void removePlayerEntity(CraftPlayer remove, CraftPlayer from)
    {
        this.removePlayerEntity(remove.getHandle(), from.getHandle());
    }
    public void removePlayerEntity(EntityPlayer remove, EntityPlayer from)
    {
        from.netServerHandler.sendPacket(new Packet29DestroyEntity(remove.id));
    }


    public void hide(Player player)
    {
        EntityPlayer playerEntity = ((CraftPlayer)player).getHandle();
        this.mojangServer.players.remove(playerEntity);

        FakePlayerQuitEvent playerQuit = new FakePlayerQuitEvent(player, ChatColor.YELLOW + player.getName() + " left the game.");
        this.server.getPluginManager().callEvent(playerQuit);
        this.server.broadcastMessage(String.valueOf(playerQuit.getQuitMessage()));

        for (EntityPlayer current : (List<EntityPlayer>)mojangServer.players)
        {
            if (!this.canSeeHiddens.contains(cserver.getPlayer(current)))
            {
                this.removePlayerEntity(playerEntity, current);
            }
        }

        for (Player current : this.hiddenPlayers)
        {
            if (current != player)
            {
                EntityPlayer currentEntity = ((CraftPlayer)current).getHandle();
                if (!this.canSeeHiddens.contains(cserver.getPlayer(currentEntity)))
                {
                    this.removePlayerEntity(playerEntity, currentEntity);
                }
            }
        }

        this.hiddenPlayers.add(player);
    }

    public void unhide(Player player)
    {
        EntityPlayer playerEntity = ((CraftPlayer)player).getHandle();

        FakePlayerJoinEvent playerJoin = new FakePlayerJoinEvent(player, ChatColor.YELLOW + player.getName() + " joined the game.");
        this.server.getPluginManager().callEvent(playerJoin);
        this.server.broadcastMessage(String.valueOf(playerJoin.getJoinMessage()));

        this.hiddenPlayers.remove(player);

        for (EntityPlayer current : (List<EntityPlayer>)mojangServer.players)
        {
            if (!this.canSeeHiddens.contains(cserver.getPlayer(current)))
            {
                this.addPlayerEntity(playerEntity, current);
                //current.netServerHandler.sendPacket(new Packet20NamedEntitySpawn(playerEntity));
            }
        }

        for (Player current : this.hiddenPlayers)
        {
            if (current != player && !this.canSeeHiddens.contains(current))
            {
                this.addPlayerEntity(playerEntity, ((CraftPlayer)current).getHandle());
            }
        }

        this.mojangServer.players.add(playerEntity);
    }

    public Player getHiddenPlayerByName(String name)
    {
        for (Player player : this.hiddenPlayers)
        {
            if (player.getName().equals(name))
            {
                return player;
            }
        }
        return null;
    }
}
