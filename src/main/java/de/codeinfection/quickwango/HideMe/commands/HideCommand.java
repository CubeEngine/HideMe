package main.java.de.codeinfection.quickwango.HideMe.commands;

import main.java.de.codeinfection.quickwango.HideMe.HideMe;
import main.java.de.codeinfection.quickwango.HideMe.Permissions;
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
        Player target;
        if (args.length > 0)
        {
            args[0] = args[0].trim().toLowerCase();
            target = this.server.getPlayer(args[0]);
            if (target == null)
            {
                sender.sendMessage(ChatColor.RED + "Couldn't find that player.");
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
        else if (sender != target && !Permissions.HIDE_OTHERS.isAuthorized(sender))
        {
            // actually: no permissions
            sender.sendMessage("Unknown command. Type \"help\" for help.");
            return true;
        }
        
        if (!this.plugin.hiddenPlayers.contains(target))
        {
            this.plugin.hidePlayer(target);
            
            target.sendMessage(ChatColor.GREEN + "You should now be completely hidden :) Have Fun");
            if (target != sender)
            {
                sender.sendMessage(ChatColor.GREEN + "He should now be completely hidden!");
            }

            HideMe.log("Player '" + target.getName() + "' is now hidden!");
        }
        else
        {
            if (target == sender)
            {
                sender.sendMessage(ChatColor.RED + "You are already hidden!");
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "He is already hidden!");
            }
        }
        return true;
    }
}
