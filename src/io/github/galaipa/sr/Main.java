package io.github.galaipa.sr;



import io.github.galaipa.sr.Updater.ReleaseType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static Economy econ = null;
    public static final Logger log = Logger.getLogger("Minecraft");    
    public static boolean update = false;
    public static String name = "";
    public static ReleaseType type = null;
    public static String version = "";
    public static String link = "";
    @Override
    public void onDisable() {
        PluginManager pluginManager = getServer().getPluginManager();
        log.info(getConfig().getString("1"));
    }

    @Override
    public void onEnable() {        
        log.info(getConfig().getString("2"));
        saveDefaultConfig();       
        getServer().getPluginManager().registerEvents(new UpdateListener(), this);
        if ((getConfig().getBoolean("Economy"))){
            if (!setupEconomy()){
                log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
                
            }
        }
        if ((getConfig().getBoolean("Updater"))){
        Updater updater = new Updater(this, 75680, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false); // Start Updater but just do a version check
        update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE; // Determine if there is an update ready for us
        name = updater.getLatestName(); // Get the latest name
        version = updater.getLatestGameVersion(); // Get the latest game version
        type = updater.getLatestType(); // Get the latest file's type
        link = updater.getLatestFileLink(); // Get the latest link

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
        //AL ARGS
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++){
            sb.append(args[i]).append(" ");
            }String allArgs = sb.toString().trim();
            //CHARACTERS
            allArgs = allArgs.replace("[<3]" , "\u2764");
            allArgs = allArgs.replace("[ARROW]" , "\u279c");
            allArgs = allArgs.replace("[TICK]" , "\u2714");
            allArgs = allArgs.replace("[X]]" , "\u2716");
            allArgs = allArgs.replace("[STAR]" , "\u2716");
            allArgs = allArgs.replace("[HAND]" , "/u270C");
            allArgs = allArgs.replace("[FLOWER]" , "\u273f");
       //CHARACTERS

        if (cmd.getName().equalsIgnoreCase("sr")){
            sender.sendMessage(ChatColor.GREEN + "Simple Rename");
            sender.sendMessage(ChatColor.BLUE + "Author:"+ " " + ChatColor.GREEN + "Galaipa");
            sender.sendMessage(ChatColor.BLUE + "Version:"+ " " +ChatColor.GREEN + "2.0");
            sender.sendMessage(ChatColor.BLUE + "BukkitDev:"+" " + ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/simple-rename/");
            sender.sendMessage(ChatColor.BLUE + "Metrics:"+" " + ChatColor.GREEN + "http://mcstats.org/plugin/SimpleRename");
            return true;
            }
  //UPDATE          
        if (cmd.getName().equalsIgnoreCase("sr") && args[0].equalsIgnoreCase("update") && player.hasPermission("sr.update")) {
            Updater updater = new Updater(this, 75680, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, true); // Go straight to downloading, and announce progress to console.
            sender.sendMessage(ChatColor.GREEN + "Update progress in the console");
            return true;
            }
 // NAME NAME NAME NAME NAME NAME       
        else if(cmd.getName().equalsIgnoreCase("rename")){
            if (!player.hasPermission("sr.name")) {
                sender.sendMessage(ChatColor.RED+(getConfig().getString("6")));
                return true;

            }else if (args.length < 1) {
                sender.sendMessage(ChatColor.RED+(getConfig().getString("3")));
                return true;
                
            }else if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null) {
                sender.sendMessage(ChatColor.RED +(getConfig().getString("4")));
                return true;                
             }else if(allArgs.contains("&") && !player.hasPermission("sr.color")) {
                sender.sendMessage(ChatColor.RED +(getConfig().getString("7")));;
                return true;



//Economy off                
            }else if (!(getConfig().getBoolean("Economy"))){
              ItemStack item = player.getItemInHand();           
              ItemMeta itemStackMeta = item.getItemMeta();
              itemStackMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', allArgs));           
              item.setItemMeta(itemStackMeta);        
              sender.sendMessage(ChatColor.GREEN +(getConfig().getString("5"))); 
              return true;
           

//Economy on             
            }else if ((getConfig().getBoolean("Economy"))) {
                if (player.hasPermission("sr.free")) {
              ItemStack item = player.getItemInHand();           
              ItemMeta itemStackMeta = item.getItemMeta();
              itemStackMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', allArgs));           
              item.setItemMeta(itemStackMeta); 
              sender.sendMessage(ChatColor.GREEN + (getConfig().getString("5")) + (" ") + ChatColor.RED + (getConfig().getString("9")) + (":") + (" ") + ("0") + ("$") );
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
                        sender.sendMessage(ChatColor.GREEN + (getConfig().getString("5")) + (" ") + ChatColor.RED + (getConfig().getString("9")) + (":") + (" ") + precio + ("$") );
                        return true;
                    }else{
                        sender.sendMessage(ChatColor.RED + (getConfig().getString("8"))+ ChatColor.RED + (getConfig().getString("9")) + (":") + (" ") + precio + ("$") );
                        return true;
                    }             
                }		
} 
            
//LORE LORE LORE LORE LORE LORE LORE LORE LORE LORE LORE LORE

} else if (cmd.getName().equalsIgnoreCase("relore"));{
            if (!player.hasPermission("sr.lore")) {
                sender.sendMessage(ChatColor.RED+(getConfig().getString("6")));

            }else if (args.length < 1) {
                sender.sendMessage(ChatColor.RED+(getConfig().getString("3")));

                
            }else if (player.getItemInHand().getType() == Material.AIR || player.getItemInHand() == null) {
                sender.sendMessage(ChatColor.RED +(getConfig().getString("4")));
             }else if(allArgs.contains("&") && !player.hasPermission("sr.color")) {
                sender.sendMessage(ChatColor.RED +(getConfig().getString("7")));;
                return true;

//Economy off                
            }else if (!(getConfig().getBoolean("Economy"))){    
            ItemStack itemStack = player.getItemInHand();
            List<String> lore = new ArrayList();
            lore.add(ChatColor.translateAlternateColorCodes('&', allArgs));
            ItemMeta itemStackMeta = itemStack.getItemMeta();
            itemStackMeta.setLore(lore);
            itemStack.setItemMeta(itemStackMeta);
            sender.sendMessage(ChatColor.GREEN +(getConfig().getString("5")));
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
                        sender.sendMessage(ChatColor.GREEN + (getConfig().getString("5")) + (" ") + ChatColor.RED + (getConfig().getString("9")) + (":") + (" ") + ("0") + ("$") );
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
                        sender.sendMessage(ChatColor.GREEN + (getConfig().getString("5")) + (" ") + ChatColor.RED + (getConfig().getString("9")) + (":") + (" ") + precioa + ("$") );
                        return true;

                    }else{
                        sender.sendMessage(ChatColor.RED + (getConfig().getString("8"))+ ChatColor.RED + (getConfig().getString("9")) + (":") + (" ") + precioa + ("$") );
                        return true;
                    }             
                }               
            		
        }
    
}

return true;
}
}

    

