package me.parapenguin.overcast.scrimmage.event;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapTeam;
import me.parapenguin.overcast.scrimmage.map.MapTeamSpawn;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.player.PlayerChatEvent;
import me.parapenguin.overcast.scrimmage.utils.Characters;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
		client.setTeam(Scrimmage.getRotation().getSlot().getMap().getObservers(), true, true, true);
		
		event.setJoinMessage(client.getStars() + client.getTeam().getColor() + event.getPlayer().getName() + ChatColor.YELLOW + " joined the game.");
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Client client = Client.getClient(event.getPlayer());
		try {
			event.setLeaveMessage(client.getStars() + client.getTeam().getColor() + event.getPlayer().getName() + ChatColor.YELLOW + " left the game.");
			onPlayerExit(event.getPlayer());
		} catch(NullPointerException e) {}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Client client = Client.getClient(event.getPlayer());
		try {
			event.setQuitMessage(client.getStars() + client.getTeam().getColor() + event.getPlayer().getName() + ChatColor.YELLOW + " left the game.");
			onPlayerExit(event.getPlayer());
		} catch(NullPointerException e) {}
	}
	
	public void onPlayerExit(Player player) {
		Client.getClients().remove(Client.getClient(player));
	}
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();

		ChatColor color = ChatColor.RED;
		if(!Scrimmage.isOpen()) color = ChatColor.RED;
		else if(Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning()) color = ChatColor.GREEN;
		else color = ChatColor.GRAY;
		
		String team = "";
		if(!Scrimmage.isPublic())
			team = ChatColor.GRAY + "(" + ChatColor.GOLD + Scrimmage.getTeam() + ChatColor.GRAY + ")";
		
		event.setMotd(color + " " + Characters.raquo + " " + ChatColor.AQUA + map.getName() + color + " " + Characters.laquo + " "  + team);
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
		MapTeamSpawn spawn = client.getTeam().loadout(client, false, true);
		event.setRespawnLocation(spawn.getSpawn());
		if(!client.getTeam().isObserver() && spawn.getKit() != null)
			spawn.getKit().load(client);
		else client.getTeam().loadout(client, false, true);
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		if(event.isCancelled())
			return;
		
		Client client = event.getClient();
		String message = event.getMessage();
		
		MapTeam team = client.getTeam();
		
		String format = team.getColor() + "[Team] " + client.getStars() + team.getColor() + client.getPlayer().getName() + ChatColor.WHITE + ": " + message;
		if(!event.isTeam()) {
			format = client.getStars() + team.getColor() + client.getPlayer().getName() + ChatColor.WHITE + ": " + message;
			team = null;
		}
		
		Scrimmage.broadcast(format, team);
		ServerLog.info(format);
	}
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player == false && event.getDamager() instanceof Projectile == false)
			return;
		
		if(event.getEntity() instanceof Player == false)
			return;
		
		Client damaged = Client.getClient((Player) event.getEntity());
		Player attackerPlayer = null;
		if(event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if(proj.getShooter() instanceof Player == false)
				return;
			
			attackerPlayer = (Player) proj.getShooter();
		} else attackerPlayer = (Player) event.getDamager();
		Client attacker = Client.getClient(attackerPlayer);
		
		if(attacker.getTeam() == damaged.getTeam() || attacker.getTeam().isObserver() || damaged.getTeam().isObserver())
			event.setCancelled(true);
	}
	
}
