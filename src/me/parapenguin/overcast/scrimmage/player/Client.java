package me.parapenguin.overcast.scrimmage.player;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
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
		this.team = team;
		player.teleport(team.getSpawn());
		player.setScoreboard(team.getMap().getBoard());
		team.loadout(this);
	}
	
	public boolean isObserver() {
		return team.isObserver();
	}
	
}
