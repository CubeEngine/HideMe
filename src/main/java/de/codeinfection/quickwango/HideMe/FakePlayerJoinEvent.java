package main.java.de.codeinfection.quickwango.HideMe;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 *
 * @author CodeInfection
 */
public class FakePlayerJoinEvent extends PlayerJoinEvent
{
    public FakePlayerJoinEvent(Player who, String quitMessage)
    {
        super(who, quitMessage);
    }
}
