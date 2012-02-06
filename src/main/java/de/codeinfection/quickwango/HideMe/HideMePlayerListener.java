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
import org.bukkit.event.player.*;

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
                    player.sendMessage(ChatColor.YELLOW + "Player " + ChatColor.GREEN + playr + ChatColor.YELLOW + " is now visible!");
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
            this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                public void run() {
                    plugin.hidePlayer(player, false);
                }
            }, 1L);
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
        this.plugin.mojangServer.players.add(((CraftPlayer)event.getPlayer()).getHandle());
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
