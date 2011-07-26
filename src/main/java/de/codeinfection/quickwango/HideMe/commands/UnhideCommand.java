package de.codeinfection.quickwango.HideMe.commands;

import de.codeinfection.quickwango.HideMe.HideMe;
import org.bukkit.ChatColor;
import org.bukkit.Server;
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
    protected final Server server;

    
    public UnhideCommand(HideMe plugin)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player target = null;
        if (args.length > 0)
        {
            if (sender instanceof Player && !HideMe.hasPermission((Player)sender, "HideMe.hide.others"))
            {
                sender.sendMessage("You are not allowed to unhide others.");
                return true;
            }

            for (Player player : this.plugin.getHiddens())
            {
                if (player.getName().equalsIgnoreCase(args[0]))
                {
                    target = player;
                    break;
                }
            }
            if (target == null)
            {
                sender.sendMessage("That player is not hidden.");
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
                sender.sendMessage("Only players can hide!");
                return true;
            }
        }
        
        if (sender == target && !HideMe.hasPermission(target, "HideMe.hide"))
        {
            target.sendMessage("You are not allowed to unhide!");
            return true;
        }
        if (this.plugin.isHidden(target))
        {
            this.plugin.unhide(target);
            target.sendMessage(ChatColor.GREEN + "You should now be completely visible again!");
            if (target != sender)
            {
                sender.sendMessage(ChatColor.GREEN + "He should now be completely visible again!");
            }
        }
        else
        {
            target.sendMessage("You are not hidden!");
        }
        return true;
    }
}
