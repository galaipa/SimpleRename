package io.github.galaipa.sr;
//SIMPLE RENAME
//AUTHOR: GALAIPA


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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
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
        getServer().getPluginManager().registerEvents(new AnvilListener(), this);

//TRANSLATION
                copyTranslation("en");
                copyTranslation("es");
                copyTranslation("eu");
                copyTranslation("custom");
                copyTranslation("nl");
                copyTranslation("fr");
                copyTranslation("de");
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
        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage("[SimpleRename]" + "Commands can only be run by players");
            return true;
        }
            Player player = (Player)sender;
            PluginDescriptionFile pdfFile = this.getDescription();
            String version1 = pdfFile.getVersion();
        //AL ARGS
           /* StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++){
            sb.append(args[i]).append(" ");
            }String allArgs = sb.toString().trim();*/


//SR INFO 
    if (cmd.getName().equalsIgnoreCase("sr")&& (args.length < 1)||cmd.getName().equalsIgnoreCase("sr") && args[0].equalsIgnoreCase("info") ){ 
            sender.sendMessage(ChatColor.GREEN + "Simple Rename");
            sender.sendMessage(ChatColor.BLUE + "Author:"+ " " + ChatColor.GREEN + "Galaipa & EnergizerBEAST1");
            sender.sendMessage(ChatColor.BLUE + "Version:"+ " " +ChatColor.GREEN + version1);
            sender.sendMessage(ChatColor.BLUE + "BukkitDev:"+" " + ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/simple-rename/");
            sender.sendMessage(ChatColor.BLUE + "Metrics:"+" " + ChatColor.GREEN + "http://mcstats.org/plugin/SimpleRename");
            return true;
//SR help
    }else if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("help")  ){ 
            sender.sendMessage(ChatColor.GREEN + "Simple Rename Commands" + " v" + ChatColor.GREEN + version1);
            sender.sendMessage(ChatColor.BLUE + "/rename or /setname");
            sender.sendMessage(ChatColor.BLUE + "/relore or /setlore");
            sender.sendMessage(ChatColor.BLUE + "/addlore");
            sender.sendMessage(ChatColor.BLUE + "/sr book setAuthor/setTitle/unSign");
            sender.sendMessage(ChatColor.BLUE + "/sr copy/paste");
            sender.sendMessage(ChatColor.BLUE + "/sr characters");
            sender.sendMessage(ChatColor.BLUE + "/sr clear");
            sender.sendMessage(ChatColor.BLUE + "/sr reload");
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
         return true;
        }
          //UPDATE          
    }else if (cmd.getName().equalsIgnoreCase("sr") && args[0].equalsIgnoreCase("update") && player.hasPermission("sr.update")) {
        Updater updater = new Updater(this, 75680, getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
        sender.sendMessage(ChatColor.GREEN + "Update progress in the console");
            return true;
            
//SECURITY CHECK
        
    }else if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null) {
                 sender.sendMessage(ChatColor.RED +(getTranslation("4")));
                 return true;
            }
       
            for (String sab : getConfig().getStringList("BlackListID")) {
                    if (Material.matchMaterial(sab) == player.getItemInHand().getType()&& !player.hasPermission("sr.blacklist") ) {
                        sender.sendMessage(ChatColor.RED+(getTranslation("15")));
                        return true;
                    }
            }
//WORD BLACKLIST
           for (String s : args) {
            List<String> ex = getConfig().getStringList("BlackList");
            List<String> ex2 = new ArrayList<String>();
                    if(s.contains("&")){
                        s= ChatColor.translateAlternateColorCodes('&', s);
                        s = ChatColor.stripColor(s);
                    }
                    for(String a : ex){
                        ex2.add(a.toLowerCase());
                    }
                    s = s.replaceAll("[^a-zA-Z0-9]+", "");
                    if (ex2.contains(s.toLowerCase())&& !player.hasPermission("sr.blacklist") ) {
                        sender.sendMessage(ChatColor.RED+(getTranslation("14")) + ": " + s);
                        return true;
           }
                   } 
                    
    //DUPLICATE
        if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("duplicate")) {
            if (!player.hasPermission("sr.duplicate")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;
            }
            else {
             ItemStack item = player.getItemInHand();      
             player.getInventory().addItem(item);
             sender.sendMessage(ChatColor.GREEN+(getTranslation("10")));
            return true;
            }
            }
