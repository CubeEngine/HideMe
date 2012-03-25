package main.java.de.codeinfection.quickwango.HideMe.commands;

import main.java.de.codeinfection.quickwango.HideMe.HideMe;
import main.java.de.codeinfection.quickwango.HideMe.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author CodeInfection
 */
public class ListseehiddensCommand implements CommandExecutor
{
    protected final HideMe plugin;

    
    public ListseehiddensCommand(HideMe plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!Permissions.LISTSEEHIDDENS.isAuthorized(sender))
        {
            // actually: no permissions
            sender.sendMessage("Unknown command. Type \"help\" for help.");
            return true;
        }

        if (this.plugin.canSeeHiddens.isEmpty())
        {
            sender.sendMessage(ChatColor.RED + "There are no players who can see hiddens!");
        }
        else
        {
            sender.sendMessage("Players who can see hiddens:");
            for (Player player : this.plugin.canSeeHiddens)
            {
                sender.sendMessage(" - " + ChatColor.YELLOW + player.getName());
            }
            HideMe.log("'" + sender.getName() + "' listed hidden players!");
        }

        return true;
    }
}
