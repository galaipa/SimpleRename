
package io.github.galaipa.sr;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class UpdateListener implements Listener {

    @EventHandler
public void onPlayerJoin(PlayerJoinEvent event){
  Player player = event.getPlayer();
  if(player.hasPermission("sr.update") && Main.update)
  {
    player.sendMessage(ChatColor.GREEN + "An update is available: " + ChatColor.YELLOW + Main.name + ChatColor.GREEN +  " for " + Main.version + " available at " +  ChatColor.YELLOW + "http://goo.gl/hAf1QV");
    player.sendMessage(ChatColor.RED + "Type /sr update if you would like to update it automatically.");
  }
}
}

    