// BOOKS
        else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("book") ){
            if(player.getItemInHand().getType() != Material.WRITTEN_BOOK){
                sender.sendMessage(ChatColor.RED +getTranslation("16"));
                return true;
            }
            else if (!player.hasPermission("sr.book")){
                sender.sendMessage(ChatColor.RED+ getTranslation("6"));
            }
            else if(args[1].equalsIgnoreCase("setauthor") && (ordainketa(player,"BookPrice","5","BookXP"))){
              ItemStack liburua = player.getItemInHand();
              BookMeta meta = (BookMeta) liburua.getItemMeta();
              meta.setAuthor(Args(2,args));
              liburua.setItemMeta(meta);
              return true;
            }
            else if(args[1].equalsIgnoreCase("settitle")&& (ordainketa(player,"BookPrice","5","BookXP"))){
              ItemStack liburua = player.getItemInHand();
              BookMeta meta = (BookMeta) liburua.getItemMeta();
              meta.setTitle(Args(2,args));
              liburua.setItemMeta(meta);
              return true;
            }
            else if(args[1].equalsIgnoreCase("unsign")&& (ordainketa(player,"BookPrice","5","BookXP"))){
              ItemStack liburua = player.getItemInHand();
              BookMeta metaZaharra = (BookMeta) liburua.getItemMeta();
              ItemStack sinatugabea = new ItemStack(Material.BOOK_AND_QUILL, 1);
              BookMeta metaBerria = (BookMeta) sinatugabea.getItemMeta();
              metaBerria.setPages(metaZaharra.getPages());
              sinatugabea.setItemMeta(metaBerria);
              player.getInventory().setItem(player.getInventory().getHeldItemSlot(),sinatugabea);
              return true;
        }}
    //CLEAR
        else if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("clear")) {
            if (!player.hasPermission("sr.clear")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;
            }else if (ordainketa(player,"ClearPrice","13","ClearXP")){
            ItemStack item = player.getItemInHand();
            int slot = player.getInventory().getHeldItemSlot();
            item.setItemMeta(null);
            player.getInventory().removeItem(item);
            player.getInventory().setItem(slot, item);
            return true;
            
            }}
    //COPY
        else if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("copy") ) {
            if (!player.hasPermission("sr.copy")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;
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
            if (!player.hasPermission("sr.copy")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;
            }else if (ordainketa(player,"PastePrice","12","PasteXP")){
            ItemStack item1 = player.getItemInHand();
            int slot = player.getInventory().getHeldItemSlot();
            ItemMeta MetaData = copy.get(player.getName());  
                item1.setItemMeta(MetaData);
                player.getInventory().removeItem(item1);
                player.getInventory().setItem(slot, item1);
                return true;
            }}

   //ADD LORE
        else if (cmd.getName().equalsIgnoreCase("addlore")){
                if (!player.hasPermission("sr.lore")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;
            }else if (ordainketa(player,"Lprice","5", "LoreXP")){
              addLore(player,(Args(0,args)));
              return true; 
            }
        }
 // NAME NAME NAME NAME NAME NAME       
        else if(cmd.getName().equalsIgnoreCase("rename")){
            if (!player.hasPermission("sr.name")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));
                return true;

            }else if (args.length < 1) {
                sender.sendMessage(ChatColor.RED +(getTranslation("3")));
                return true;
                               
             }else if((Args(0,args)).contains("&") && !player.hasPermission("sr.color")) {
                sender.sendMessage(ChatColor.RED +(getTranslation("7")));
                return true; 
              
            }else if (ordainketa(player,"Nprice","5","NameXP")){
              setName(player,(Args(0,args)));
              return true; 
            } 
//LORE LORE LORE LORE LORE LORE LORE LORE LORE LORE LORE LORE

} else if (cmd.getName().equalsIgnoreCase("relore")){
            if (!player.hasPermission("sr.lore")) {
                sender.sendMessage(ChatColor.RED+(getTranslation("6")));

            }else if (args.length < 1) {
                sender.sendMessage(ChatColor.RED+(getTranslation("3")));

             }else if((Args(0,args)).contains("&") && !player.hasPermission("sr.color")) {
                sender.sendMessage(ChatColor.RED +(getTranslation("7")));
                return true;
             
            }else if (ordainketa(player,"Lprice","5","LoreXP")){
              setLore(player,(Args(0,args)));
              return true; 
            }
}else {
            sender.sendMessage(ChatColor.GREEN + "[Simple Rename]" + ChatColor.RED + " Unknown command");
            sender.sendMessage(ChatColor.GREEN + "[Simple Rename]" + ChatColor.RED + " Type '/sr help'to see allstop avaliable commands");
            return true; 
            
}

