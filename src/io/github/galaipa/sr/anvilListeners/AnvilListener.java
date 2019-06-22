
package io.github.galaipa.sr.anvilListeners;

import io.github.galaipa.sr.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class AnvilListener implements Listener {
    
    
   @EventHandler
   public void anvilListener(PrepareAnvilEvent event){
       Inventory inv = event.getInventory();
       try{
            HumanEntity p = event.getViewers().get(0);
            
            String oldName = inv.getItem(0).getItemMeta().getDisplayName();
            ItemStack newItem = event.getResult();
            ItemMeta newMeta = newItem.getItemMeta();
            String newName = newMeta.getDisplayName();
            
            if(oldName != null && oldName.equals("§" + newName)) {
                 newMeta.setDisplayName(oldName);
            }else{
                if(oldName != null && oldName.startsWith("§") && newName.startsWith(oldName.substring(1))) //recover lost color code
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
}
