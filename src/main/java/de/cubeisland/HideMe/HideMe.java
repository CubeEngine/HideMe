package de.cubeisland.HideMe;

import de.cubeisland.HideMe.commands.CanseehiddensCommand;
import de.cubeisland.HideMe.commands.HiddenCommand;
import de.cubeisland.HideMe.commands.HideCommand;
import de.cubeisland.HideMe.commands.ListhiddensCommand;
import de.cubeisland.HideMe.commands.ListseehiddensCommand;
import de.cubeisland.HideMe.commands.SeehiddensCommand;
import de.cubeisland.HideMe.commands.UnhideCommand;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HideMe extends JavaPlugin
{
    private static Logger  logger    = null;
    public static  boolean debugMode = true;

    public final Set<Player> hiddenPlayers = Collections.synchronizedSet(new HashSet<Player>());
    public final Set<Player> canSeeHiddens = Collections.synchronizedSet(new HashSet<Player>());

    public Field entityField;
    public  Server server;
    private List   players;

    public HideMe()
    {
    }

    @Override
    public void onEnable()
    {
        logger = this.getLogger();
        this.server = this.getServer();
        PluginManager pm = this.server.getPluginManager();
        Class serverClass = this.server.getClass();
        if (!serverClass.getSimpleName().equals("CraftServer"))
        {
            log("Your Bukkit server is not compatible with HideMe!.");
            log("HideMe will now disable itself...");
            pm.disablePlugin(this);
            return;
        }

        try
        {
            String craftEntityClassName = "org.bukkit.craftbukkit.entity.CraftEntity";
            Matcher mcVersionMatcher = Pattern.compile("\\.v(\\d_\\d(_\\d)?)\\.").matcher(serverClass.getName());
            if (mcVersionMatcher.find())
            {
                log("Loading classes for Minecraft version " + mcVersionMatcher.group(1).replace('_', '.'));
                craftEntityClassName = craftEntityClassName.replace("craftbukkit.", "craftbukkit.v" + mcVersionMatcher.group(1) + ".");
            }

            debug("entity class name: " + craftEntityClassName);
            this.entityField = Class.forName(craftEntityClassName).getDeclaredField("entity");
            this.entityField.setAccessible(true);
            if (!this.entityField.getType().getName().startsWith("net.minecraft.server") && !this.entityField.getType().getSimpleName().equals("Entity"))
            {
                throw new NoSuchFieldException();
            }

            Player.class.getDeclaredMethod("hidePlayer", Player.class);
            Field field = serverClass.getDeclaredField("server");
            field.setAccessible(true);
            Object server = field.get(this.server);
            if (!server.getClass().getSimpleName().equals("ServerConfigurationManager"))
            {
                throw new NoSuchFieldException();
            }
            serverClass = server.getClass();
            try
            {
                field = serverClass.getDeclaredField("players");
            }
            catch (NoSuchFieldException e)
            {
                field = serverClass.getSuperclass().getDeclaredField("players");
            }
            field.setAccessible(true);
            if (!List.class.isAssignableFrom(field.getType()))
            {
                throw new NoSuchFieldException();
            }
            this.players = (List)field.get(server);
        }
        catch (Exception e)
        {
            this.getLogger().log(Level.SEVERE, e.getLocalizedMessage(), e);
            log("Your CraftBukkit build is incompatible!");
            log("HideMe will now disable itself...");
            pm.disablePlugin(this);
            return;
        }

        this.getCommand("hide").setExecutor(new HideCommand(this));
        this.getCommand("unhide").setExecutor(new UnhideCommand(this));
        this.getCommand("hidden").setExecutor(new HiddenCommand(this));
        this.getCommand("seehiddens").setExecutor(new SeehiddensCommand(this));
        this.getCommand("listhiddens").setExecutor(new ListhiddensCommand(this));
        this.getCommand("canseehiddens").setExecutor(new CanseehiddensCommand(this));
        this.getCommand("listseehiddens").setExecutor(new ListseehiddensCommand(this));

        HideMePlayerListener playerListener = new HideMePlayerListener(this);
        
        pm.registerEvents(playerListener, this);

        log("Version " + this.getDescription().getVersion() + " is now enabled");
    }

    @Override
    public void onDisable()
    {
        this.entityField = null;
        this.players = null;
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
        try
        {
            this.players.add(entityField.get(player));
        }
        catch (IllegalAccessException ignored)
        {}
    }

    public void removePlayerFromList(final Player player)
    {
        try
        {
            this.players.remove(entityField.get(player));
        }
        catch (IllegalAccessException ignored)
        {}
    }
}
