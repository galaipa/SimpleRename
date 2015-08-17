
package io.github.galaipa.sr;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class AnvilListener implements Listener {
    @EventHandler
public void onInventoryClick(InventoryClickEvent event){
        HumanEntity p = event.getWhoClicked();
        Inventory inv = event.getInventory();
          if(inv.getType().equals(InventoryType.ANVIL)){
                    if (event.getSlotType() == InventoryType.SlotType.RESULT && event.getCurrentItem() != null) {
                            ItemStack a = event.getCurrentItem();
                            if(a.hasItemMeta() && a.getItemMeta().hasDisplayName()){
                                
                            }else{return;}
                            ItemMeta b = a.getItemMeta();
                            String c = b.getDisplayName();
                             if(c.contains("&")){
                              if(p.hasPermission("sr.color")){
                                  c = ChatColor.translateAlternateColorCodes('&', c);
                                  b.setDisplayName(c);
                                  a.setItemMeta(b);
                                  event.setCurrentItem(a);
                              }
                          }
                    }
                }
          } 
    }

