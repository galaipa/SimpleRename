package io.github.galaipa.sr;

import static io.github.galaipa.sr.Utils.Args;
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
	if (Utils.checkEverything(player, Args(0, args), "sr.name", 1, player.getItemInHand())) {
	    Methods.setName(player.getItemInHand(), (Args(0, args)));
	    player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
	}
    }

    public static void cmdAddLore(Player player, String[] args) {
	if (Utils.checkEverything(player, Args(0, args), "sr.lore", 1, player.getItemInHand())) {
	    Methods.addLore(player.getItemInHand(), (Args(0, args)));
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
	if (Utils.checkEverything(player, Args(0, args), "sr.lore", 1, player.getItemInHand())) {
	    Methods.setLore(player.getItemInHand(), (Args(0, args)));
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
		cmdSR_Book(player, args);
		break;
	    case "clear":
		cmdSR_Clear(player);
		break;
	    case "duplicate":
		cmdSR_Duplicate(player, args);
		break;
	    case "getamount":
		cmdSR_GetAmount(player, args);
		break;
	    case "copy":
		cmdSR_Copy(player, args);
		break;
	    case "paste":
		cmdSR_Paste(player, args);
		break;
	    case "reload":
		cmdSR_Reload(player);
		break;
	    case "getskull":
		cmdSR_GetSkull(player, args);
		break;
	    case "glow":
		cmdSR_Glow(player);
		break;
	    case "unglow":
		cmdSR_UnGlow(player);
		break;
	    case "breakable":
		cmdSR_Breakable(player);
		break;
	    case "unbreakable":
		cmdSR_Unbreakable(player);
		break;
	    case "mob":
		cmdSR_Mob(player, args);
		break;
	    case "hideflags":
		cmdSR_Hideflags(player);
		break;
	    case "characters":
	    case "character":
		cmdSR_Characters(player);
		break;
	    case "info":
		cmdSR_Info(player);
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

    public void cmdSR_Book(Player player, String[] args) {
	if (player.getItemInHand().getType() != Material.WRITTEN_BOOK) {
	    player.sendMessage(ChatColor.RED + getTranslation("16"));
	} else if (Utils.checkEverything(player, Args(2, args), "sr.book", 0, null)) {
	    switch (args[1].toLowerCase()) {
	    case "setauthor":
		Methods.setBookAuthor(player.getItemInHand(), Args(2, args));
		player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
		break;
	    case "settitle":
		Methods.setBookTitle(player.getItemInHand(), Args(2, args));
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

    public void cmdSR_Clear(Player player) {
	if (Utils.checkEverything(player, null, "sr.clear", 0, player.getItemInHand())) {
	    Methods.clearItem(player.getItemInHand());
	    player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
	}
    }

    public void cmdSR_Duplicate(Player player, String[] args) {
	if (Utils.checkEverything(player, null, "sr.duplicate", 0, player.getItemInHand())) {
	    if (args.length >= 2 && Utils.isInt(args[1]))
		Methods.duplicateItem(player.getItemInHand(), Integer.parseInt(args[1]));
	    else
		Methods.duplicateItem(player.getItemInHand(), 2);
	    player.sendMessage(ChatColor.GREEN + (getTranslation("10")));
	}
    }

    public void cmdSR_GetAmount(Player player, String[] args) {
	if (Utils.checkEverything(player, null, "sr.duplicate", 0, player.getItemInHand())) {
	    if (args.length >= 2 && Utils.isInt(args[1]))
		player.getItemInHand().setAmount(Integer.parseInt(args[1]));
	    else
		player.getItemInHand().setAmount(2);
	}
    }

    public void cmdSR_Copy(Player player, String[] args) {
	if (Utils.checkEverything(player, player.getItemInHand().getItemMeta().getDisplayName(), "sr.copy", 1,
		player.getItemInHand())) {
	    Methods.copyMeta(player);
	}
    }

    public void cmdSR_Paste(Player player, String[] args) {
	if (Utils.checkEverything(player, null, "sr.copy", 1, player.getItemInHand())) {
	    Methods.pasteMeta(player);
	}
    }

    public void cmdSR_Reload(Player player) {
	if (Utils.checkEverything(player, null, "sr.reload", 1, null)) {
	    reloadConfig();
	    onEnable();
	    player.sendMessage(ChatColor.BLUE + "SimpleRename reloaded");
	}
    }

    public void cmdSR_GetSkull(Player player, String[] args) {
	if (Utils.checkEverything(player, null, "sr.skull", 2, null)) {
	    ItemStack skull = Methods.getSkull(args[1]);
	    player.getInventory().addItem(skull);
	}
    }

    public void cmdSR_Mob(Player player, String[] args) {
	if (Utils.checkEverything(player, Args(0, args), "sr.mob", 2, null)) {
	    Methods.renameMobs(player, args[1]);
	}
    }

    public void cmdSR_Glow(Player player) {
	if (Utils.checkEverything(player, null, "sr.glow", 1, player.getItemInHand())) {
	    Methods.glowItem(player.getItemInHand());
	    player.updateInventory();
	    player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
	}
    }

    public void cmdSR_UnGlow(Player player) {
	if (Utils.checkEverything(player, null, "sr.glow", 1, player.getItemInHand())) {
	    Methods.unGlowItem(player.getItemInHand());
	    player.updateInventory();
	    player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
	}
    }

    public void cmdSR_Hideflags(Player player) {
	if (Utils.checkEverything(player, null, "sr.hide", 1, player.getItemInHand())) {
	    Methods.hideFlags(player.getItemInHand());
	    player.updateInventory();
	    player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
	}
    }

    public void cmdSR_Breakable(Player player) {
	if (Utils.checkEverything(player, null, "sr.unbreakable", 1, player.getItemInHand())) {
	    Methods.makeUnbreakable(player.getItemInHand(), false);
	    player.updateInventory();
	    player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
	}
    }

    public void cmdSR_Unbreakable(Player player) {
	if (Utils.checkEverything(player, null, "sr.unbreakable", 1, player.getItemInHand())) {
	    Methods.makeUnbreakable(player.getItemInHand(), true);
	    player.updateInventory();
	    player.sendMessage(ChatColor.GREEN + (getTranslation("5")));
	}
    }

    public void cmdSR_Info(Player player) {
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

    public void cmdSR_Characters(Player player) {
	player.sendMessage(ChatColor.GREEN + "Special Characters List (SimpleRename)");
	player.sendMessage(ChatColor.BLUE + "[<3]" + " " + "----->" + ChatColor.WHITE + "\u2764");
	player.sendMessage(ChatColor.BLUE + "[ARROW]" + " " + "----->" + ChatColor.WHITE + "\u279c");
	player.sendMessage(ChatColor.BLUE + "[TICK]" + " " + "----->" + ChatColor.WHITE + "\u2714");
	player.sendMessage(ChatColor.BLUE + "[X]" + " " + "----->" + ChatColor.WHITE + "\u2716");
	player.sendMessage(ChatColor.BLUE + "[STAR]" + " " + "----->" + ChatColor.WHITE + "\u2605");
	player.sendMessage(ChatColor.BLUE + "[POINT]" + " " + "----->" + ChatColor.WHITE + "\u25Cf");
	player.sendMessage(ChatColor.BLUE + "[FLOWER]" + " " + "----->" + ChatColor.WHITE + "\u273f");
	player.sendMessage(ChatColor.BLUE + "[XD]" + " " + "----->" + ChatColor.WHITE + "\u263b");
	player.sendMessage(ChatColor.BLUE + "[DANGER]" + " " + "----->" + ChatColor.WHITE + "\u26a0");
	player.sendMessage(ChatColor.BLUE + "[MAIL]" + " " + "----->" + ChatColor.WHITE + "\u2709");
	player.sendMessage(ChatColor.BLUE + "[ARROW2]" + " " + "----->" + ChatColor.WHITE + "\u27a4");
	player.sendMessage(ChatColor.BLUE + "[ROUND_STAR]" + " " + "----->" + ChatColor.WHITE + "\u2730");
	player.sendMessage(ChatColor.BLUE + "[SUIT]" + " " + "----->" + ChatColor.WHITE + "\u2666");
	player.sendMessage(ChatColor.BLUE + "[+]" + " " + "----->" + ChatColor.WHITE + "\u2726");
	player.sendMessage(ChatColor.BLUE + "[CIRCLE]" + " " + "----->" + ChatColor.WHITE + "\u25CF");
	player.sendMessage(ChatColor.BLUE + "[SUN]" + " " + "----->" + ChatColor.WHITE + "\u2739");
    }

    public void cmdSR_Help(Player player) {
	PluginDescriptionFile pdfFile = this.getDescription();
	String version1 = pdfFile.getVersion();
	Methods.helpInfo(player, version1);
    }

    public Economy loadEconomy() {
	Economy econ;
	try {
	    getServer().getPluginManager().getPlugin("Vault");
	    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
		    .getRegistration(net.milkbowl.vault.economy.Economy.class);
	    econ = rsp.getProvider();
	    econEn = (econ != null);
	} catch (NullPointerException e) {
	    return null;
	}
	return econ;
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
	    Reader r;
	    try {
		r = new InputStreamReader(defaultStream);
		yaml = YamlConfiguration.loadConfiguration(r);
		r.close();
	    } catch (IOException e) {
		e.printStackTrace();
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
