package io.github.galaipa.sr;

import static io.github.galaipa.sr.Utils.extractArgs;
import io.github.galaipa.sr.anvilListeners.AnvilListener;
import io.github.galaipa.sr.anvilListeners.AnvilListenerAlternative;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bstats.bukkit.MetricsLite;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleRename extends JavaPlugin {
    public static final Logger log = Logger.getLogger("Minecraft");

    public static Economy econ;
    public static boolean econEn, xpEn;

    public static int characterLimit;
    public static String prefix;

    public static String language;
    public static YamlConfiguration messages;

    public static List<String> nameBlackList, itemBlackList;

    public static boolean anvilFeatures = true;
    public static String version;

    @Override
    public void onDisable() {
        log.info("SimpleRename disabled!");
    }

    @Override
    public void onEnable() {
        // Check Spigot/Bukkit version
        version = getServer().getBukkitVersion();
        // Load configuration file
        getConfig().options().copyDefaults(true);
        saveConfig();
        // Load prefix from config and add a space
        // so that messages will not be clumped with prefix
        prefix = getConfig().getString("Prefix");
        // Register events
        registerEvents();
        // Load translations
        language = getConfig().getString("Language");
        messages = loadTranslation(language);
        // Load economy
        econEn = getConfig().getBoolean("Economy");
        if (econEn) {
            log.info(prefix
                    + "Economy support has been temporarily disabled. Feature will be back in a future version ");
            /*
             * if(!setupEconomy()){ econEn = false; log.info(prefix +
             * "Economy disabled, Vault not found!"); }
             */
        }

        // Load metrics
        if (getConfig().getBoolean("Metrics"))
            new MetricsLite(this);
        // Other config
        xpEn = getConfig().getBoolean("XPprices.Enable");
        if (xpEn)
            log.info(prefix + "XP price support has been temporarily disabled. Feature will be back in next version");
        characterLimit = getConfig().getInt("CharacterLimit");

        nameBlackList = getConfig().getStringList("BlackList");
        itemBlackList = getConfig().getStringList("BlackListID");
        anvilFeatures = getConfig().getBoolean("AnvilFeatures", true);
        log.info("SimpleRename enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Block console commands
        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 1 && cmd.getName().equalsIgnoreCase("sr") && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                onEnable();
                sender.sendMessage(prefix + "Plugin reloaded");
                return true;
            } else {
                sender.sendMessage(prefix + "Commands can only be run by players");
                return true;
            }
        }

        Player player = (Player) sender;
        String command = cmd.getName();
        switch (command) {
        case "rename":
            cmdRename(player, args);
            break;
        case "addlore":
            cmdAddLore(player, args);
            break;
        case "removelore":
            cmdRemoveLore(player, args);
            break;
        case "relore":
            cmdRelore(player, args);
            break;
        case "sr":
            cmdSR(player, args);
            break;
        default:
            cmdInvalid(player);
            break;
        }
        return true;
    }

    public static void cmdRename(Player player, String[] args) {
        if (Utils.checkEverything(player, extractArgs(0, args), "sr.name", 1, player.getItemInHand())) {
            Methods.setName(player.getItemInHand(), (extractArgs(0, args)));
            player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
        }
    }

    public static void cmdAddLore(Player player, String[] args) {
        if (Utils.checkEverything(player, extractArgs(0, args), "sr.lore", 1, player.getItemInHand())) {
            Methods.addLore(player.getItemInHand(), (extractArgs(0, args)));
            player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
        }
    }

    public static void cmdRemoveLore(Player player, String[] args) {
        if (Utils.checkEverything(player, null, "sr.removeLore", 0, player.getItemInHand())) {
            if (args.length != 0 && Utils.isInt(args[0]))
                Methods.removeLore(player.getItemInHand(), Integer.parseInt(args[0]) - 1);
            else
                Methods.removeLore(player.getItemInHand(), -1);
            player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
            player.updateInventory();
        }
    }

    public static void cmdRelore(Player player, String[] args) {
        if (Utils.checkEverything(player, extractArgs(0, args), "sr.lore", 1, player.getItemInHand())) {
            Methods.setLore(player.getItemInHand(), (extractArgs(0, args)));
            player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
        }
    }

    public void cmdInvalid(Player player) {
        player.sendMessage(prefix + ChatColor.RED + " Unknown command. Type /sr help ");
    }

    public void cmdHelp(Player player) {
        PluginDescriptionFile pdfFile = this.getDescription();
        String version1 = pdfFile.getVersion();
        Methods.helpInfo(player, version1);
    }

    public void cmdSR(Player player, String[] args) {
        try {
            switch (args[0].toLowerCase()) {
            case "book":
                cmdBook(player, args);
                break;
            case "clear":
                cmdClear(player);
                break;
            case "duplicate":
                cmdDuplicate(player, args);
                break;
            case "getamount":
                cmdGetAmount(player, args);
                break;
            case "copy":
                cmdCopy(player, args);
                break;
            case "paste":
                cmdPaste(player, args);
                break;
            case "reload":
                cmdReload(player);
                break;
            case "getskull":
                cmdSkull(player, args);
                break;
            case "glow":
                cmdGlow(player);
                break;
            case "unglow":
                cmdUnGlow(player);
                break;
            case "breakable":
                cmdBreakable(player);
                break;
            case "unbreakable":
                cmdUnbreakable(player);
                break;
            case "mob":
                cmdMob(player, args);
                break;
            case "hideflags":
                cmdHideFlags(player);
                break;
            case "characters":
            case "character":
                cmdCharacters(player);
                break;
            case "info":
                cmdInfo(player);
                break;
            case "help":
                cmdHelp(player);
                break;
            default:
                cmdInvalid(player);
                break;
            }

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            cmdInvalid(player);
        }
    }

    public void cmdBook(Player player, String[] args) {
        if (player.getItemInHand().getType() != Material.WRITTEN_BOOK) {
            player.sendMessage(ChatColor.RED + getTranslation("16"));
        } else if (Utils.checkEverything(player, extractArgs(2, args), "sr.book", 0, null)) {
            switch (args[1].toLowerCase()) {
            case "setauthor":
                Methods.setBookAuthor(player.getItemInHand(), extractArgs(2, args));
                player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
                break;
            case "settitle":
                Methods.setBookTitle(player.getItemInHand(), extractArgs(2, args));
                player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
                break;
            case "unsign":
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(),
                        Methods.unSignBook(player.getItemInHand()));
                player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
                break;
            default:
                cmdInvalid(player);
                break;
            }
        }

    }

    public void cmdClear(Player player) {
        if (Utils.checkEverything(player, null, "sr.clear", 0, player.getItemInHand())) {
            Methods.clearItem(player.getItemInHand());
            player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
        }
    }

    public void cmdDuplicate(Player player, String[] args) {
        if (Utils.checkEverything(player, null, "sr.duplicate", 0, player.getItemInHand())) {
            if (args.length >= 2 && Utils.isInt(args[1]))
                Methods.duplicateItem(player.getItemInHand(), Integer.parseInt(args[1]));
            else
                Methods.duplicateItem(player.getItemInHand(), 2);
            player.sendMessage(ChatColor.GREEN + (getTranslation("10")));
        }
    }

    public void cmdGetAmount(Player player, String[] args) {
        if (Utils.checkEverything(player, null, "sr.duplicate", 0, player.getItemInHand())) {
            if (args.length >= 2 && Utils.isInt(args[1]))
                player.getItemInHand().setAmount(Integer.parseInt(args[1]));
            else
                player.getItemInHand().setAmount(2);
        }
    }

    public void cmdCopy(Player player, String[] args) {
        if (Utils.checkEverything(player, player.getItemInHand().getItemMeta().getDisplayName(), "sr.copy", 1,
                player.getItemInHand())) {
            Methods.copyMeta(player);
        }
    }

    public void cmdPaste(Player player, String[] args) {
        if (Utils.checkEverything(player, null, "sr.copy", 1, player.getItemInHand())) {
            Methods.pasteMeta(player);
        }
    }

    public void cmdReload(Player player) {
        if (Utils.checkEverything(player, null, "sr.reload", 1, null)) {
            reloadConfig();
            onEnable();
            player.sendMessage(ChatColor.BLUE + "SimpleRename reloaded");
        }
    }

    public void cmdSkull(Player player, String[] args) {
        if (Utils.checkEverything(player, null, "sr.skull", 2, null)) {
            ItemStack skull = Methods.getSkull(args[1]);
            player.getInventory().addItem(skull);
        }
    }

    public void cmdMob(Player player, String[] args) {
        if (Utils.checkEverything(player, extractArgs(0, args), "sr.mob", 2, null)) {
            Methods.renameMobs(player, args[1]);
        }
    }

    public void cmdGlow(Player player) {
        if (Utils.checkEverything(player, null, "sr.glow", 1, player.getItemInHand())) {
            Methods.glowItem(player.getItemInHand());
            player.updateInventory();
            player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
        }
    }

    public void cmdUnGlow(Player player) {
        if (Utils.checkEverything(player, null, "sr.glow", 1, player.getItemInHand())) {
            Methods.unGlowItem(player.getItemInHand());
            player.updateInventory();
            player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
        }
    }

    public void cmdHideFlags(Player player) {
        if (Utils.checkEverything(player, null, "sr.hide", 1, player.getItemInHand())) {
            Methods.hideFlags(player.getItemInHand());
            player.updateInventory();
            player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
        }
    }

    public void cmdBreakable(Player player) {
        if (Utils.checkEverything(player, null, "sr.unbreakable", 1, player.getItemInHand())) {
            Methods.makeUnbreakable(player.getItemInHand(), false);
            player.updateInventory();
            player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
        }
    }

    public void cmdUnbreakable(Player player) {
        if (Utils.checkEverything(player, null, "sr.unbreakable", 1, player.getItemInHand())) {
            Methods.makeUnbreakable(player.getItemInHand(), true);
            player.updateInventory();
            player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
        }
    }

    public void cmdInfo(Player player) {
        PluginDescriptionFile pdfFile = this.getDescription();
        String version1 = pdfFile.getVersion();
        player.sendMessage(ChatColor.GREEN + "Simple Rename");
        player.sendMessage(ChatColor.BLUE + "Author:" + " " + ChatColor.GREEN + "Galaipa & EnergizerBEAST1");
        player.sendMessage(ChatColor.BLUE + "Version:" + " " + ChatColor.GREEN + version1);
        player.sendMessage(ChatColor.BLUE + "BukkitDev:" + " " + ChatColor.GREEN
                + "http://dev.bukkit.org/bukkit-plugins/simple-rename/");
        player.sendMessage(
                ChatColor.BLUE + "Metrics:" + " " + ChatColor.GREEN + "https://bstats.org/plugin/bukkit/SimpleRename");
    }

    private static final String[][] CHARACTERS = { { "[<3]", "\u2764" }, { "[ARROW]", "\u279c" },
            { "[TICK]", "\u2714" }, { "[X]", "\u2716" }, { "[STAR]", "\u2605" }, { "[POINT]", "\u25Cf" },
            { "[FLOWER]", "\u273f" }, { "[XD]", "\u263b" }, { "[DANGER]", "\u26a0" }, { "[MAIL]", "\u2709" },
            { "[ARROW2]", "\u27a4" }, { "[ROUND_STAR]", "\u2730" }, { "[SUIT]", "\u2666" }, { "[+]", "\u2726" },
            { "[CIRCLE]", "\u25CF" }, { "[SUN]", "\u2739" } };

    public void cmdCharacters(Player player) {
        player.sendMessage(ChatColor.RED + "" + ChatColor.UNDERLINE + "SPECIAL CHARACTERS");
        for (String[] character : CHARACTERS) {
            player.sendMessage(ChatColor.WHITE + String.format("%-5s", character[1]) + ChatColor.RED + "|   " + ChatColor.BLUE  +  character[0]);
        }
    }

    public Economy loadEconomy() {
        Economy econonmy;
        try {
            getServer().getPluginManager().getPlugin("Vault");
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
                    .getRegistration(net.milkbowl.vault.economy.Economy.class);
            econonmy = rsp.getProvider();
        } catch (NullPointerException e) {
            return null;
        }
        return econonmy;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }

        return (econ != null);
    }

    // LANGUAGE RELATED
    private YamlConfiguration loadTranslation(String language) {
        copyTranslation("custom");
        YamlConfiguration yaml = null;
        if (language.equalsIgnoreCase("custom")) {
            File languageFile = new File(
                    getDataFolder() + File.separator + "lang" + File.separator + language + ".yml");
            yaml = YamlConfiguration.loadConfiguration(languageFile);
        } else {
            InputStream defaultStream = getResource(language + ".yml");
            try (Reader r = new InputStreamReader(defaultStream)) {
                yaml = YamlConfiguration.loadConfiguration(r);
            } catch (IOException e) {
                log.info("[SimpleRename] Could not read language file");
            }
        }
        return yaml;
    }

    private void copyTranslation(String trans) {
        File file = new File(
                getDataFolder().getAbsolutePath() + File.separator + "lang" + File.separator + trans + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            copy(getResource(trans + ".yml"), file);
        }
    }

    public static void copy(InputStream in, File file) {
        try (OutputStream out = new FileOutputStream(file)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
        } catch (Exception e) {
            log.info("[SimpleRename] Could not copy language file");
        }
    }

    public static String getTranslation(String path) {
        String msg;
        if (messages.getString(path) == null) {
            msg = "Message missing in the lang file. Contact Admin (N." + path + ")";
        } else {
            msg = prefix + " " + messages.getString(path);
            msg = ChatColor.translateAlternateColorCodes('&', msg);
        }
        return msg;
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        if (version.contains("1.8"))
            getServer().getPluginManager().registerEvents(new AnvilListenerAlternative(), this);
        else
            getServer().getPluginManager().registerEvents(new AnvilListener(), this);
    }
}
