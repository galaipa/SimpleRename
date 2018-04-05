
package io.github.galaipa.sr;


import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Listeners implements Listener {
    public static HashMap<Player, String> mobs = new HashMap<Player, String>();
    
    @EventHandler
   public void AnvilListener(PrepareAnvilEvent event){
       Inventory inv = event.getInventory();
       try{
            HumanEntity p = event.getViewers().get(0);
            
            String oldName = inv.getItem(0).getItemMeta().getDisplayName();
            ItemStack newItem = event.getResult();
            ItemMeta newMeta = newItem.getItemMeta();
            String newName = newMeta.getDisplayName();
            
            if(oldName.equals("§" + newName)) {
                 newMeta.setDisplayName(oldName);
            }else{
                if(oldName.startsWith("§") && newName.startsWith(oldName.substring(1))) //recover lost color code
                    newName = "&" + newName;
                if(Utils.checkEverything((Player) p, newName, null, 1,newItem)){
                    newName = ChatColor.translateAlternateColorCodes('&', newName);
                    newMeta.setDisplayName(newName);
                }else{
                    p.closeInventory();
                }
                newName = ChatColor.translateAlternateColorCodes('&', newName);
                newMeta.setDisplayName(newName);
            }

            newItem.setItemMeta(newMeta);
            event.setResult(newItem);
            
       }catch (NullPointerException e){
               
        }
   }
//Updater
    @EventHandler
public void UpdateListener(PlayerJoinEvent event){
  Player player = event.getPlayer();
  if(player.hasPermission("sr.update") && SimpleRename.update){
    player.sendMessage(ChatColor.GREEN + "An update is available: " + ChatColor.YELLOW + SimpleRename.name + ChatColor.GREEN +  " for " + SimpleRename.version + " available at " +  ChatColor.YELLOW + "http://goo.gl/hAf1QV");
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

