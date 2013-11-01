package me.parapenguin.overcast.scrimmage.map.objective;

import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;
import me.parapenguin.overcast.scrimmage.player.Client;

public class MonumentBlock {
	
	@Getter @Setter Client breaker;
	@Getter Location location;
	
	public MonumentBlock(Location location) {
		this.location = location;
	}
	
	public boolean isBroken() {
		return breaker != null;
	}
	
}
