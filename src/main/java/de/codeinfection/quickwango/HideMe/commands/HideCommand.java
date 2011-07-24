package de.codeinfection.quickwango.HideMe.commands;

import de.codeinfection.quickwango.HideMe.HideMe;
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
public class HideCommand implements CommandExecutor
{
    protected final Plugin plugin;
    protected final Server server;

    
    public HideCommand(Plugin plugin)
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
                sender.sendMessage("You are not allowed to hide others.");
                return true;
            }
            target = this.server.getPlayer(args[0]);
            if (target == null)
            {
                sender.sendMessage("Couldn't find that player.");
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
            if (!HideMe.hiddenPlayers.contains(target))
            {
                if (HideMe.hide(target))
                {
                    target.sendMessage(ChatColor.GREEN + "You should now be completely hidden :) Have Fun");
                    if (target != sender)
                    {
                        sender.sendMessage(ChatColor.GREEN + "He should now be completely hidden!");
                    }
                }
                else
                {
                    target.sendMessage(ChatColor.RED + "Failed to hide you!");
                }
            }
            else
            {
                target.sendMessage("You are already hidden!");
            }
        }
        else
        {
            target.sendMessage("You are not allowed to hide!");
        }
        return true;
    }
}
