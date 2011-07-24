package de.codeinfection.quickwango.HideMe;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
/**
 *
 * @author CodeInfection
 */
public class UnhideCommand implements CommandExecutor
{
    protected final Plugin plugin;
    protected final Server server;

    
    public UnhideCommand(Plugin plugin)
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

            for (Player player : HideMe.hiddenPlayers)
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
        
        if (HideMe.hasPermission(target, "HideMe.hide"))
        {
            if (HideMe.hiddenPlayers.contains(target))
            {
                if (HideMe.unhide(target))
                {
                    target.sendMessage(ChatColor.GREEN + "You should now be completely visible again!");
                    if (target != sender)
                    {
                        sender.sendMessage(ChatColor.GREEN + "He should now be completely visible again!");
                    }
                }
                else
                {
                    target.sendMessage(ChatColor.GREEN + "You may be invisible for others until reconnect.");
                }
            }
            else
            {
                target.sendMessage("You are not hidden!");
            }
        }
        else
        {
            target.sendMessage("You are not allowed to unhide!");
        }
        return true;
    }
}
