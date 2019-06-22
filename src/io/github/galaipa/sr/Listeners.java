
package io.github.galaipa.sr;


import java.util.HashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;


public class Listeners implements Listener {
    public static HashMap<Player, String> mobs = new HashMap<Player, String>();
    
    // Animal renaming
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (entity instanceof LivingEntity){
            if(mobs.get(player) != null){
                entity.setCustomName(mobs.get(player));
                mobs.remove(player);
            }
        }
    }

}

