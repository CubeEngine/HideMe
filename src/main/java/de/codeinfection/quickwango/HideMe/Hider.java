package de.codeinfection.quickwango.HideMe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.server.ServerConfigurationManager;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

/**
 *
 * @author CodeInfection
 */
public class Hider implements Runnable
{
    protected final HideMe plugin;
    protected final ServerConfigurationManager mojangServer;
    protected final CraftServer cserver;
    protected final List<Player> hiddenPlayers;
    protected final ArrayList<Player> playerBuffer;

    public Hider(HideMe plugin, ServerConfigurationManager mojangServer, CraftServer cserver)
    {
        this.plugin = plugin;
        this.mojangServer = mojangServer;
        this.cserver = cserver;
        this.hiddenPlayers = plugin.getHiddens();

        this.playerBuffer = new ArrayList<Player>();
    }

    public void run()
    {
        playerBuffer.addAll(Arrays.asList(this.plugin.server.getOnlinePlayers()));
        playerBuffer.addAll(this.hiddenPlayers);

        for (Player hidden : this.hiddenPlayers)
        {
            for (Player current : playerBuffer)
            {
                if (hidden != current && !this.plugin.canSeeHiddens.contains(current))
                {
                    this.plugin.removePlayerEntity(hidden, current);
                }
            }
        }

        this.playerBuffer.clear();
    }

}