return true;
}
    
    public static String getTranslation(String path) {
            if (yaml.getString(path) == null)
            {
            path = "Message missing in the lang file. Contact Admin";
            return path;
            }
            String msg = yaml.getString(path);
            String color = ChatColor.translateAlternateColorCodes('&', msg);
            return color;
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
        
        public Boolean ordainketa(Player player, String zer,String mezua, String zer2){
              if (player.hasPermission("sr.free")) {
                    player.sendMessage(ChatColor.GREEN + (getTranslation(mezua)) + (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + ("0") + ("$")  + (" & ") +("0") + ("XP"));
                    return true;
                }
              else if ((getConfig().getBoolean("Economy"))) {
                   int Kantitatea = player.getInventory().getItemInHand().getAmount();
                   int XPprezioa = this.getConfig().getInt("XPprices."+ zer2);
                   int Prezioa = this.getConfig().getInt("Prices."+ zer);
                   int Guztira= Kantitatea * Prezioa ;
                   int XPGuztira= Kantitatea * XPprezioa ;
                   EconomyResponse r = econ.withdrawPlayer(player.getName(), Guztira); 
                    if ((!r.transactionSuccess()) && (player.getTotalExperience() < XPGuztira)){
                        player.sendMessage(ChatColor.RED + (getTranslation("8"))+ (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + Guztira + ("$")+ (" & ") + XPGuztira + ("XP"));
                        return false;
                    }else{
                        player.sendMessage(ChatColor.GREEN + (getTranslation(mezua)) + (" ") + ChatColor.RED + (getTranslation("9")) + (":") + (" ") + Guztira + ("$")+ (" & ") + XPGuztira + ("XP"));
                        setXP(player, player.getTotalExperience() - XPGuztira);
                        return true;
                    }  
            }else if ((getConfig().getBoolean("XPprices.Enable"))) {
                        int XPprezioa = this.getConfig().getInt("XPprices."+ zer2);
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
            } else{
                player.sendMessage(ChatColor.GREEN +(getTranslation(mezua)));
                return true;
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
        public void setName(Player player, String name){
              ItemStack item = player.getItemInHand(); 
              ItemMeta itemStackMeta = item.getItemMeta();
              itemStackMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));           
              item.setItemMeta(itemStackMeta);
        }
        public void setLore(Player player, String name){
            String[] splittedName = name.split("/n");    
            ItemStack itemStack = player.getItemInHand();
            List<String> lore = new ArrayList();
            for(String s : splittedName){
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            ItemMeta itemStackMeta = itemStack.getItemMeta();
            itemStackMeta.setLore(lore);
            itemStack.setItemMeta(itemStackMeta);
        }
        public void addLore(Player player, String name){
            String[] splittedName = name.split("/n");    
            ItemStack itemStack = player.getItemInHand();
            List<String> lore = itemStack.getItemMeta().getLore();
            if(lore == null){
                setLore(player, name);
            }
            else {
            for(String s : splittedName){
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            ItemMeta itemStackMeta = itemStack.getItemMeta();
            itemStackMeta.setLore(lore);
            itemStack.setItemMeta(itemStackMeta);
            }
            }
        public static String Args(int nondik, String[] args) {
            StringBuilder sb = new StringBuilder();
            for (int i = nondik; i < args.length; i++){
            sb.append(args[i]).append(" ");
            }String allArgs = sb.toString().trim();
            allArgs = ChatColor.translateAlternateColorCodes('&', allArgs);
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
        protected void setXP (Player p, int amount) {      
        p.setExp(0);
        p.setLevel(0);
        p.setTotalExperience(0);
        p.giveExp(amount);
       
        if (calcXPLevels(p.getLevel() + 1) == p.getTotalExperience()) {
                p.setLevel(p.getLevel() + 1);
                p.setExp(0);
        }
    }
                protected int calcXPLevels (int levels) {
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

        }
//SIMPLE RENAME
//AUTHOR: GALAIPA

    
