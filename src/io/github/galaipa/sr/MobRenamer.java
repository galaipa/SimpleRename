
package io.github.galaipa.sr;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MobRenamer implements Listener {
    Map<Player, String> queue;

    public MobRenamer() {
        queue = new HashMap<>();
    }

    public void addToQueue(Player p, String name) {
        queue.put(p, ChatColor.translateAlternateColorCodes('&', name));
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (entity instanceof LivingEntity && (queue.get(player) != null)) {
            entity.setCustomName(queue.get(player));
            queue.remove(player);
        }
    }

}
