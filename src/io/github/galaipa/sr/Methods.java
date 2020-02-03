package io.github.galaipa.sr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Methods {

    // Rename
    public static void setName(ItemStack item, String name) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.setDisplayName(Utils.formatString(name));
        item.setItemMeta(itemStackMeta);
    }

    private static List<String> multiLineLore(String lore) {
        List<String> loreList = Arrays.asList(lore.split("\\\\n"));
        ListIterator<String> itr = loreList.listIterator();
        while (itr.hasNext()) {
            itr.set(Utils.formatString(itr.next()));
        }
        return loreList;
    }

    // Set lore
    public static void setLore(ItemStack itemStack, String name) {
        String lore = Utils.formatString(name);
        List<String> loreList = multiLineLore(lore);
        ItemMeta itemStackMeta = itemStack.getItemMeta();
        itemStackMeta.setLore(loreList);
        itemStack.setItemMeta(itemStackMeta);
    }

    // Add a new lore line
    public static void addLore(ItemStack itemStack, String loreString) {
        List<String> lore = itemStack.getItemMeta().getLore();

        if (lore == null) {
            setLore(itemStack, loreString);
        } else {
            List<String> newLore = multiLineLore(loreString);
            lore.addAll(newLore);
            ItemMeta itemStackMeta = itemStack.getItemMeta();
            itemStackMeta.setLore(lore);
            itemStack.setItemMeta(itemStackMeta);
        }
    }

    // Set book author
    public static void setBookAuthor(ItemStack book, String name) {
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setAuthor(name);
        book.setItemMeta(meta);
    }

    // Set book title
    public static void setBookTitle(ItemStack book, String name) {
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setDisplayName(null);
        meta.setTitle(name);
        book.setItemMeta(meta);
    }

    // Unsign book
    public static ItemStack unSignBook(ItemStack book) {
        BookMeta oldMeta = (BookMeta) book.getItemMeta();
        ItemStack unsigned;
        try {
            unsigned = new ItemStack(Material.WRITABLE_BOOK, 1);
        } catch (NoSuchFieldError e) {
            unsigned = new ItemStack(Material.matchMaterial("BOOK_AND_QUILL"), 1);
        }
        BookMeta newMeta = (BookMeta) unsigned.getItemMeta();
        newMeta.setPages(oldMeta.getPages());
        unsigned.setItemMeta(newMeta);
        return unsigned;

    }

    // Clear Meta
    public static void clearItem(ItemStack item) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.setDisplayName(null);
        itemStackMeta.setLore(null);
        item.setItemMeta(itemStackMeta);
    }

    // Duplicate item
    public static void duplicateItem(ItemStack item, int amount) {
        int amountInHand = item.getAmount();
        int result = amountInHand * amount;
        item.setAmount(result);
    }

    // Get specific amount of an item
    public static void getAmountItem(ItemStack item, int amount) {
        int max = item.getMaxStackSize();
        if (max < amount) {
            item.setAmount(max);
        } else {
            item.setAmount(amount);
        }
    }

    // Make item unbreakable
    public static void makeUnbreakable(ItemStack item, boolean unbreakable) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.setUnbreakable(unbreakable);
        item.setItemMeta(itemStackMeta);
    }

    // Copy / paste
    public static Map<String, ItemMeta> copy = new HashMap<>();

    // Copy
    public static void copyMeta(Player player) {
        ItemStack item = player.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        copy.put(player.getName(), meta);
    }

    // Paste
    public static void pasteMeta(Player player) {
        ItemStack item1 = player.getItemInHand();
        int slot = player.getInventory().getHeldItemSlot();
        ItemMeta metaData = copy.get(player.getName());
        item1.setItemMeta(metaData);
        player.getInventory().removeItem(item1);
        player.getInventory().setItem(slot, item1);
    }

    // Help info
    public static void helpInfo(Player sender, String version1) {
        sender.sendMessage(ChatColor.YELLOW + "Simple Rename Commands" + " v" + ChatColor.GREEN + version1);
        sender.sendMessage(ChatColor.BLUE + "/rename or /setname");
        sender.sendMessage(ChatColor.BLUE + "/relore or /setlore");
        sender.sendMessage(ChatColor.BLUE + "/addlore");
        sender.sendMessage(ChatColor.BLUE + "/sr characters");
        sender.sendMessage(ChatColor.BLUE + "/sr clear");
        sender.sendMessage(ChatColor.BLUE + "/sr book setAuthor/setTitle/unSign");
        sender.sendMessage(ChatColor.BLUE + "/sr getskull");
        sender.sendMessage(ChatColor.BLUE + "/sr copy/paste");
        sender.sendMessage(ChatColor.BLUE + "/sr duplicate");
        sender.sendMessage(ChatColor.BLUE + "/sr getamount");
        sender.sendMessage(ChatColor.BLUE + "/sr mob <name>");
        sender.sendMessage(ChatColor.BLUE + "/sr reload");
        sender.sendMessage(ChatColor.BLUE + "/removelore <lineN>");
        sender.sendMessage(ChatColor.BLUE + "/sr hideflags");
        sender.sendMessage(ChatColor.BLUE + "/sr glow/unglow");
        sender.sendMessage(ChatColor.BLUE + "/sr breakable/unbreakable");
    }

    // Get Skull
    public static ItemStack getSkull(String owner) {
        ItemStack skull = null;
        try {
            skull = new ItemStack(Material.PLAYER_HEAD, 1);
        } catch (NoSuchFieldError e) {
            skull = new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1);
        }
        skull.setDurability((short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(owner);
        skull.setItemMeta(meta);
        return skull;
    }

    // Rename mobs
    public static void renameMobs(Player p, String name) {
        Listeners.mobs.put(p, ChatColor.translateAlternateColorCodes('&', name));
    }

    public static void glowItem(ItemStack item) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.addEnchant(Enchantment.LURE, 0, true);
        itemStackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemStackMeta);
    }

    public static void unGlowItem(ItemStack item) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.removeEnchant(Enchantment.LURE);
        item.setItemMeta(itemStackMeta);
    }

    public static void hideFlags(ItemStack item) {
        ItemMeta itemStackMeta = item.getItemMeta();
        itemStackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStackMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStackMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(itemStackMeta);
    }

    public static void removeLore(ItemStack item, int n) {
        ItemMeta itemStackMeta = item.getItemMeta();
        if (itemStackMeta.hasLore() && (n != -1)) {
            List<String> list = itemStackMeta.getLore();
            if (list.size() >= (n + 1)) {
                list.remove(n);
                itemStackMeta.setLore(list);
            }
        } else {
            itemStackMeta.setLore(null);
        }
        item.setItemMeta(itemStackMeta);
    }

}
