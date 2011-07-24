package de.codeinfection.quickwango.HideMe;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.ServerConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

public class HideMe extends JavaPlugin
{
    protected static final Logger log = Logger.getLogger("Minecraft");
    public static boolean debugMode = true;

    public final static ArrayList<Player> hiddenPlayers = new ArrayList<Player>();
    public final static ArrayList<Player> canSeeHiddens = new ArrayList<Player>();
    protected static PermissionHandler permissionHandler = null;
    
    protected static Server server;
    protected static ServerConfigurationManager mojangServer;
    protected PluginManager pm;
    protected Configuration config;
    protected File dataFolder;

    public HideMe()
    {
    }

    public void onEnable()
    {
        server = this.getServer();
        mojangServer = ((CraftServer)server).getHandle();
        this.pm = server.getPluginManager();
        this.config = this.getConfiguration();
        this.dataFolder = this.getDataFolder();

        this.dataFolder.mkdirs();
        // Create default config if it doesn't exist.
        if (!(new File(this.dataFolder, "config.yml")).exists())
        {
            this.defaultConfig();
        }
        this.loadConfig();

        Permissions permissions = (Permissions)this.pm.getPlugin("Permissions");
        if (permissions != null)
        {
            permissionHandler = permissions.getHandler();
        }

        this.getCommand("hide").setExecutor(new HideCommand(this));
        this.getCommand("unhide").setExecutor(new UnhideCommand(this));
        this.getCommand("seehiddens").setExecutor(new SeehiddensCommand(this));
        this.getCommand("listhiddens").setExecutor(new ListhiddensCommand(this));

        HideMePlayerListener playerListener = new HideMePlayerListener(this);
        this.pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Highest, this);
        this.pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Highest, this);
        this.pm.registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Priority.Highest, this);
        this.pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Lowest, this);

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

    public static boolean hasPermission(Player player, String permission)
    {
        return (player.isOp() || (permissionHandler != null && permissionHandler.has(player, permission)));
    }

    public static void addPlayerEntity(String toHide, String hideFrom)
    {
        Player playerToHide = server.getPlayer(toHide);
        Player hideFromPlayer = server.getPlayer(hideFrom);
        if (playerToHide == null || hideFromPlayer == null)
        {
            return;
        }
        addPlayerEntity(playerToHide, hideFromPlayer);
    }
    public static void addPlayerEntity(Player toHide, Player hideFrom)
    {
        addPlayerEntity((CraftPlayer)toHide, (CraftPlayer)hideFrom);
    }
    public static void addPlayerEntity(CraftPlayer toHide, CraftPlayer hideFrom)
    {
        addPlayerEntity(toHide.getHandle(), hideFrom.getHandle());
    }
    public static void addPlayerEntity(EntityPlayer toHide, EntityPlayer hideFrom)
    {
        hideFrom.netServerHandler.sendPacket(new Packet20NamedEntitySpawn(toHide));
    }

    public static void removePlayerEntity(String toHide, String hideFrom)
    {
        Player playerToHide = server.getPlayer(toHide);
        Player hideFromPlayer = server.getPlayer(hideFrom);
        if (playerToHide == null || hideFromPlayer == null)
        {
            return;
        }
        removePlayerEntity(playerToHide, hideFromPlayer);
    }
    public static void removePlayerEntity(Player toHide, Player hideFrom)
    {
        removePlayerEntity((CraftPlayer)toHide, (CraftPlayer)hideFrom);
    }
    public static void removePlayerEntity(CraftPlayer toHide, CraftPlayer hideFrom)
    {
        removePlayerEntity(toHide.getHandle(), hideFrom.getHandle());
    }
    public static void removePlayerEntity(EntityPlayer toHide, EntityPlayer hideFrom)
    {
        hideFrom.netServerHandler.sendPacket(new Packet29DestroyEntity(toHide.id));
    }


    public static boolean hide(Player player)
    {
        EntityPlayer playerEntity = ((CraftPlayer)player).getHandle();
        mojangServer.players.remove(playerEntity);

        try
        {
            for (EntityPlayer current : (List<EntityPlayer>)mojangServer.players)
            {
                if (!canSeeHiddens.contains(current.netServerHandler.getPlayer()))
                {
                    removePlayerEntity(playerEntity, current);
                }
            }

            for (Player current : HideMe.hiddenPlayers)
            {
                if (!canSeeHiddens.contains(current))
                {
                    if (current != player)
                    {
                        removePlayerEntity(player, current);
                    }
                }
            }
        }
        catch (Exception e)
        {
            // Readd the player on failure
            mojangServer.players.add(playerEntity);
            return false;
        }

        FakePlayerQuitEvent playerQuit = new FakePlayerQuitEvent(player, ChatColor.YELLOW + player.getName() + " left the game.");
        server.getPluginManager().callEvent(playerQuit);
        server.broadcastMessage(String.valueOf(playerQuit.getQuitMessage()));

        hiddenPlayers.add(player);
        return true;
    }

    public static boolean unhide(Player player)
    {
        EntityPlayer playerEntity = ((CraftPlayer)player).getHandle();
        boolean result = true;

        FakePlayerJoinEvent playerJoin = new FakePlayerJoinEvent(player, ChatColor.YELLOW + player.getName() + " joined the game.");
        server.getPluginManager().callEvent(playerJoin);
        server.broadcastMessage(String.valueOf(playerJoin.getJoinMessage()));

        hiddenPlayers.remove(player);

        try
        {
            for (EntityPlayer current : (List<EntityPlayer>)mojangServer.players)
            {
                current.netServerHandler.sendPacket(new Packet20NamedEntitySpawn(playerEntity));
            }

            for (Player current : hiddenPlayers)
            {
                if (current != player && !canSeeHiddens.contains(current))
                {
                    addPlayerEntity(playerEntity, ((CraftPlayer)current).getHandle());
                }
            }
        }
        catch (Exception e)
        {
            result = false;
        }

        mojangServer.players.add(playerEntity);
        return result;
    }
}
