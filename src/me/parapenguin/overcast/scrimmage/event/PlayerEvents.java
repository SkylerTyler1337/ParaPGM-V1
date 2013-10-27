package me.parapenguin.overcast.scrimmage.event;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapTeam;
import me.parapenguin.overcast.scrimmage.map.MapTeamSpawn;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.player.PlayerChatEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class PlayerEvents implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.DARK_AQUA + "Overcast Scrimmage Servers by " + ChatColor.GOLD + "ParaPenguin" + ChatColor.DARK_AQUA + "!");
		if(Scrimmage.isPublic())
			player.sendMessage(ChatColor.DARK_AQUA + "Public Scrimmage Server: " + ChatColor.GOLD + "#" + Scrimmage.getID());
		else
			player.sendMessage(ChatColor.GOLD + Scrimmage.getTeam() + ChatColor.DARK_AQUA + "'s Private Scrimmage Server");
		
		Client client = new Client(player);
		
		Client.getClients().add(client);
		client.setTeam(Scrimmage.getRotation().getSlot().getMap().getObservers(), true);
		
		event.setJoinMessage(client.getTeam().getColor() + event.getPlayer().getName() + ChatColor.YELLOW + " joined the game.");
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Client client = Client.getClient(event.getPlayer());
		event.setLeaveMessage(client.getTeam().getColor() + event.getPlayer().getName() + ChatColor.YELLOW + " left the game.");
		onPlayerExit(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Client client = Client.getClient(event.getPlayer());
		event.setQuitMessage(client.getTeam().getColor() + event.getPlayer().getName() + ChatColor.YELLOW + " left the game.");
		onPlayerExit(event.getPlayer());
	}
	
	public void onPlayerExit(Player player) {
		Client.getClients().remove(Client.getClient(player));
	}
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();

		ChatColor color = ChatColor.RED;
		if(!Scrimmage.isOpen()) {
			event.setMotd(color + " » " + ChatColor.AQUA + map.getName() + color + " « ");
			return;
		}
		
		color = ChatColor.GRAY;
		event.setMotd(color + " » " + ChatColor.AQUA + map.getName() + color + " « ");
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Client client = Client.getClient(event.getPlayer());
		event.setCancelled(true);
		
		PlayerChatEvent chat = new PlayerChatEvent(client, event.getMessage(), true);
		Scrimmage.callEvent(chat);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Client client = Client.getClient(event.getPlayer());
		MapTeamSpawn spawn = client.getTeam().loadout(client, false);
		event.setRespawnLocation(spawn.getSpawn());
		if(!client.getTeam().isObserver())
			spawn.getKit().load(client);
		else client.getTeam().loadout(client, false);
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		if(event.isCancelled())
			return;
		
		Client client = event.getClient();
		String message = event.getMessage();
		
		MapTeam team = client.getTeam();
		
		String format = team.getColor() + "[Team] " + client.getPlayer().getName() + ChatColor.WHITE + ": " + message;
		if(!event.isTeam()) {
			format = team.getColor() + client.getPlayer().getName() + ChatColor.WHITE + ": " + message;
			team = null;
		}
		
		Scrimmage.broadcast(format, team);
		ServerLog.info(format);
	}
	
}
