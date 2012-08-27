package de.cubeisland.HideMe;

import de.cubeisland.HideMe.commands.CanseehiddensCommand;
import de.cubeisland.HideMe.commands.HiddenCommand;
import de.cubeisland.HideMe.commands.HideCommand;
import de.cubeisland.HideMe.commands.ListhiddensCommand;
import de.cubeisland.HideMe.commands.ListseehiddensCommand;
import de.cubeisland.HideMe.commands.SeehiddensCommand;
import de.cubeisland.HideMe.commands.UnhideCommand;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ServerConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HideMe extends JavaPlugin
{
    private static Logger logger = null;
    public static boolean debugMode = true;

    public final Set<Player> hiddenPlayers = Collections.synchronizedSet(new HashSet<Player>());
    public final Set<Player> canSeeHiddens = Collections.synchronizedSet(new HashSet<Player>());
    
    public Server server;
    private ServerConfigurationManager mojangServer;
    private PluginManager pm;

    public HideMe()
    {}

    @Override
    public void onEnable()
    {
        logger = this.getLogger();
        this.server = this.getServer();
        this.mojangServer = ((CraftServer)this.server).getHandle();
        this.pm = this.server.getPluginManager();
        
        try
        {
            Player.class.getDeclaredMethod("hidePlayer", Player.class);
        }
        catch (NoSuchMethodException e)
        {
            log("Your CraftBukkit build is too old.");
            log("HideMe detected that you're using a build without Vanish API");
            log("HideMe will now disable itself...");
            this.pm.disablePlugin(this);
            return;
        }
        catch (SecurityException e)
        {}

        this.getCommand("hide").setExecutor(new HideCommand(this));
        this.getCommand("unhide").setExecutor(new UnhideCommand(this));
        this.getCommand("hidden").setExecutor(new HiddenCommand(this));
        this.getCommand("seehiddens").setExecutor(new SeehiddensCommand(this));
        this.getCommand("listhiddens").setExecutor(new ListhiddensCommand(this));
        this.getCommand("canseehiddens").setExecutor(new CanseehiddensCommand(this));
        this.getCommand("listseehiddens").setExecutor(new ListseehiddensCommand(this));

        HideMePlayerListener playerListener = new HideMePlayerListener(this);
        
        this.pm.registerEvents(playerListener, this);

        log("Version " + this.getDescription().getVersion() + " is now enabled");
    }

    @Override
    public void onDisable()
    {
        log("Version " + this.getDescription().getVersion() + " is now disabled");
    }

    public static void log(String msg)
    {
        logger.log(Level.INFO, msg);
    }

    public static void error(String msg)
    {
        logger.log(Level.SEVERE, msg);
    }

    public static void error(String msg, Throwable t)
    {
        logger.log(Level.SEVERE, msg, t);
    }

    public static void debug(String msg)
    {
        if (debugMode)
        {
            log("[debug] " + msg);
        }
    }

    public void hidePlayer(Player player)
    {
        this.hidePlayer(player, true);
    }

    public void hidePlayer(Player player, boolean message)
    {
        PlayerQuitEvent playerQuit = new FakePlayerQuitEvent(player, ChatColor.YELLOW + player.getName() + " left the game.");
        this.server.getPluginManager().callEvent(playerQuit);
        if (message)
        {
            this.server.broadcastMessage(String.valueOf(playerQuit.getQuitMessage()));
        }

        this.hiddenPlayers.add(player);
        this.removePlayerFromList(player);

        for (Player current : this.server.getOnlinePlayers())
        {
            if (!this.canSeeHiddens.contains(current))
            {
                current.hidePlayer(player);
            }
        }

        for (Player current : this.hiddenPlayers)
        {
            if (current != player && !this.canSeeHiddens.contains(current))
            {
                current.hidePlayer(player);
            }
        }
    }

    public void showPlayer(Player player)
    {
        this.hiddenPlayers.remove(player);
        this.addPlayerToList(player);

        FakePlayerJoinEvent playerJoin = new FakePlayerJoinEvent(player, ChatColor.YELLOW + player.getName() + " joined the game.");
        this.server.getPluginManager().callEvent(playerJoin);
        final String msg = playerJoin.getJoinMessage();
        if (msg != null)
        {
            this.server.broadcastMessage(msg);
        }

        for (Player current : server.getOnlinePlayers())
        {
            if (!this.canSeeHiddens.contains(current))
            {
                current.showPlayer(player);
            }
        }

        for (Player current : this.hiddenPlayers)
        {
            if (current != player && !this.canSeeHiddens.contains(current))
            {
                current.showPlayer(player);
            }
        }
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

    public void addPlayerToList(final Player player)
    {
        this.mojangServer.players.add(((CraftPlayer)player).getHandle());
    }

    public void removePlayerFromList(final Player player)
    {
        this.mojangServer.players.remove(((CraftPlayer)player).getHandle());
    }


}
