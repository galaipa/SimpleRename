
package io.github.galaipa.sr;


import static io.github.galaipa.sr.Utils.Args;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class SimpleRename extends JavaPlugin{
    public static final Logger log = Logger.getLogger("Minecraft");
    public static Updater updater;

    public static Economy econ;
    public static boolean econEn, xpEn;
    
    public static int characterLimit;
    public static String prefix;
    
    
    public static String language;
    public static YamlConfiguration messages;
    
    
    @Override
    public void onDisable() {
        log.info("SimpleRename disabled!");
    }
    
    @Override
    public void onEnable() {    
        //Load configuration file
        getConfig().options().copyDefaults(true);
        saveConfig();
        // Register events
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        //Load translations
        language = getConfig().getString("Language");
        messages = loadTranslation(language);
        // Load economy
        econEn = getConfig().getBoolean("Economy");
        if (econEn){
            if(!setupEconomy()){
                econEn = false;
                log.info("[SimpleRename] Economy disabled, Vault not found!");
            }   
        }             
        // Load updater
        if ((getConfig().getBoolean("Updater")))
            updater = new Updater(this, 75680, getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
            
        // Load metrics
         if (getConfig().getBoolean("Metrics"))
            new MetricsLite(this); 
        // Other config
        xpEn = getConfig().getBoolean("XPprices.Enable");
        characterLimit = getConfig().getInt("CharacterLimit");
        prefix = getConfig().getString("Prefix");
        
        log.info("SimpleRename enabled!");
    }
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        //Block console commands
        if(sender instanceof ConsoleCommandSender){
            if(args.length == 1 && cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("reload") ){
                reloadConfig();
                onEnable();
                sender.sendMessage("[SimpleRename]" + "Plugin reloaded");
                return true;
            }else{
                sender.sendMessage("[SimpleRename]" + "Commands can only be run by players");
                return true; 
            }
        }
        Player player = (Player)sender;
        if(cmd.getName().equalsIgnoreCase("removelore")){
            if(Utils.checkEverything(player, null, "sr.removeLore", 0, player.getItemInHand())){
                if(args.length != 0 && Utils.isInt(args[0])){
                    Methods.removeLore(player, Integer.parseInt(args[0])-1);
                }else Methods.removeLore(player, -1);
                return true;
            }
            return true;
        }
        else if(args.length <1){
            PluginDescriptionFile pdfFile = this.getDescription();
            String version1 = pdfFile.getVersion();
            Methods.helpInfo(player,version1);
            return true;
                }
        // Item Rename
        if(cmd.getName().equalsIgnoreCase("rename")){
           if(Utils.checkEverything(player, Args(0,args), "sr.name", 1,player.getItemInHand())){
               if (Utils.ordainketa(player,"Nprice","5","NameXP")){
                    Methods.setName(player,(Args(0,args)));
                       }
           }
           return true;
                }
        // Add Lore
        else if (cmd.getName().equalsIgnoreCase("addlore")){
           if(Utils.checkEverything(player, Args(0,args), "sr.lore", 1, player.getItemInHand())){
               if (Utils.ordainketa(player,"Lprice","5", "LoreXP")){
                    Methods.addLore(player,(Args(0,args)));
                       }
           }
           return true;
                }
        // Set Lore (One line)
        else if (cmd.getName().equalsIgnoreCase("relore")){
           if(Utils.checkEverything(player, Args(0,args), "sr.lore", 1, player.getItemInHand())){
               if (Utils.ordainketa(player,"Lprice","5", "LoreXP")){
                    Methods.setLore(player,(Args(0,args)));
                       }
           }return true;
                }
        // Books (SetAuthor,SetTitle and UnSign)
        else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("book") ){
            if(player.getItemInHand().getType() != Material.WRITTEN_BOOK){
                sender.sendMessage(ChatColor.RED + getTranslation("16"));
                return true;
            }else if(Utils.checkEverything(player, Args(2,args), "sr.book", 0, null)){
                if(Utils.ordainketa(player,"BookPrice","5","BookXP")){
                    if(args[1].toLowerCase().equalsIgnoreCase("setauthor")){
                        Methods.setBookAuthor(player, Args(2,args));
                            }
                    else if(args[1].toLowerCase().equalsIgnoreCase("settitle")){
                        Methods.setBookTitle(player, Args(2,args));
                    }
                    else if(args[1].toLowerCase().equalsIgnoreCase("unsign")){
                        Methods.unSignBook(player);
                    }
            }
        }
            return true;
    }
        // Clear
        else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("clear") ){
           if(Utils.checkEverything(player, null, "sr.clear", 0, player.getItemInHand())){
               if (Utils.ordainketa(player,"ClearPrice","13","ClearXP")){
                    Methods.clearItem(player);
                       }
           }
           return true;
            }
        // Duplicate
        else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("duplicate") ){
           if(Utils.checkEverything(player, null, "sr.duplicate", 0, player.getItemInHand())){
               if(args.length >= 2 && Utils.isInt(args[1])){
                   Methods.duplicateItem(player,Integer.parseInt(args[1]));
                   return true;
               }else{
                   Methods.duplicateItem(player,2);
                   return true;
               }

           }
           return true;
            }
        // Get Amount
        else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("getAmount") ){
           if(Utils.checkEverything(player, null, "sr.duplicate", 0, player.getItemInHand())){
               if(args.length >= 2 && Utils.isInt(args[1])){
                   Methods.getAmount(player,Integer.parseInt(args[1]));
                   return true;
               }else{
                    Methods.getAmount(player,2);
               }
           }
           return true;
            }
        //Copy
        else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("copy") ){
            if(Utils.checkEverything(player, player.getItemInHand().getItemMeta().getDisplayName(), "sr.copy", 1, player.getItemInHand())){
                Methods.copyMeta(player);
            }
            return true;
        }
        //Paste
        else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("paste") ){
            if(Utils.checkEverything(player, null, "sr.copy", 1, player.getItemInHand())){
                if (Utils.ordainketa(player,"PastePrice","12","PasteXP")){
                    Methods.pasteMeta(player);
                    return true;
                }
             }
            return true;
        //Reload
        }else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("reload") ){
            if(Utils.checkEverything(player, null, "sr.reload", 1, null)){
                reloadConfig();
                onEnable();
                player.sendMessage(ChatColor.BLUE + "SimpleRename reloaded"); //OTHER RELOAD COMMAND FOR CONSOLE
                return true;
            }
            return true;
        //Get Skull
        }else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].toLowerCase().equalsIgnoreCase("getskull") ){
            if(Utils.checkEverything(player, Args(0,args), "sr.skull", 2, null)){
                    Methods.getSkull(player, args[1]);
                    return true;
                }
         // Rename mobs
        }else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].toLowerCase().equalsIgnoreCase("mob") ){
            if(Utils.checkEverything(player, Args(0,args), "sr.mob", 2, null)){
                Methods.renameMobs(player,args[1]);
            }
         // Add glow effect
        }else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].toLowerCase().equalsIgnoreCase("glow") ){
            if(Utils.checkEverything(player, null, "sr.glow", 1, player.getItemInHand())){
                Methods.glowItem(player);
                return true;
            }
         // Hide flags
        }else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].toLowerCase().equalsIgnoreCase("hideflags") ){
            if(Utils.checkEverything(player, null, "sr.hide", 1, player.getItemInHand())){
                Methods.hideFlags(player);
                return true;
            } 
        // Info 
        }else if (cmd.getName().equalsIgnoreCase("sr")&& (args.length < 1)||cmd.getName().equalsIgnoreCase("sr") && args[0].equalsIgnoreCase("info") ){ 
            PluginDescriptionFile pdfFile = this.getDescription();
            String version1 = pdfFile.getVersion();
            sender.sendMessage(ChatColor.GREEN + "Simple Rename");
            sender.sendMessage(ChatColor.BLUE + "Author:"+ " " + ChatColor.GREEN + "Galaipa & EnergizerBEAST1");
            sender.sendMessage(ChatColor.BLUE + "Version:"+ " " +ChatColor.GREEN + version1);
            sender.sendMessage(ChatColor.BLUE + "BukkitDev:"+" " + ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/simple-rename/");
            sender.sendMessage(ChatColor.BLUE + "Metrics:"+" " + ChatColor.GREEN + "http://mcstats.org/plugin/SimpleRename");
            return true;
        //Characters list
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
        //Help
        else if (cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("help")  ){ 
            PluginDescriptionFile pdfFile = this.getDescription();
            String version1 = pdfFile.getVersion();
            Methods.helpInfo(player,version1);
            return true;
        }
        // Unknown command
        else{
            sender.sendMessage(ChatColor.GREEN + "[Simple Rename]" + ChatColor.RED + " Unknown command");
            sender.sendMessage(ChatColor.GREEN + "[Simple Rename]" + ChatColor.RED + " Type '/sr help' to see all avaliable commands");
            return true;
        }
        return true;
}

    
    public Economy loadEconomy(){ 
        Economy econ;
        try{ 
            getServer().getPluginManager().getPlugin("Vault");
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            econ =  rsp.getProvider();
            econEn = (econ != null);
        }catch (NullPointerException e){
            return null;
        }
        return econ;
    }
    
    private boolean setupEconomy(){
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }

        return (econ != null);
    }
    
    // LANGUAGE RELATED
    private YamlConfiguration loadTranslation(String language){
        copyTranslation("custom");
        YamlConfiguration yaml = null;
        if(language.equalsIgnoreCase("custom")){
            File languageFile = new File(getDataFolder() + File.separator + "lang"+ File.separator + language + ".yml");
            yaml = YamlConfiguration.loadConfiguration(languageFile);
        }else{
            InputStream defaultStream = getResource(language +".yml");
            Reader r;
            try {
                r = this.getReaderFromStream(defaultStream);
                yaml =  YamlConfiguration.loadConfiguration(r);
                r.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return yaml;
    }
    
    public Reader getReaderFromStream(InputStream initialStream) throws IOException {
          byte[] buffer = IOUtils.toByteArray(initialStream);
          Reader targetReader = new CharSequenceReader(new String(buffer));
          return targetReader;
    }
    
    private void copyTranslation(String trans) {
        File file = new File(getDataFolder().getAbsolutePath() + File.separator + "lang" + File.separator + trans + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            copy(getResource(trans + ".yml"), file);
        }
    }
    
    public static void copy(InputStream in, File file) {
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
    
    public static String getTranslation(String path) {
        String msg;
        if (messages.getString(path) == null){
            msg = "Message missing in the lang file. Contact Admin (N." + path + ")";
        }else{
            msg = prefix + " " + messages.getString(path);
            msg = ChatColor.translateAlternateColorCodes('&', msg);
        }
        return msg;
    }
    
    

}
