package de.codeinfection.quickwango.HideMe;

import net.minecraft.server.Packet20NamedEntitySpawn;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

/**
 *
 * @author CodeInfection
 */
public class HideMePacketListener implements PacketListener
{
    private final Server server;
    private final HideMe plugin;

    public HideMePacketListener(HideMe plugin)
    {
        this.server = Bukkit.getServer();
        this.plugin = plugin;
    }

    private Player getPlayer(String name)
    {
        Player player = server.getPlayerExact(name);
        if (player == null)
        {
            player = this.plugin.getHiddenPlayerByName(name);
        }
        return player;
    }

    public boolean checkPacket(Player player, MCPacket mcPacket)
    {
        @SuppressWarnings("deprecation")
        Object packet = mcPacket.getPacket();
        if (packet instanceof Packet20NamedEntitySpawn)
        {
            Packet20NamedEntitySpawn packet20 = (Packet20NamedEntitySpawn)packet;
            Player playerToSpawn = getPlayer(packet20.b);
            if (playerToSpawn != null)
            {
                return (!this.plugin.hiddenPlayers.contains(playerToSpawn) || this.plugin.canSeeHiddens.contains(player));
            }
        }
        return true;
    }
}
