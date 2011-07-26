package de.codeinfection.quickwango.HideMe;

import de.codeinfection.quickwango.HideMe.commands.SeehiddensCommand;
import de.codeinfection.quickwango.HideMe.commands.UnhideCommand;
import de.codeinfection.quickwango.HideMe.commands.ListhiddensCommand;
import de.codeinfection.quickwango.HideMe.commands.HideCommand;
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
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.scheduler.BukkitScheduler;

public class HideMe extends JavaPlugin
{
    protected static final Logger log = Logger.getLogger("Minecraft");
    public static boolean debugMode = true;

    protected final ArrayList<Player> hiddenPlayers = new ArrayList<Player>();
    public final ArrayList<Player> canSeeHiddens = new ArrayList<Player>();
    protected static PermissionHandler permissionHandler = null;
    
    public Server server;
    public CraftServer cserver;
    public ServerConfigurationManager mojangServer;
    protected PluginManager pm;
    protected Configuration config;
    protected File dataFolder;
    protected BukkitScheduler scheduler;
    protected Hider hider;
    protected final int hideInterval = 10;

    protected int hiderTaskId;

    public HideMe()
    {
        this.hiderTaskId = -1;
    }

    public void onEnable()
    {
        this.server = this.getServer();
        this.cserver = (CraftServer)this.server;
        this.mojangServer = this.cserver.getHandle();
        this.pm = this.server.getPluginManager();
        this.config = this.getConfiguration();
        this.dataFolder = this.getDataFolder();
        this.scheduler = this.server.getScheduler();
        this.hider = new Hider(this, mojangServer, cserver);

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

        this.activateHider();

        System.out.println(this.getDescription().getName() + " (v" + this.getDescription().getVersion() + ") enabled");
    }

    public void onDisable()
    {
        this.scheduler.cancelTasks(this);
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

    protected boolean activateHider()
    {
        if (this.hiderTaskId < 0 && this.hiddenPlayers.size() > 0)
        {
            this.hiderTaskId = this.scheduler.scheduleAsyncRepeatingTask(this, this.hider, 0, hideInterval);
            log("Activated the HiderTask");
            return (this.hiderTaskId > -1);
        }
        else
        {
            return false;
        }
    }

    public boolean deactivateHider()
    {
        if (this.hiderTaskId > -1)
        {
            this.scheduler.cancelTask(this.hiderTaskId);
            this.hiderTaskId = -1;
            log("Deactivated the HiderTask");
            return true;
        }
        else
        {
            return false;
        }
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

    public void addPlayerEntity(String toHide, String hideFrom)
    {
        Player playerToHide = this.server.getPlayer(toHide);
        Player hideFromPlayer = this.server.getPlayer(hideFrom);
        if (playerToHide == null || hideFromPlayer == null)
        {
            return;
        }
        this.addPlayerEntity(playerToHide, hideFromPlayer);
    }
    public void addPlayerEntity(Player toHide, Player hideFrom)
    {
        this.addPlayerEntity((CraftPlayer)toHide, (CraftPlayer)hideFrom);
    }
    public void addPlayerEntity(CraftPlayer toHide, CraftPlayer hideFrom)
    {
        this.addPlayerEntity(toHide.getHandle(), hideFrom.getHandle());
    }
    public void addPlayerEntity(EntityPlayer toHide, EntityPlayer hideFrom)
    {
        hideFrom.netServerHandler.sendPacket(new Packet20NamedEntitySpawn(toHide));
    }

    public void removePlayerEntity(String toHide, String hideFrom)
    {
        Player playerToHide = server.getPlayer(toHide);
        Player hideFromPlayer = server.getPlayer(hideFrom);
        if (playerToHide == null || hideFromPlayer == null)
        {
            return;
        }
        this.removePlayerEntity(playerToHide, hideFromPlayer);
    }
    public void removePlayerEntity(Player toHide, Player hideFrom)
    {
        this.removePlayerEntity((CraftPlayer)toHide, (CraftPlayer)hideFrom);
    }
    public void removePlayerEntity(CraftPlayer toHide, CraftPlayer hideFrom)
    {
        this.removePlayerEntity(toHide.getHandle(), hideFrom.getHandle());
    }
    public void removePlayerEntity(EntityPlayer toHide, EntityPlayer hideFrom)
    {
        hideFrom.netServerHandler.sendPacket(new Packet29DestroyEntity(toHide.id));
    }


    public void hide(Player player)
    {
        EntityPlayer playerEntity = ((CraftPlayer)player).getHandle();
        this.mojangServer.players.remove(playerEntity);

        /*try
        {
            for (EntityPlayer current : (List<EntityPlayer>)mojangServer.players)
            {
                if (!this.canSeeHiddens.contains(current.netServerHandler.getPlayer()))
                {
                    removePlayerEntity(playerEntity, current);
                }
            }

            for (Player current : this.hiddenPlayers)
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
        }*/

        FakePlayerQuitEvent playerQuit = new FakePlayerQuitEvent(player, ChatColor.YELLOW + player.getName() + " left the game.");
        this.server.getPluginManager().callEvent(playerQuit);
        this.server.broadcastMessage(String.valueOf(playerQuit.getQuitMessage()));

        this.addHidden(player);
    }

    public void unhide(Player player)
    {
        EntityPlayer playerEntity = ((CraftPlayer)player).getHandle();

        FakePlayerJoinEvent playerJoin = new FakePlayerJoinEvent(player, ChatColor.YELLOW + player.getName() + " joined the game.");
        this.server.getPluginManager().callEvent(playerJoin);
        this.server.broadcastMessage(String.valueOf(playerJoin.getJoinMessage()));

        this.removeHidden(player);

        for (EntityPlayer current : (List<EntityPlayer>)mojangServer.players)
        {
            current.netServerHandler.sendPacket(new Packet20NamedEntitySpawn(playerEntity));
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

    public boolean isHidden(Player player)
    {
        return this.hiddenPlayers.contains(player);
    }

    public void addHidden(Player player)
    {
        this.hiddenPlayers.add(player);
        this.activateHider();
    }

    public void removeHidden(Player player)
    {
        this.hiddenPlayers.remove(player);
        if (this.countHiddens() < 1)
        {
            this.deactivateHider();
        }
    }

    public List<Player> getHiddens()
    {
        return this.hiddenPlayers;
    }

    public int countHiddens()
    {
        return this.hiddenPlayers.size();
    }
}
