package me.parapenguin.overcast.scrimmage.map.objective;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapTeam;

import org.bukkit.DyeColor;
import org.bukkit.Location;

public class WoolObjective extends TeamObjective {
	
	@Getter Location place;
	@Getter DyeColor wool;
	
	public WoolObjective(Map map, MapTeam owner, String name, Location place, DyeColor wool) {
		super(map, owner, name);
		this.place = place;
		this.wool = wool;
	}
	
	public static DyeColor getDye(String string) {
		for(DyeColor dye : DyeColor.values())
			if(dye.name().replaceAll("_", " ").equalsIgnoreCase(string))
				return dye;
		
		return null;
	}
	
	public boolean isLocation(Location location) {
		boolean x = place.getBlockX() == location.getBlockX();
		boolean y = place.getBlockY() == location.getBlockY();
		boolean z = place.getBlockZ() == location.getBlockZ();
		boolean match = x && y && z;
		
		return match;
	}
	
}
