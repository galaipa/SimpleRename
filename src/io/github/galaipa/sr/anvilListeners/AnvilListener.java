
package io.github.galaipa.sr.anvilListeners;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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

public class AnvilListener implements Listener {

    private static char COLOR_CODE = '§';
    SimpleRename plugin;

    public AnvilListener(SimpleRename plugin) {
        this.plugin = plugin;
    }

    public static String recoverColorCodes(String newName, String oldName) {
        int iOld = 1;
        int iNew = 0;
        while (iOld < oldName.length() && iNew < newName.length()
                && ((int) oldName.charAt(iOld) == (int) newName.charAt(iNew))) {
            if (iOld + 1 != oldName.length() && oldName.charAt(iOld + 1) == COLOR_CODE)
                iOld++;
            iOld++;
            iNew++;
        }
        if (oldName.charAt(iOld - 1) == COLOR_CODE)
            iOld--;
        String name = oldName.substring(0, iOld) + newName.substring(iNew, newName.length());
        return name.replace(COLOR_CODE, '&');
    }

    @EventHandler
    public void anvilListener(PrepareAnvilEvent event) {
        AnvilInventory inv = (AnvilInventory) event.getInventory();

        if(!isRealAnvil(inv)) {
        	// ignore this inventory, it could be a dummy for a GUI plugin.
        	return;
        }
        
        try {
            if (inv.getItem(0) != null) {
                String oldName = inv.getItem(0).getItemMeta().getDisplayName();
                ItemStack newItem = event.getResult();
                if (newItem.getItemMeta() != null) {
                    ItemMeta newMeta = newItem.getItemMeta();
                    String newName = newMeta.getDisplayName();

                    if (!newName.equals(oldName)) {
                        if (oldName.contains(String.valueOf(COLOR_CODE))) {
                            newName = recoverColorCodes(inv.getRenameText(), oldName);
                        }
                        Methods.setName(newItem, newName);
                        event.setResult(newItem);
                        return;
                    }
                }
            }
        } catch (NullPointerException e) {
        	e.printStackTrace();
        }
    }

    @EventHandler
    public void anvilListenerGetResult(InventoryClickEvent event) {
        if(event.getInventory() instanceof AnvilInventory && !isRealAnvil((AnvilInventory) event.getInventory())) {
        	// ignore this inventory, it could be a dummy for a GUI plugin.
        	return;
        }
        
        try {
            Inventory inv = event.getInventory();
            HumanEntity p = event.getWhoClicked();
            if (inv.getType().equals(InventoryType.ANVIL) && event.getSlotType() == InventoryType.SlotType.RESULT) {
                if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
                    ItemStack item = event.getCurrentItem();
                    String newName = item.getItemMeta().getDisplayName();
                    String oldName = inv.getItem(0).getItemMeta().getDisplayName();
                    if (newName != null && !newName.equals(oldName)
                            && !plugin.checkEverything((Player) p, newName, null, 1, item)) {
                        p.closeInventory();
                    }
                }
            }
        } catch (NullPointerException e) {
        	e.printStackTrace();
        }
    }
    
    /**
     * Checks whether or not an anvil inventory is actually connected with a real world anvil
     * @param anvilInventory
     * @return
     */
    public boolean isRealAnvil(AnvilInventory anvilInventory) {
    	if (anvilInventory.getLocation().getBlockX() == 0 && anvilInventory.getLocation().getBlockY() == 0 && anvilInventory.getLocation().getBlockZ() == 0) {
    		if(anvilInventory.getLocation().getBlock().getType() != Material.ANVIL) {
    			return false;
    		}
    	}
    	return true;
    }
}
