package me.parapenguin.overcast.scrimmage.player;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.ServerLog;
// import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.MapTeam;

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
	
	public void setTeam(MapTeam team) {
		/*
		ServerLog.info("Starting: " + Scrimmage.getRotation().getSlot().getMatch().isCurrentlyStarting());
		ServerLog.info("Running: " + Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning());
		ServerLog.info("Cycling: " + Scrimmage.getRotation().getSlot().getMatch().isCurrentlyCycling());
		*/
		
		if(Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning()) {
			setTeam(team, true);
			return;
		}
		
		setTeam(team, false);
	}
	
	public void setTeam(MapTeam team, boolean load) {
		this.team = team;
		player.setScoreboard(team.getMap().getBoard());
		if(load) team.loadout(this, true);
		
		if(team.getTeam() == null) ServerLog.info("Scoreboard Team for '" + team.getName() + "' is null");
		team.getTeam().addPlayer(getPlayer());
	}
	
	public boolean isObserver() {
		return team.isObserver();
	}
	
}
