
package io.github.galaipa.sr;


import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;


public class Listeners implements Listener {
    public static HashMap<Player, String> mobs = new HashMap<Player, String>();
    
    //Updater
    @EventHandler
    public void UpdateListener(PlayerJoinEvent event){
      Player player = event.getPlayer();
      if(player.hasPermission("sr.update") && (SimpleRename.updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE)){
        player.sendMessage(ChatColor.GREEN + "An update is available: " + ChatColor.YELLOW + SimpleRename.updater.getLatestName() + ChatColor.GREEN +  " for " + SimpleRename.updater.getLatestGameVersion() + " available at " +  ChatColor.YELLOW + "http://goo.gl/hAf1QV");
        //player.sendMessage(ChatColor.RED + "Type /sr update if you would like to update it automatically.");
      }
    }
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

