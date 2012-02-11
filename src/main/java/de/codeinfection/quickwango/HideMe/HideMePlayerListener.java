package de.codeinfection.quickwango.HideMe;

import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author CodeInfection
 */
public class HideMePlayerListener implements Listener
{
    protected HideMe plugin;

    public HideMePlayerListener(HideMe plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player playr = event.getPlayer();
        if (event instanceof FakePlayerJoinEvent)
        {
            for (Player player : this.plugin.canSeeHiddens)
            {
                if (playr != player)
                {
                    player.sendMessage(ChatColor.YELLOW + "Player " + ChatColor.GREEN + playr.getName() + ChatColor.YELLOW + " is now visible!");
                }
            }
            return;
        }
        for (Player hidden : this.plugin.hiddenPlayers)
        {
            hidden.sendMessage(event.getJoinMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void autoHide(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        if (Permissions.HIDE_AUTO.isAuthorized(player))
        {
            event.setJoinMessage(null);
            for (Player current : this.plugin.getServer().getOnlinePlayers())
            {
                if (!this.plugin.canSeeHiddens.contains(current))
                {
                    current.hidePlayer(player);
                }
            }
            this.plugin.mojangServer.players.remove(((CraftPlayer)player).getHandle());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void autoSeehiddens(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        if (Permissions.SEEHIDDENS_AUTO.isAuthorized(player) && !plugin.canSeeHiddens.contains(player))
        {
            plugin.canSeeHiddens.add(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player playr = event.getPlayer();
        if (event instanceof FakePlayerQuitEvent)
        {
            for (Player player : this.plugin.canSeeHiddens)
            {
                if (playr != player)
                {
                    player.sendMessage(ChatColor.YELLOW + "Player " + ChatColor.GREEN + playr.getName() + ChatColor.YELLOW + " is now hidden!");
                }
            }
            return;
        }
        
        if (this.plugin.hiddenPlayers.contains(playr))
        {
            for (Player current : this.plugin.canSeeHiddens)
            {
                current.sendMessage(event.getQuitMessage());
            }
            event.setQuitMessage(null);
            this.plugin.hiddenPlayers.remove(playr);
        }
        else
        {
            for (Player hidden : this.plugin.hiddenPlayers)
            {
                hidden.sendMessage(event.getQuitMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void pluginFix(PlayerQuitEvent event)
    {
        if (event instanceof FakePlayerQuitEvent)
        {
            return;
        }
        
        final Player player = event.getPlayer();
        if (this.plugin.hiddenPlayers.contains(player))
        {
            this.plugin.mojangServer.players.add(((CraftPlayer)player).getHandle());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event)
    {
        if (this.plugin.hiddenPlayers.contains(event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(PlayerChatEvent event)
    {
        Player player = event.getPlayer();
        Set<Player> recipients = event.getRecipients();
        if (this.plugin.canSeeHiddens.contains(player))
        {
            recipients.add(player);
        }
        else
        {
            recipients.addAll(this.plugin.hiddenPlayers);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTarget(EntityTargetEvent event)
    {
        final Entity entity = event.getEntity();
        if (entity instanceof Player)
        {
            final Player player = (Player)entity;
            if (this.plugin.hiddenPlayers.contains(player))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        if (!Permissions.DROP.isAuthorized(event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }
}
