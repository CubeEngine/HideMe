package de.codeinfection.quickwango.HideMe.commands;

import de.codeinfection.quickwango.HideMe.HideMe;
import de.codeinfection.quickwango.HideMe.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

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
        if (sender instanceof Permissible && !Permissions.LISTSEEHIDDENS.isAuthorized((Permissible)sender))
        {
            sender.sendMessage(ChatColor.RED + "You are not allowed to list the players who can see hiddens!");
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
