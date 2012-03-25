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
public class SeehiddensCommand implements CommandExecutor
{
    protected final HideMe plugin;

    public SeehiddensCommand(HideMe plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player)sender;
            if (Permissions.SEEHIDDENS.isAuthorized(player))
            {
                if (!this.plugin.canSeeHiddens.contains(player))
                {
                    for (Player current : this.plugin.hiddenPlayers)
                    {
                        if (current != null && current != player)
                        {
                            player.showPlayer(current);
                        }
                    }
                    this.plugin.canSeeHiddens.add(player);

                    HideMe.log("Player '" + player.getName() + "' can now see hidden players!");

                    player.sendMessage(ChatColor.GREEN + "You should now be able to see other hidden players!");
                }
                else
                {
                    this.plugin.canSeeHiddens.remove(player);

                    for (Player current : this.plugin.hiddenPlayers)
                    {
                        if (current != null)
                        {
                            player.hidePlayer(current);
                        }
                    }

                    HideMe.log("Player '" + player.getName() + "' can NOT see hidden players anymore!");

                    player.sendMessage(ChatColor.RED + "You shouldn't see other hidden players anymore!");
                }
            }
            else
            {
                // actually: no permissions
                player.sendMessage("Unknown command. Type \"help\" for help.");
            }
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Only players can see players!");
        }
        return true;
    }
}
