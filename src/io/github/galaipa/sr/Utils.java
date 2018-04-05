
package io.github.galaipa.sr;


import static io.github.galaipa.sr.SimpleRename.getTranslation;

import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Utils {
    public static SimpleRename plugin;
    public Utils(SimpleRename instance) {
        plugin = instance;
    } 
   
    public static boolean checkEverything(Player p, String message, String perm, int lenght, ItemStack item){
        if(message == null){
            message = "";
        }
                
        if(perm != null && !p.hasPermission(perm)){ // CHEK PERMS
            p.sendMessage(ChatColor.RED+(getTranslation("6")));
            return false;
        }else if((message.contains("&") || message.contains(("§"))) && !p.hasPermission("sr.color")){ //CHECK COLOR PERM
            p.sendMessage(ChatColor.RED +(getTranslation("7")));
            return false;
        }else if(message.split(" ").length  < lenght) { // CHECK ARGUMENT LENGTH
            p.sendMessage(ChatColor.RED +(getTranslation("3")));
            return false;
        }else if(plugin.characterLimit != 0 && message.length() > plugin.characterLimit){
            p.sendMessage(ChatColor.RED +(getTranslation("19")) + plugin.characterLimit);
            return false;
        }else if(item != null &&item.getType().equals(Material.AIR)){ // CHECK ITEM IN HAND IS NOT AIR
            p.sendMessage(ChatColor.RED+(getTranslation("4")));
            return false;
        }else if(!p.hasPermission("sr.blacklist") && !checkName(message)){ //CHECK MESSAGE BLACKLIST
            p.sendMessage(ChatColor.RED+(getTranslation("14")) + ": " );
            return false;
        }else if(!p.hasPermission("sr.blacklist") && item != null && !checkItem(item)){ //CHECK ITEM BLACKLIST
            p.sendMessage(ChatColor.RED+(getTranslation("15")));
            return false;
        }
        return true;
        
    }
    
    public static boolean checkName(String message){
        List<String> blackList = plugin.getConfig().getStringList("BlackList");
        List<String> blackListLowerCase = listToLowerCase(blackList);

        for (String word : message.split(" ")) {
            word = word.toLowerCase();
            word = ChatColor.stripColor(word);
            word = word.replaceAll("[^a-zA-Z0-9]+", "");
            if(blackListLowerCase.contains(word))
                return false;
        }
        return true;
    }
    
    public static boolean checkItem(ItemStack item){
        List<String> blackList = plugin.getConfig().getStringList("BlackListID");
        
        for(String material : blackList){
            if(Material.matchMaterial(material) == item.getType())
                return false;
        }
        return true;      
    }
    
    
    public static List<String> listToLowerCase(List<String> list){
        List<String> newList = new ArrayList<>();
        
        for(String s : list){
            newList.add(s.toLowerCase());
        }
        return newList;
    }
    
    public static String Args(int nondik, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = nondik; i < args.length; i++){
            sb.append(args[i]).append(" ");
        }
        String allArgs = sb.toString().trim();
        //CHARACTERS
        allArgs = allArgs.replace("[<3]" , "\u2764");
        allArgs = allArgs.replace("[ARROW]" , "\u279c");
        allArgs = allArgs.replace("[TICK]" , "\u2714");
        allArgs = allArgs.replace("[X]" , "\u2716");
        allArgs = allArgs.replace("[STAR]" , "\u2605");
        allArgs = allArgs.replace("[POINT]" , "\u25Cf");
        allArgs = allArgs.replace("[FLOWER]" , "\u273f");
        //v2.1
        allArgs = allArgs.replace("[XD]" , "\u263b");
        allArgs = allArgs.replace("[DANGER]" , "\u26a0");
        allArgs = allArgs.replace("[MAIL]" , "\u2709");
        allArgs = allArgs.replace("[ARROW2]" , "\u27a4");
        allArgs = allArgs.replace("[ROUND_STAR]" , "\u2730");
        allArgs = allArgs.replace("[SUIT]" , "\u2666");
        allArgs = allArgs.replace("[+]" , "\u2726");
        allArgs = allArgs.replace("[CIRCLE]" , "\u25CF");
        allArgs = allArgs.replace("[SUN]" , "\u2739");
        return allArgs;
    }
    
    
        public static Boolean ordainketa(Player player, String zer,String mezua, String zer2){
              if (player.hasPermission("sr.free")) {
                    player.sendMessage(ChatColor.GREEN + (getTranslation(mezua)));
                    return true;
                }
              else if ((plugin.econEn && plugin.xpEn)) {
                   int Kantitatea = player.getInventory().getItemInHand().getAmount();
                   int XPprezioa = plugin.getConfig().getInt("XPprices."+ zer2);
                   int Prezioa = plugin.getConfig().getInt("Prices."+ zer);
                   int Guztira= Kantitatea * Prezioa ;
                   int XPGuztira= Kantitatea * XPprezioa ;
                   EconomyResponse r = plugin.econ.withdrawPlayer(player, Guztira); 
                    if ((!r.transactionSuccess()) || (player.getTotalExperience() < XPGuztira)){
                        player.sendMessage(ChatColor.RED + (getTranslation("8"))+ (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + Guztira + ("$")+ (" & ") + XPGuztira + ("XP"));
                        return false;
                    }else{
                        player.sendMessage(ChatColor.GREEN + (getTranslation(mezua)) + (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + Guztira + ("$")+ (" & ") + XPGuztira + ("XP"));
                        setXP(player, player.getTotalExperience() - XPGuztira);
                        return true;
                    }  
            }else if (plugin.xpEn) {
                        int XPprezioa = plugin.getConfig().getInt("XPprices."+ zer2);
                        int Kantitatea = player.getInventory().getItemInHand().getAmount();
                        int XPGuztira= Kantitatea * XPprezioa ;
                    if (player.getTotalExperience() < XPGuztira){
                        player.sendMessage(ChatColor.RED + (getTranslation("8"))+ (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + XPGuztira + ("XP"));
                        return false;
                    }else{
                        player.sendMessage(ChatColor.GREEN + (getTranslation(mezua)) + (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + XPGuztira + ("XP"));
                        setXP(player, player.getTotalExperience() - XPGuztira);
                        return true;
                    } 
            }else if (plugin.econEn) {
                    int Prezioa = plugin.getConfig().getInt("Prices."+ zer);
                    int Kantitatea = player.getInventory().getItemInHand().getAmount();
                    int Guztira= Kantitatea * Prezioa ;
                    EconomyResponse r = plugin.econ.withdrawPlayer(player, Guztira);    
                    if (!r.transactionSuccess()){
                        player.sendMessage(ChatColor.RED + (getTranslation("8"))+ (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + Guztira + ("$"));
                        return false;
                    }else{
                        player.sendMessage(ChatColor.GREEN + (getTranslation(mezua)) + (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + Guztira + ("$"));
                        return true;
                    } 
            } else{
                player.sendMessage(ChatColor.GREEN +(getTranslation(mezua)));
                return true;
            }
        }
protected static void setXP (Player p, int amount) {      
    p.setExp(0);
    p.setLevel(0);
    p.setTotalExperience(0);
    p.giveExp(amount);

    if (calcXPLevels(p.getLevel() + 1) == p.getTotalExperience()) {
            p.setLevel(p.getLevel() + 1);
            p.setExp(0);
    }
    }
    protected static int calcXPLevels (int levels) {
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



