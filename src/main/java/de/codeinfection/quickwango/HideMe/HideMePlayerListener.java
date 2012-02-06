package de.codeinfection.quickwango.HideMe;

import java.util.Set;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChatEvent;
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
        if (event instanceof FakePlayerJoinEvent)
        {
            return;
        }
        for (Player hidden : this.plugin.hiddenPlayers)
        {
            hidden.sendMessage(event.getJoinMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        if (event instanceof FakePlayerQuitEvent)
        {
            return;
        }
        Player player = event.getPlayer();
        if (this.plugin.hiddenPlayers.contains(player))
        {
            for (Player current : this.plugin.canSeeHiddens)
            {
                current.sendMessage(event.getQuitMessage());
            }
            event.setQuitMessage(null);
            this.plugin.hiddenPlayers.remove(player);
        }
        else
        {
            for (Player hidden : this.plugin.hiddenPlayers)
            {
                hidden.sendMessage(event.getQuitMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pluginFix(PlayerQuitEvent event)
    {
        if (event instanceof FakePlayerQuitEvent)
        {
            return;
        }
        this.plugin.mojangServer.players.add(((CraftPlayer)event.getPlayer()).getHandle());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event)
    {
        if (this.plugin.hiddenPlayers.contains(event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
}
