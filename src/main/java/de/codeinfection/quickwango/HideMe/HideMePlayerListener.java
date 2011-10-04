package de.codeinfection.quickwango.HideMe;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author CodeInfection
 */
public class HideMePlayerListener extends PlayerListener
{
    protected HideMe plugin;

    public HideMePlayerListener(HideMe plugin)
    {
        this.plugin = plugin;
    }

    @Override
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

    @Override
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

    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event)
    {
        if (this.plugin.hiddenPlayers.contains(event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }

    @Override
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
}
