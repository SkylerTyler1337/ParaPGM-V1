package me.parapenguin.overcast.scrimmage.map;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.kit.ItemKit;
import me.parapenguin.overcast.scrimmage.map.region.ConfiguredRegion;

import org.bukkit.Location;

public class MapTeamSpawn {
	
	public static float DEFAULT_YAW_VALUE = 0;
	public static float DEFAULT_PITCH_VALUE = 0;
	
	@Getter Map map;
	
	@Getter List<Location> possibles = new ArrayList<Location>();
	@Getter String kitName;
	
	public MapTeamSpawn(Map map, List<Location> possibles, String kitName) {
		this.map = map;
		this.possibles = possibles;
		this.kitName = kitName;
	}
	
	public MapTeamSpawn(Map map, ConfiguredRegion region, String kitName) {
		this(map, region.getLocations(), kitName);
	}
	
	public Location getSpawn() {
		try {
			return possibles.get(Scrimmage.random(0, possibles.size() - 1));
		} catch(IndexOutOfBoundsException ioobe) {
			// What a lovely Exception label... hahah
			ioobe.printStackTrace();
		}
		
		return null;
	}
	
	public ItemKit getKit() {
		for(ItemKit kit : getMap().getKits())
			if(kit.getName().equalsIgnoreCase(kitName))
				return kit;
		
		return null;
	}
	
	public MapTeamSpawn clone() {
		return new MapTeamSpawn(getMap(), getPossibles(), getKitName());
	}
	
}
