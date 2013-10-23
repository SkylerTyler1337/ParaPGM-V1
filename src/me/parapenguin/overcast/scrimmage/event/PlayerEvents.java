package me.parapenguin.overcast.scrimmage.event;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.player.Client;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

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
		
		event.setJoinMessage(ChatColor.AQUA + event.getPlayer().getName() + ChatColor.YELLOW + " joined the game.");
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		onPlayerExit(event.getPlayer());
		event.setLeaveMessage(ChatColor.AQUA + event.getPlayer().getName() + ChatColor.YELLOW + " left the game.");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		onPlayerExit(event.getPlayer());
		event.setQuitMessage(ChatColor.AQUA + event.getPlayer().getName() + ChatColor.YELLOW + " left the game.");
	}
	
	public void onPlayerExit(Player player) {
		Client.getClients().remove(Client.getClient(player));
	}
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();

		ChatColor color = ChatColor.RED;
		if(!Scrimmage.isOpen()) {
			event.setMotd(color + " È " + ChatColor.AQUA + map.getName() + color + " Ç ");
			return;
		}
		
		color = ChatColor.GRAY;
		event.setMotd(color + " È " + ChatColor.AQUA + map.getName() + color + " Ç ");
	}
	
}
