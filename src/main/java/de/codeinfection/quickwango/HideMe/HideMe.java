package de.codeinfection.quickwango.HideMe;

import de.codeinfection.quickwango.HideMe.commands.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ServerConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HideMe extends JavaPlugin
{
    private static Logger logger = null;
    public static boolean debugMode = true;

    public final List<Player> hiddenPlayers = Collections.synchronizedList(new ArrayList<Player>());
    public final List<Player> canSeeHiddens = Collections.synchronizedList(new ArrayList<Player>());
    
    public Server server;
    public CraftServer cserver;
    public ServerConfigurationManager mojangServer;
    protected PluginManager pm;
    protected File dataFolder;

    public HideMe()
    {}

    @Override
    public void onEnable()
    {
        logger = this.getLogger();
        this.server = this.getServer();
        this.cserver = (CraftServer)this.server;
        this.mojangServer = this.cserver.getHandle();
        this.pm = this.server.getPluginManager();
        this.dataFolder = this.getDataFolder();

        this.getCommand("hide").setExecutor(new HideCommand(this));
        this.getCommand("unhide").setExecutor(new UnhideCommand(this));
        this.getCommand("seehiddens").setExecutor(new SeehiddensCommand(this));
        this.getCommand("listhiddens").setExecutor(new ListhiddensCommand(this));
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
        EntityPlayer playerEntity = ((CraftPlayer)player).getHandle();
        this.mojangServer.players.remove(playerEntity);

        FakePlayerQuitEvent playerQuit = new FakePlayerQuitEvent(player, ChatColor.YELLOW + player.getName() + " left the game.");
        this.server.getPluginManager().callEvent(playerQuit);
        if (message)
        {
            this.server.broadcastMessage(String.valueOf(playerQuit.getQuitMessage()));
        }

        for (Player current : server.getOnlinePlayers())
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

        this.hiddenPlayers.add(player);
    }

    public void showPlayer(Player player)
    {
        EntityPlayer playerEntity = ((CraftPlayer)player).getHandle();

        FakePlayerJoinEvent playerJoin = new FakePlayerJoinEvent(player, ChatColor.YELLOW + player.getName() + " joined the game.");
        this.server.getPluginManager().callEvent(playerJoin);
        this.server.broadcastMessage(String.valueOf(playerJoin.getJoinMessage()));

        this.hiddenPlayers.remove(player);

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
