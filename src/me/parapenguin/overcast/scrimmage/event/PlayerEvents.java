package me.parapenguin.overcast.scrimmage.event;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.player.Client;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.DARK_AQUA + "Overcast Scrimmage Servers by " + ChatColor.GOLD + "ParaPenguin" + ChatColor.DARK_AQUA + "!");
		if(Scrimmage.isPublic())
			player.sendMessage(ChatColor.DARK_AQUA + "Public Scrimmage Server: " + ChatColor.GOLD + "#" + Scrimmage.getID());
		else
			player.sendMessage(ChatColor.GOLD + "Team " + Scrimmage.getTeam() + ChatColor.DARK_AQUA + "'s Private Scrimmage Server");
		
		Client client = new Client(player);
		
		Client.getClients().add(client);
		client.setTeam(Scrimmage.getRotation().getSlot().getMap().getObservers());
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		onPlayerExit(event.getPlayer());
		event.setLeaveMessage(null);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		onPlayerExit(event.getPlayer());
		event.setQuitMessage(null);
	}
	
	
	public void onPlayerExit(Player player) {
		Client.getClients().remove(Client.getClient(player));
	}
	
}
