package de.codeinfection.quickwango.HideMe.commands;

import de.codeinfection.quickwango.HideMe.HideMe;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author CodeInfection
 */
public class ListhiddensCommand implements CommandExecutor
{
    protected final HideMe plugin;

    
    public ListhiddensCommand(HideMe plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player && !HideMe.hasPermission((Player)sender, "HideMe.listhiddens"))
        {
            sender.sendMessage("You are not allowed to list the hidden players!");
            return true;
        }

        int hiddenPlayerCount = this.plugin.countHiddens();
        if (hiddenPlayerCount == 0)
        {
            sender.sendMessage("There are no hidden players!");
            return true;
        }

        StringBuilder message = new StringBuilder();
        List<Player> hiddenPlayers = this.plugin.getHiddens();
        message.append("Hidden players: ");
        message.append(hiddenPlayers.get(0).getName());
        if (hiddenPlayerCount > 1)
        {
            for (int i = 1; i < hiddenPlayerCount; ++i)
            {
                message.append(", ");
                message.append(hiddenPlayers.get(i).getName());
            }
        }
        sender.sendMessage(message.toString());
        return true;
    }
}
