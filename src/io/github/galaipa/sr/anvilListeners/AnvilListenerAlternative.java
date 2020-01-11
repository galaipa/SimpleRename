
package io.github.galaipa.sr.anvilListeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.galaipa.sr.SimpleRename;

public class AnvilListenerAlternative implements Listener {
    SimpleRename plugin;

    public AnvilListenerAlternative(SimpleRename plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void anvilListenerAlternative(InventoryClickEvent event) {
        try {
            Inventory inv = event.getInventory();
            HumanEntity p = event.getWhoClicked();
            if (inv.getType().equals(InventoryType.ANVIL) && event.getSlotType() == InventoryType.SlotType.RESULT) {
                ItemStack item = event.getCurrentItem();
                ItemMeta meta = item.getItemMeta();
                String name = meta.getDisplayName();
                if (plugin.checkEverything((Player) p, name, null, 1, item)) {
                    name = ChatColor.translateAlternateColorCodes('&', name);
                    meta.setDisplayName(name);
                    item.setItemMeta(meta);
                    event.setCurrentItem(item);
                } else {
                    event.setCancelled(true);
                }

            }
        } catch (NullPointerException e) {

        }

    }
}
