package de.cubeisland.HideMe.commands;

import de.cubeisland.HideMe.HideMe;
import de.cubeisland.HideMe.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author CodeInfection
 */
public class UnhideCommand implements CommandExecutor
{
    protected final HideMe plugin;
    
    public UnhideCommand(HideMe plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player target = null;
        if (args.length > 0)
        {
            args[0] = args[0].trim().toLowerCase();

            for (Player player : this.plugin.hiddenPlayers)
            {
                if (player.getName().equalsIgnoreCase(args[0]))
                {
                    target = player;
                    break;
                }
            }
            if (target == null)
            {
                sender.sendMessage(ChatColor.RED + "That player is not hidden.");
                return true;
            }
        }
        else
        {
            if (sender instanceof Player)
            {
                target = (Player)sender;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Only players can hide!");
                return true;
            }
        }

        if (sender == target && !Permissions.HIDE.isAuthorized(sender))
        {
            // actually: no permissions
            sender.sendMessage("Unknown command. Type \"help\" for help.");
            return true;
        }
        if (sender != target && !Permissions.HIDE_OTHERS.isAuthorized(sender))
        {
            // actually: no permissions
            sender.sendMessage("Unknown command. Type \"help\" for help.");
            return true;
        }
        
        if (this.plugin.hiddenPlayers.contains(target))
        {
            this.plugin.showPlayer(target);
            
            target.sendMessage(ChatColor.GREEN + "You should now be completely visible again!");
            if (target != sender)
            {
                sender.sendMessage(ChatColor.GREEN + "He should now be completely visible again!");
            }

            HideMe.log("Player '" + target.getName() + "' is now visible again!");
        }
        else
        {
            if (target == sender)
            {
                sender.sendMessage(ChatColor.RED + "You are not hidden!");
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "He is not hidden!");
            }
        }
        return true;
    }
}
