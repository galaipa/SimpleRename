
package io.github.galaipa.sr;


import static io.github.galaipa.sr.Utils.Args;
import static io.github.galaipa.sr.Utils.getTranslation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class SimpleRename extends JavaPlugin{
    public static Economy econ = null;
    public static final Logger log = Logger.getLogger("Minecraft");    
    public static boolean update = false,economy,xp;
    public static String name = "";
    public static Updater.ReleaseType type = null;
    public static String version = "";
    public static String link = "";
    public static String translation;
    public static YamlConfiguration yaml;
    File languageFile;
    public static int CharacterLimit;
    
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
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        Enable();
        utils = new Utils(this);
    }
    public void Enable(){
        loadTranslations();
        if ((getConfig().getBoolean("Economy"))){
            if (!setupEconomy() ) {
                log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }else{
                economy = true;
            }
        }if ((getConfig().getBoolean("XPprices.Enable"))){
            xp = true;
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
        // Glow effect
        Utils.registerGlow();
        CharacterLimit = getConfig().getInt("CharacterLimit");
    }
    public Utils utils;
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        //Block console commands
        if(sender instanceof ConsoleCommandSender){
            if(args.length == 1 && cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("reload") ){
                reloadConfig();
                Enable();
                sender.sendMessage("[SimpleRename]" + "Plugin reloaded");
                return true;
            }else{
                sender.sendMessage("[SimpleRename]" + "Commands can only be run by players");
                return true; 
            }
        }
        Player player = (Player)sender;
        if(args.length <1){
            PluginDescriptionFile pdfFile = this.getDescription();
            String version1 = pdfFile.getVersion();
            Methods.helpInfo(player,version1);
            return true;
                }
        // Item Rename
        if(cmd.getName().equalsIgnoreCase("rename")){
           if(utils.SecurityCheck(player, Args(0,args), "sr.name", 1,player.getItemInHand())){
               if (Utils.ordainketa(player,"Nprice","5","NameXP")){
                    Methods.setName(player,(Args(0,args)));
                       }
           }
           return true;
                }
        // Add Lore
        else if (cmd.getName().equalsIgnoreCase("addlore")){
           if(utils.SecurityCheck(player, Args(0,args), "sr.lore", 1, player.getItemInHand())){
               if (Utils.ordainketa(player,"Lprice","5", "LoreXP")){
                    Methods.addLore(player,(Args(0,args)));
                       }
           }
           return true;
                }
        // Set Lore (One line)
        else if (cmd.getName().equalsIgnoreCase("relore")){
           if(utils.SecurityCheck(player, Args(0,args), "sr.lore", 1, player.getItemInHand())){
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
            }else if(utils.SecurityCheck(player, Args(2,args), "sr.book", 0, null)){
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
           if(utils.SecurityCheck(player, null, "sr.clear", 0, player.getItemInHand())){
               if (Utils.ordainketa(player,"ClearPrice","13","ClearXP")){
                    Methods.clearItem(player);
                       }
           }
           return true;
            }
        // Duplicate
        else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("duplicate") ){
           if(utils.SecurityCheck(player, null, "sr.duplicate", 0, player.getItemInHand())){
               if(args.length >= 2 && utils.isInt(args[1])){
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
           if(utils.SecurityCheck(player, null, "sr.duplicate", 0, player.getItemInHand())){
               if(args.length >= 2 && utils.isInt(args[1])){
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
            if(utils.SecurityCheck(player, player.getItemInHand().getItemMeta().getDisplayName(), "sr.copy", 1, player.getItemInHand())){
                Methods.copyMeta(player);
            }
            return true;
        }
        //Paste
        else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("paste") ){
            if(utils.SecurityCheck(player, null, "sr.copy", 1, player.getItemInHand())){
                if (Utils.ordainketa(player,"PastePrice","12","PasteXP")){
                    Methods.pasteMeta(player);
                    return true;
                }
             }
            return true;
        //Reload
        }else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].equalsIgnoreCase("reload") ){
            if(utils.SecurityCheck(player, null, "sr.reload", 1, null)){
                reloadConfig();
                Enable();
                player.sendMessage(ChatColor.BLUE + "SimpleRename reloaded"); //OTHER RELOAD COMMAND FOR CONSOLE
                return true;
            }
            return true;
        //Get Skull
        }else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].toLowerCase().equalsIgnoreCase("getskull") ){
            if(utils.SecurityCheck(player, Args(0,args), "sr.skull", 2, null)){
                    Methods.getSkull(player, args[1]);
                    return true;
                }
         // Rename mobs
        }else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].toLowerCase().equalsIgnoreCase("mob") ){
            if(utils.SecurityCheck(player, Args(0,args), "sr.mob", 2, null)){
                Methods.renameMobs(player,args[1]);
            }
         // Add glow effect
        }else if(cmd.getName().equalsIgnoreCase("sr")&& args[0].toLowerCase().equalsIgnoreCase("glow") ){
            if(utils.SecurityCheck(player, null, "sr.glow", 1, player.getItemInHand())){
                Methods.glowItem(player);
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

        private void loadTranslations(){
            copyTranslation("custom");
            translation = getConfig().getString("Language");
            if(translation.equalsIgnoreCase("custom")){
                languageFile = new File(getDataFolder() + File.separator + "lang"+ File.separator + translation + ".yml");
                yaml = YamlConfiguration.loadConfiguration(languageFile);
            }else{
                InputStream defaultStream = getResource(translation +".yml");
                yaml =  YamlConfiguration.loadConfiguration(defaultStream);
            }
        }
         private void copyTranslation(String trans) {
                File file = new File(getDataFolder().getAbsolutePath() + File.separator + "lang" + File.separator + trans + ".yml");
                if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        Utils.copy(getResource(trans + ".yml"), file);
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


}
