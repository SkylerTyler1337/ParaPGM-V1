package me.parapenguin.overcast.scrimmage.player;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.Map;
// import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.MapTeam;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class Client {
	
	static @Getter List<Client> clients = new ArrayList<Client>();
	
	public static Client getClient(Player player) {
		for(Client client : clients)
			if(client.getPlayer() == player)
				return client;
		
		return null;
	}
	
	@Getter Player player;
	@Getter MapTeam team;
	
	@Getter PermissionAttachment perms;
	
	public Client(Player player) {
		this.player = player;
		this.perms = player.addAttachment(Scrimmage.getInstance());
	}
	
	public boolean isRanked() {
		String[] devs = new String[]{"ParaPenguin"};
		String[] refs = new String[]{"pmheys", "your_loved_one", "dcstarwars", "ShinyDialga45", "iEli2tyree011"};
		
		String op = ChatColor.RED + "*";
		String dev = ChatColor.GOLD + "*";
		String ref = ChatColor.DARK_AQUA + "*";
		
		String stars = "";
		for(String string : devs)
			if(string.equalsIgnoreCase(getPlayer().getName())) {
				stars += dev;
				break;
			}
		
		for(String string : refs)
			if(string.equalsIgnoreCase(getPlayer().getName())) {
				stars += ref;
				break;
			}
		
		if(getPlayer().isOp())
			stars += op;
		
		return stars.length() != 0;
	}
	
	public String getStars() {
		if(!isRanked()) return "";
		
		String[] devs = new String[]{"ParaPenguin"};
		String[] refs = new String[]{"pmheys", "your_loved_one", "dcstarwars", "ShinyDialga45", "iEli2tyree011"};
		
		String op = ChatColor.RED + "*";
		String dev = ChatColor.GOLD + "*";
		String ref = ChatColor.DARK_AQUA + "*";
		
		String stars = "";
		for(String string : devs)
			if(string.equalsIgnoreCase(getPlayer().getName())) {
				stars += dev;
				break;
			}
		
		for(String string : refs)
			if(string.equalsIgnoreCase(getPlayer().getName())) {
				stars += ref;
				break;
			}
		
		if(getPlayer().isOp())
			stars += op;
		
		return stars;
	}
	
	public void setTeam(MapTeam team) {
		/*
		ServerLog.info("Starting: " + Scrimmage.getRotation().getSlot().getMatch().isCurrentlyStarting());
		ServerLog.info("Running: " + Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning());
		ServerLog.info("Cycling: " + Scrimmage.getRotation().getSlot().getMatch().isCurrentlyCycling());
		*/
		
		if(Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning()) {
			setTeam(team, true, true, true);
			return;
		}
		
		setTeam(team, false, true, false);
	}
	
	public void setTeam(MapTeam team, boolean load, boolean clear, boolean teleport) {
		this.team = team;
		player.setScoreboard(team.getMap().getBoard());
		if(load) team.loadout(this, teleport, clear);
		
		if(team.getTeam() == null) ServerLog.info("Scoreboard Team for '" + team.getName() + "' is null");
		if(clear) team.getTeam().addPlayer(getPlayer());
		
		updateVision();
	}
	
	public boolean isObserver() {
		return team.isObserver();
	}
	
	public void updateVision() {
		for(Client client : getClients()) {
			Map map = client.getTeam().getMap();
			List<Client> observers = map.getObservers().getPlayers();
			List<Client> players = new ArrayList<Client>();
			for(MapTeam team : map.getTeams())
				players.addAll(team.getPlayers());
			
			if(getTeam().isObserver()) {
				for(Client observer : observers)
					client.getPlayer().showPlayer(observer.getPlayer());
				for(Client player : players)
					client.getPlayer().showPlayer(player.getPlayer());
			} else {
				for(Client observer : observers)
					client.getPlayer().hidePlayer(observer.getPlayer());
				for(Client player : players)
					client.getPlayer().showPlayer(player.getPlayer());
			}
		}
	}
	
}
