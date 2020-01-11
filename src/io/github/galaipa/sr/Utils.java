package io.github.galaipa.sr;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {

    public static String removeColorCodes(String string) {
        return ChatColor.stripColor(string).replaceAll("[§&][0-9a-f-A-Fk-rK-R]", "");
    }

    public static String formatString(String string) {
        // The &r is added at the beginning to remove default formatting
        if (string.startsWith("&r")) {
            return ChatColor.translateAlternateColorCodes('&', string);
        } else {
            return ChatColor.translateAlternateColorCodes('&', "&r" + string);
        }
    }

    public static boolean checkPermissions(String perm, Player p, ItemStack item) {
        if (perm == null)
            return true;
        if (p.hasPermission(perm))
            return true;
        try {
            return p.hasPermission(perm + "." + item.getType().toString());
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkName(List<String> blackList, String message) {
        List<String> blackListLowerCase = listToLowerCase(blackList);
        for (String word : message.split(" ")) {
            word = word.toLowerCase();
            word = ChatColor.stripColor(word);
            word = word.replaceAll("[^a-zA-Z0-9]+", "");
            if (blackListLowerCase.contains(word))
                return false;
        }
        return true;
    }

    public static boolean checkItem(List<String> blackList, Player p, ItemStack item) {
        if (p.hasPermission("sr.blacklist"))
            return true;
        if (p.hasPermission("sr.blacklist." + item.getType().toString()))
            return true;

        for (String material : blackList) {
            if (Material.matchMaterial(material) == item.getType())
                return false;
        }
        return true;
    }

    public static List<String> listToLowerCase(List<String> list) {
        List<String> newList = new ArrayList<>();

        for (String s : list) {
            newList.add(s.toLowerCase());
        }
        return newList;
    }

    public static String extractArgs(int nondik, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = nondik; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String allArgs = sb.toString().trim();
        // CHARACTERS
        allArgs = allArgs.replace("[<3]", "\u2764");
        allArgs = allArgs.replace("[ARROW]", "\u279c");
        allArgs = allArgs.replace("[TICK]", "\u2714");
        allArgs = allArgs.replace("[X]", "\u2716");
        allArgs = allArgs.replace("[STAR]", "\u2605");
        allArgs = allArgs.replace("[POINT]", "\u25Cf");
        allArgs = allArgs.replace("[FLOWER]", "\u273f");
        // v2.1
        allArgs = allArgs.replace("[XD]", "\u263b");
        allArgs = allArgs.replace("[DANGER]", "\u26a0");
        allArgs = allArgs.replace("[MAIL]", "\u2709");
        allArgs = allArgs.replace("[ARROW2]", "\u27a4");
        allArgs = allArgs.replace("[ROUND_STAR]", "\u2730");
        allArgs = allArgs.replace("[SUIT]", "\u2666");
        allArgs = allArgs.replace("[+]", "\u2726");
        allArgs = allArgs.replace("[CIRCLE]", "\u25CF");
        allArgs = allArgs.replace("[SUN]", "\u2739");
        return allArgs;
    }

    protected static void setXP(Player p, int amount) {
        p.setExp(0);
        p.setLevel(0);
        p.setTotalExperience(0);
        p.giveExp(amount);

        if (calcXPLevels(p.getLevel() + 1) == p.getTotalExperience()) {
            p.setLevel(p.getLevel() + 1);
            p.setExp(0);
        }
    }

    protected static int calcXPLevels(int levels) {
        int xp = 0;

        for (int i = 1; i <= levels; i++) {
            if (i <= 16) {
                xp += 17;
            } else if (i > 16 && i <= 31) {
                xp += (i - 16) * 3 + 17;
            } else if (i > 31) {
                xp += (i - 31) * 7 + 62;
            }
        }

        return xp;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
