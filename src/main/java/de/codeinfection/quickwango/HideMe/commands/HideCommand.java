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
public class HideCommand implements CommandExecutor
{
    protected final HideMe plugin;
    protected final Server server;

    
    public HideCommand(HideMe plugin)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player target = null;
        if (args.length > 0)
        {
            args[0] = args[0].trim().toLowerCase();
            if (sender instanceof Player && !((Player)sender).hasPermission("HideMe.hide.others"))
            {
                sender.sendMessage("You are not allowed to hide others.");
                return true;
            }
            target = this.server.getPlayerExact(args[0]);
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
        
        if (sender == target && !target.hasPermission("HideMe.hide"))
        {
            target.sendMessage(ChatColor.RED + "You are not allowed to hide!");
            return true;
        }
        
        if (!this.plugin.hiddenPlayers.contains(target))
        {
            this.plugin.hide(target);
            target.sendMessage(ChatColor.GREEN + "You should now be completely hidden :) Have Fun");
            if (target != sender)
            {
                sender.sendMessage(ChatColor.GREEN + "He should now be completely hidden!");
            }

            HideMe.log("Player '" + target.getName() + "' is now hidden!");
        }
        else
        {
            target.sendMessage(ChatColor.RED + "You are already hidden!");
            if (target != sender)
            {
                sender.sendMessage(ChatColor.RED + "He is already hidden!");
            }
        }
        return true;
    }
}
