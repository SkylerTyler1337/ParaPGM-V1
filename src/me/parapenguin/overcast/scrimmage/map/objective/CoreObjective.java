package me.parapenguin.overcast.scrimmage.map.objective;

import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapTeam;

import org.bukkit.Location;

public class CoreObjective extends TeamObjective {
	
	@Getter List<Location> blocks;
	
	public CoreObjective(Map map, MapTeam owner, String name, List<Location> blocks) {
		super(map, owner, name);
		this.blocks = blocks;
	}
	
	public boolean isLocation(Location location) {
		return getBlock(location) != null;
	}
	
	public Location getBlock(Location location) {
		for(Location loc : blocks) {
			boolean x = loc.getBlockX() == location.getBlockX();
			boolean y = loc.getBlockY() == location.getBlockY();
			boolean z = loc.getBlockZ() == location.getBlockZ();
			if(x && y && z) return loc;
		}
		
		return null;
	}
	
}
