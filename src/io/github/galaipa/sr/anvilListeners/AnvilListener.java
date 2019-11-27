
package io.github.galaipa.sr.anvilListeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.galaipa.sr.Methods;
import io.github.galaipa.sr.SimpleRename;
import io.github.galaipa.sr.Utils;


public class AnvilListener implements Listener {
    
	private static char COLOR_CODE = '§';
	
    public static String recoverColorCodes(String newName, String oldName) {
        int iOld = 1;
        int iNew = 0;
        while(iOld < oldName.length() && iNew < newName.length() && ((int) oldName.charAt(iOld) == (int) newName.charAt(iNew))) {
        	if(iOld+1 != oldName.length() && oldName.charAt(iOld+1) == COLOR_CODE) iOld++;
        	iOld++;
        	iNew++;
        }
        if(oldName.charAt(iOld-1) == COLOR_CODE) iOld--;
        String name = oldName.substring(0,iOld) + newName.substring(iNew,newName.length());
    	return name.replace(COLOR_CODE, '&');
    }
    
   @EventHandler
   public void anvilListener(PrepareAnvilEvent event){
	   if(SimpleRename.anvilFeatures) {
		   AnvilInventory inv = (AnvilInventory) event.getInventory();
	       try{ 
	    	   if(inv.getItem(0) != null) {
	               String oldName = inv.getItem(0).getItemMeta().getDisplayName();
	               ItemStack newItem = event.getResult();
	               if(newItem.getItemMeta() != null) {
	                   ItemMeta newMeta = newItem.getItemMeta();
	                   String newName = newMeta.getDisplayName();
	                   
	                   if(!newName.equals(oldName)) {
	                       if(oldName.contains(String.valueOf(COLOR_CODE))) {
	                       	newName = recoverColorCodes(inv.getRenameText(),oldName);
	                       }
	                       Methods.setName(newItem, newName);
	                       event.setResult(newItem); 	
	                       return;
	                   }
	               }
	    	   }  
	       }catch (NullPointerException e){}
	   }
   }
   
   @EventHandler
   public void anvilListenerGetResult(InventoryClickEvent event){
	   if(SimpleRename.anvilFeatures) {
		   try {
			   Inventory inv = event.getInventory();
		       HumanEntity p = event.getWhoClicked();
		       if(inv.getType().equals(InventoryType.ANVIL) && event.getSlotType() == InventoryType.SlotType.RESULT){
		    	   if(event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
			           ItemStack item = event.getCurrentItem();
			           String newName = item.getItemMeta().getDisplayName();
			           String oldName = inv.getItem(0).getItemMeta().getDisplayName();
			           if(newName != null && !newName.equals(oldName) && !Utils.checkEverything((Player) p, newName, null, 1,item)) {
			        	   p.closeInventory();
			           }   
		    	   }
		       }		   
		   }catch (NullPointerException e) {}
	   }
   }
}
