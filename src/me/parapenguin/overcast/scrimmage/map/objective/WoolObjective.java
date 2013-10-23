package me.parapenguin.overcast.scrimmage.map.objective;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.map.Map;

import org.bukkit.DyeColor;
import org.bukkit.Location;

public class WoolObjective extends TeamObjective {
	
	@Getter Location place;
	@Getter DyeColor wool;
	
	public WoolObjective(Map map, String name, Location place, DyeColor wool) {
		super(map, name);
		this.place = place;
		this.wool = wool;
	}
	
	public static DyeColor getDye(String string) {
		for(DyeColor dye : DyeColor.values())
			if(dye.name().replaceAll("_", " ").equalsIgnoreCase(string))
				return dye;
		
		return null;
	}
	
}
