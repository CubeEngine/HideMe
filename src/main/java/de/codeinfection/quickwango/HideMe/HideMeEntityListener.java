package de.codeinfection.quickwango.HideMe;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;


/**
 *
 * @author CodeInfection
 */
public class HideMeEntityListener extends EntityListener
{
    protected HideMe plugin;

    public HideMeEntityListener(HideMe plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onEntityTarget(EntityTargetEvent event)
    {
        final Entity entity = event.getEntity();
        if (entity instanceof Player)
        {
            final Player player = (Player)entity;
            if (this.plugin.hiddenPlayers.contains(player))
            {
                event.setCancelled(true);
            }
        }
    }
}
