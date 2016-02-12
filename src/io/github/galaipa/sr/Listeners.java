
package io.github.galaipa.sr;


import static io.github.galaipa.sr.Utils.Args;
import static io.github.galaipa.sr.Utils.getTranslation;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Listeners implements Listener {
    public static HashMap<Player, String> mobs = new HashMap<Player, String>();
//AnvilListener
    @EventHandler
public void AnvilListener(InventoryClickEvent event){
        HumanEntity p = event.getWhoClicked();
        Inventory inv = event.getInventory();
          if(inv.getType().equals(InventoryType.ANVIL)){
                    if (event.getSlotType() == InventoryType.SlotType.RESULT && event.getCurrentItem() != null) {
                            ItemStack a = event.getCurrentItem();
                            if(a.hasItemMeta() && a.getItemMeta().hasDisplayName()){
                                
                            }else{return;}
                            ItemMeta b = a.getItemMeta();
                            String c = b.getDisplayName();
                           /*  if(c.contains("&")){
                              if(p.hasPermission("sr.color")){
                                  c = ChatColor.translateAlternateColorCodes('&', c);
                                  b.setDisplayName(c);
                                  a.setItemMeta(b);
                                  event.setCurrentItem(a);
                              }else{
                                  p.sendMessage(ChatColor.GREEN + "[SimpleRename] " + ChatColor.RED + getTranslation("7"));
                              }*/
                           if(Utils.SecurityCheck((Player) p, c, "sr.name", 1,a)){
                                c = ChatColor.translateAlternateColorCodes('&', c);
                                b.setDisplayName(c);
                                a.setItemMeta(b);
                                event.setCurrentItem(a);
                          }
                    }
                }
          }
//Updater
    @EventHandler
public void UpdateListener(PlayerJoinEvent event){
  Player player = event.getPlayer();
  if(player.hasPermission("sr.update") && SimpleRename.update){
    player.sendMessage(ChatColor.GREEN + "An update is available: " + ChatColor.YELLOW + SimpleRename.name + ChatColor.GREEN +  " for " + SimpleRename.version + " available at " +  ChatColor.YELLOW + "http://goo.gl/hAf1QV");
    player.sendMessage(ChatColor.RED + "Type /sr update if you would like to update it automatically.");
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

