package de.codeinfection.quickwango.HideMe;

import net.minecraft.server.ServerConfigurationManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author CodeInfection
 */
public class SeehiddensCommand implements CommandExecutor
{
    protected final Plugin plugin;
    protected final Server server;
    protected final ServerConfigurationManager serverConfigurationManager;


    public SeehiddensCommand(Plugin plugin)
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
            if (HideMe.hasPermission(player, "HideMe.seehiddens"))
            {
                if (!HideMe.canSeeHiddens.contains(player))
                {
                    for (Player current : HideMe.hiddenPlayers)
                    {
                        if (current != null && current != player)
                        {
                            HideMe.addPlayerEntity(current, player);
                        }
                    }
                    HideMe.canSeeHiddens.add(player);

                    player.sendMessage(ChatColor.GREEN + "You should now be able to see other hidden players!");
                }
                else
                {
                    for (Player current : HideMe.hiddenPlayers)
                    {
                        if (current != null && current != player)
                        {
                            HideMe.removePlayerEntity(current, player);
                        }
                    }
                    HideMe.canSeeHiddens.remove(player);

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
