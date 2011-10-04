package de.codeinfection.quickwango.HideMe.commands;

import de.codeinfection.quickwango.HideMe.HideMe;
import net.minecraft.server.ServerConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

/**
 *
 * @author CodeInfection
 */
public class SeehiddensCommand implements CommandExecutor
{
    protected final HideMe plugin;
    protected final Server server;
    protected final ServerConfigurationManager serverConfigurationManager;


    public SeehiddensCommand(HideMe plugin)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.serverConfigurationManager = ((CraftServer)this.server).getHandle();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player)sender;
            if (player.hasPermission("HideMe.seehiddens"))
            {
                if (!this.plugin.canSeeHiddens.contains(player))
                {
                    for (Player current : this.plugin.hiddenPlayers)
                    {
                        if (current != null && current != player)
                        {
                            this.plugin.addPlayerEntity(current, player);
                        }
                    }
                    this.plugin.canSeeHiddens.add(player);

                    HideMe.log("Player '" + player + "' can now see hidden players!");

                    player.sendMessage(ChatColor.GREEN + "You should now be able to see other hidden players!");
                }
                else
                {
                    this.plugin.canSeeHiddens.remove(player);

                    for (Player current : this.plugin.hiddenPlayers)
                    {
                        if (current != null)
                        {
                            this.plugin.removePlayerEntity(current, player);
                        }
                    }

                    HideMe.log("Player '" + player.getName() + "' can NOT see hidden players anymore!");

                    player.sendMessage(ChatColor.GREEN + "You shouldn't see other hidden players anymore!");
                }
            }
            else
            {
                player.sendMessage("You are not allowed to see hidden players!");
            }
        }
        else
        {
            sender.sendMessage("Only players can see players!");
        }
        return true;
    }
}
