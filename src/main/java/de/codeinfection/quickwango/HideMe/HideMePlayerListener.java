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
        Player player = event.getPlayer();
        for (Player current : HideMe.hiddenPlayers)
        {
            HideMe.removePlayerEntity(player, current);
            current.sendMessage(event.getJoinMessage());
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
        if (HideMe.hiddenPlayers.contains(player))
        {
            HideMe.hiddenPlayers.remove(player);
            for (Player current : HideMe.canSeeHiddens)
            {
                current.sendMessage(event.getQuitMessage());
            }
            event.setQuitMessage(null);
        }
        else
        {
            for (Player current : HideMe.hiddenPlayers)
            {
                current.sendMessage(event.getQuitMessage());
            }
        }
    }

    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event)
    {
        if (HideMe.hiddenPlayers.contains(event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event)
    {
        Player player = event.getPlayer();
        Set<Player> recipients = event.getRecipients();
        if (HideMe.canSeeHiddens.contains(player))
        {
            recipients.add(player);
        }
        else
        {
            recipients.addAll(HideMe.hiddenPlayers);
        }
    }
}
