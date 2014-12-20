package io.github.galaipa.sr;
//SIMPLE RENAME MAIN CLASS
//AUTHOR: GALAIPA
// DO NOT COPY WITH OUT PERMISSION PLEASE


import io.github.galaipa.sr.Updater.ReleaseType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public HashMap<String, ItemMeta > copy = new HashMap<String, ItemMeta>();
    ArrayList<String> list = new ArrayList<String>();
    public static Economy econ = null;
    public static final Logger log = Logger.getLogger("Minecraft");    
    public static boolean update = false;
    public static String name = "";
    public static Updater.ReleaseType type = null;
    public static String version = "";
    public static String link = "";
    public static String translation;
    public static YamlConfiguration yaml;
    private File languageFile;
   
    @Override
    public void onDisable() {
        PluginManager pluginManager = getServer().getPluginManager();
        log.info("SimpleRename disabled!");
    }

    @Override
    public void onEnable() {        
        log.info("SimpleRename enabled!");
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(new UpdateListener(), this);

//TRANSLATION
                copyTranslation("en");
                copyTranslation("es");
                copyTranslation("eu");
                copyTranslation("custom");
                copyTranslation("nl");
                copyTranslation("fr");
                translation = getConfig().getString("Language");
                languageFile = new File(getDataFolder() + File.separator + "lang"
                                + File.separator + translation + ".yml"); 
                //Settings default if language in config isn't found
                if (!languageFile.exists()) {
                        getLogger().info(
                                        "Could not find language file, language set to english");
                        translation = "en";
                }
                getLogger().info(translation);
                yaml = YamlConfiguration.loadConfiguration(languageFile);
                
//BLACKLIST (4.0)  
                  /* if (!getConfig().contains("BlackList")) {
                        this.getConfig().set("BlackList", list);
                        saveConfig();
                        list.add("example");
                        this.saveConfig();
                        }*/
        if ((getConfig().getBoolean("Economy"))){
            if (!setupEconomy() ) {
                log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        if ((getConfig().getBoolean("Updater"))){
            Updater updater = new Updater(this, 75680, getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
            update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
            name = updater.getLatestName();
            version = updater.getLatestGameVersion();
            type = updater.getLatestType();
            link = updater.getLatestFileLink();

                }
        //Metrics
        if ((getConfig().getBoolean("Metrics"))){
            try {
                Metrics metrics = new Metrics(this);
                metrics.start();
            } catch (IOException e) {
                // Failed to submit the stats :-(
            }
    }
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player player = (Player)sender;
            PluginDescriptionFile pdfFile = this.getDescription();
            String version1 = pdfFile.getVersion();
        //AL ARGS
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++){
            sb.append(args[i]).append(" ");
            }String allArgs = sb.toString().trim();
//BLACLIST
            List<String> ex = getConfig().getStringList("BlackList");

       //CHARACTERS
            //v2.0
            //v2.0
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
 
//SR INFO 1
    if (cmd.getName().equalsIgnoreCase("sr")&& (args.length < 1) ){ 
            sender.sendMessage(ChatColor.GREEN + "Simple Rename");
            sender.sendMessage(ChatColor.BLUE + "Author:"+ " " + ChatColor.GREEN + "Galaipa & EnergizerBEAST1");
            sender.sendMessage(ChatColor.BLUE + "Version:"+ " " +ChatColor.GREEN + version1);
            sender.sendMessage(ChatColor.BLUE + "BukkitDev:"+" " + ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/simple-rename/");
            sender.sendMessage(ChatColor.BLUE + "Metrics:"+" " + ChatColor.GREEN + "http://mcstats.org/plugin/SimpleRename");
            return true;
//SR CHARACTERS
    }else if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("characters") ||cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("character") ){
            sender.sendMessage(ChatColor.GREEN + "Special Characters List (SimpleRename)");
            sender.sendMessage(ChatColor.BLUE + "[<3]"+ " " +   "----->" + ChatColor.WHITE +"\u2764");
            sender.sendMessage(ChatColor.BLUE + "[ARROW]"+ " " +   "----->" + ChatColor.WHITE +"\u279c");
            sender.sendMessage(ChatColor.BLUE + "[TICK]"+ " " +   "----->" + ChatColor.WHITE +"\u2714");
            sender.sendMessage(ChatColor.BLUE + "[X]"+ " " +   "----->" + ChatColor.WHITE +"\u2716");
            sender.sendMessage(ChatColor.BLUE + "[STAR]"+ " " +   "----->" + ChatColor.WHITE +"\u2605");
            sender.sendMessage(ChatColor.BLUE + "[POINT]"+ " " +   "----->" + ChatColor.WHITE +"\u25Cf");
            sender.sendMessage(ChatColor.BLUE + "[FLOWER]"+ " " +   "----->" + ChatColor.WHITE +"\u273f");
            sender.sendMessage(ChatColor.BLUE + "[XD]"+ " " +   "----->" + ChatColor.WHITE +"\u263b");
            sender.sendMessage(ChatColor.BLUE + "[DANGER]"+ " " +   "----->" + ChatColor.WHITE +"\u26a0");
            sender.sendMessage(ChatColor.BLUE + "[MAIL]"+ " " +   "----->" + ChatColor.WHITE +"\u2709");
            sender.sendMessage(ChatColor.BLUE + "[ARROW2]"+ " " +   "----->" + ChatColor.WHITE +"\u27a4");
            sender.sendMessage(ChatColor.BLUE + "[ROUND_STAR]"+ " " +   "----->" + ChatColor.WHITE +"\u2730");
            sender.sendMessage(ChatColor.BLUE + "[SUIT]"+ " " +   "----->" + ChatColor.WHITE +"\u2666");
            sender.sendMessage(ChatColor.BLUE + "[+]"+ " " +   "----->" + ChatColor.WHITE +"\u2726");
            sender.sendMessage(ChatColor.BLUE + "[CIRCLE]"+ " " +   "----->" + ChatColor.WHITE +"\u25CF");
            sender.sendMessage(ChatColor.BLUE + "[SUN]"+ " " +   "----->" + ChatColor.WHITE +"\u2739");         
            return true;
            }
    //RELOAD
    else if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("reload")) {
        if (!player.hasPermission("sr.reload")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;
            }else{
         reloadConfig();
         translation = getConfig().getString("Language"); // LANGUAGE
         languageFile = new File(getDataFolder() + File.separator + "lang"
         + File.separator + translation + ".yml");
         getLogger().info(translation);
         yaml = YamlConfiguration.loadConfiguration(languageFile);
         sender.sendMessage(ChatColor.BLUE + "SimpleRename reloaded");
        }
    }
    //DUPLICATE
        else if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("duplicate")) {
            if (!player.hasPermission("sr.duplicate")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;
            }
            else if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null) {
                 sender.sendMessage(ChatColor.RED +(getTranslation("4")));
                 return true;
            }
            else {
             ItemStack item = player.getItemInHand();      
             player.getInventory().addItem(item);
             sender.sendMessage(ChatColor.GREEN+(getTranslation("10")));
            return true;
            }
            }
    //CLEAR
        else if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("clear")) {
            if (!player.hasPermission("sr.copy")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;
            }
            else if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null) {
                 sender.sendMessage(ChatColor.RED +(getTranslation("4")));
                 return true;
            }
            else {
            ItemStack item = player.getItemInHand();
            int slot = player.getInventory().getHeldItemSlot();
            item.setItemMeta(null);
            player.getInventory().removeItem(item);
            player.getInventory().setItem(slot, item);
            sender.sendMessage(ChatColor.GREEN+(getTranslation("13")));
            return true;
            
            }}
    //COPY
        else if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("copy") ) {
            if (!player.hasPermission("sr.copy")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;
            }
            if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null) {
                 sender.sendMessage(ChatColor.RED +(getTranslation("4")));
            }
            else {
            ItemStack item = player.getItemInHand();
             ItemMeta meta = item.getItemMeta();
             copy.put(player.getName(), meta);
             sender.sendMessage(ChatColor.GREEN+(getTranslation("11")));
            return true;
            
            }}

    //PASTE
        else if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("paste") && player.hasPermission("sr.copy")) {
            if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null) {
                 sender.sendMessage(ChatColor.RED +(getTranslation("4")));
            }
            else {
            ItemStack item1 = player.getItemInHand();
            int slot = player.getInventory().getHeldItemSlot();
            ItemMeta MetaData = copy.get(player.getName());  

                item1.setItemMeta(MetaData);
                player.getInventory().removeItem(item1);
                player.getInventory().setItem(slot, item1);
                sender.sendMessage(ChatColor.GREEN+(getTranslation("12")));
                return true;
            }}
  //UPDATE          
        else if (cmd.getName().equalsIgnoreCase("sr") && args[0].equalsIgnoreCase("update") && player.hasPermission("sr.update")) {
        Updater updater = new Updater(this, 75680, getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
        sender.sendMessage(ChatColor.GREEN + "Update progress in the console");
            return true;
            }
   //ADD LORE
        else if (cmd.getName().equalsIgnoreCase("addlore")){
                for (String s : args) {
                    if (ex.contains(s)&& !player.hasPermission("sr.blacklist") ) {
                        sender.sendMessage(ChatColor.RED+(getTranslation("14")));
                        return true;
                    } }
                if (!player.hasPermission("sr.lore")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;
            
            }else if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null) {
                 sender.sendMessage(ChatColor.RED +(getTranslation("4")));
                 return true;
            }else {
                ItemStack itemStack = player.getItemInHand();
                List<String> lore = itemStack.getItemMeta().getLore();
                if (lore == null){
                            
                            List<String> lore2 = new ArrayList();
                            lore2.add(ChatColor.translateAlternateColorCodes('&', allArgs));
                            ItemMeta itemStackMeta = itemStack.getItemMeta();
                            itemStackMeta.setLore(lore2);
                            itemStack.setItemMeta(itemStackMeta);
                            sender.sendMessage(ChatColor.GREEN +(getTranslation("5")));
                }else{
                            lore.add(ChatColor.translateAlternateColorCodes('&', allArgs));
                            ItemMeta itemStackMeta = itemStack.getItemMeta();
                            itemStackMeta.setLore(lore);
                            itemStack.setItemMeta(itemStackMeta);
                            sender.sendMessage(ChatColor.GREEN +(getTranslation("5")));
                return true;
                }
            }
        }
 // NAME NAME NAME NAME NAME NAME       
        else if(cmd.getName().equalsIgnoreCase("rename")){
                for (String s : args) {
                    if (ex.contains(s)&& !player.hasPermission("sr.blacklist") ) {
                        sender.sendMessage(ChatColor.RED+(getTranslation("14")));
                        return true;
                    } }
            if (!player.hasPermission("sr.name")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;

            }else if (args.length < 1) {
                sender.sendMessage(ChatColor.RED +(getTranslation("3")));
                return true;
                
            }else if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null) {
                sender.sendMessage(ChatColor.RED +(getTranslation("4")));
                return true;                
             }else if(allArgs.contains("&") && !player.hasPermission("sr.color")) {
                sender.sendMessage(ChatColor.RED +(getTranslation("7")));
                return true;

               
//Economy off                
            }else if (!(getConfig().getBoolean("Economy"))){
              ItemStack item = player.getItemInHand();           
              ItemMeta itemStackMeta = item.getItemMeta();
              itemStackMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', allArgs));           
              item.setItemMeta(itemStackMeta);        
              sender.sendMessage(ChatColor.GREEN +(getTranslation("5"))); 
              return true;
           

//Economy on             
            }else if ((getConfig().getBoolean("Economy"))) {
                if (player.hasPermission("sr.free")) {
              ItemStack item = player.getItemInHand();           
              ItemMeta itemStackMeta = item.getItemMeta();
              itemStackMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', allArgs));           
              item.setItemMeta(itemStackMeta); 
              sender.sendMessage(ChatColor.GREEN + (getTranslation("5")) + (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + ("0") + ("$") );
                        return true;
                }
                else  {
                   int Nprecio = this.getConfig().getInt("Nprice"); 
                   int cantidad = player.getInventory().getItemInHand().getAmount();
                   int precio = cantidad * Nprecio ;
                   EconomyResponse r = econ.withdrawPlayer(player.getName(), Nprecio * cantidad);                    
                    if (r.transactionSuccess()){
                        ItemStack item = player.getItemInHand();
                        ItemMeta itemStackMeta = item.getItemMeta();
                        itemStackMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', allArgs));
                        item.setItemMeta(itemStackMeta);
                        sender.sendMessage(ChatColor.GREEN + (getTranslation("5")) + (" ") + ChatColor.RED + (getConfig().getString("9")) + (":") + (" ") + precio + ("$") );
                        return true;
                    }else{
                        sender.sendMessage(ChatColor.RED + (getTranslation("8"))+ ChatColor.RED + (getTranslation("9")) + (":") + (" ") + precio + ("$") );
                        return true;
                    }             
                }		
} 
            
//LORE LORE LORE LORE LORE LORE LORE LORE LORE LORE LORE LORE

} else if (cmd.getName().equalsIgnoreCase("relore")){
                for (String s : args) {
                    if (ex.contains(s)&& !player.hasPermission("sr.blacklist") ) {
                        sender.sendMessage(ChatColor.RED+(getTranslation("14")));
                        return true;
                    } }
            if (!player.hasPermission("sr.lore")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));

            }else if (args.length < 1) {
                sender.sendMessage(ChatColor.RED+(getTranslation("3")));

            }else if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null) {
                sender.sendMessage(ChatColor.RED +(getTranslation("4")));
             }else if(allArgs.contains("&") && !player.hasPermission("sr.color")) {
                sender.sendMessage(ChatColor.RED +(getTranslation("7")));
                return true;

               

//Economy off                
            }else if (!(getConfig().getBoolean("Economy"))){    
            ItemStack itemStack = player.getItemInHand();
            List<String> lore = new ArrayList();
            lore.add(ChatColor.translateAlternateColorCodes('&', allArgs));
            ItemMeta itemStackMeta = itemStack.getItemMeta();
            itemStackMeta.setLore(lore);
            itemStack.setItemMeta(itemStackMeta);
            sender.sendMessage(ChatColor.GREEN +(getTranslation("5")));
            return true;                  

//Economy on             
            }else if ((getConfig().getBoolean("Economy"))) {
                if (player.hasPermission("sr.free")) {
                    ItemStack itemStack = player.getItemInHand();
                        List<String> lore = new ArrayList();
                        lore.add(ChatColor.translateAlternateColorCodes('&', allArgs));
                        ItemMeta itemStackMeta = itemStack.getItemMeta();
                        itemStackMeta.setLore(lore);
                        itemStack.setItemMeta(itemStackMeta);
                        sender.sendMessage(ChatColor.GREEN + (getTranslation("5")) + (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + ("0") + ("$") );
                        return true;
                }
                else { 
                   int Lprecio = this.getConfig().getInt("Lprice");
                   int cantidad = player.getInventory().getItemInHand().getAmount();
                   int precioa = cantidad * Lprecio ;
                   EconomyResponse r = econ.withdrawPlayer(player.getName(), Lprecio * cantidad);                    
                    if (r.transactionSuccess()){
                        ItemStack itemStack = player.getItemInHand();
                        List<String> lore = new ArrayList();
                        lore.add(ChatColor.translateAlternateColorCodes('&', allArgs));
                        ItemMeta itemStackMeta = itemStack.getItemMeta();
                        itemStackMeta.setLore(lore);
                        itemStack.setItemMeta(itemStackMeta);
                        sender.sendMessage(ChatColor.GREEN + (getTranslation("5")) + (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + precioa + ("$") );
                        return true;

                    }else{
                        sender.sendMessage(ChatColor.RED + (getTranslation("8"))+ ChatColor.RED + (getTranslation("9")) + (":") + (" ") + precioa + ("$") );
                        return true;
                    }             
                }               
            		
        } 
    //SR INFO 2
}else {
            sender.sendMessage(ChatColor.GREEN + "Simple Rename");
            sender.sendMessage(ChatColor.BLUE + "Author:"+ " " + ChatColor.GREEN + "Galaipa & EnergizerBEAST1");
            sender.sendMessage(ChatColor.BLUE + "Version:"+ " " +ChatColor.GREEN + version1);
            sender.sendMessage(ChatColor.BLUE + "BukkitDev:"+" " + ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/simple-rename/");
            sender.sendMessage(ChatColor.BLUE + "Metrics:"+" " + ChatColor.GREEN + "http://mcstats.org/plugin/SimpleRename");
            return true;
            
}




return true;
}
    
    public static String getTranslation(String path) {
        if((yaml.getString(path)) == null){
        path = "Message missing in the lang file. Contact Admin";
        return (path);
        } else
                return yaml.getString(path);
        }
 
        private void copy(InputStream in, File file) {
                try {
                        OutputStream out = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                        }
                        out.close();
                        in.close();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
 
        private void copyTranslation(String trans) {
                File file = new File(getDataFolder().getAbsolutePath() + File.separator
                                + "lang" + File.separator + trans + ".yml");
                if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        copy(getResource(trans + ".yml"), file);
                }
        }


}
//SIMPLE RENAME
//AUTHOR: GALAIPA
// DO NOT COPY WITH OUT PERMISSION PLEASE
    

