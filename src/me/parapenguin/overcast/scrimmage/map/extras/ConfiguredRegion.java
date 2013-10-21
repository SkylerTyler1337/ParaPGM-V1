package me.parapenguin.overcast.scrimmage.map.extras;

import java.util.List;

import org.bukkit.Location;

import lombok.Getter;

public class ConfiguredRegion {
	
	@Getter String name;
	@Getter List<Location> locations;
	
	public ConfiguredRegion(List<Location> locations) {
		this(null, locations);
	}
	
	public ConfiguredRegion(String name, List<Location> locations) {
		this.name = name;
		this.locations = locations;
	}
	
}
