package me.parapenguin.overcast.scrimmage.map;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.region.ConfiguredRegion;

import org.bukkit.Location;

public class MapTeamSpawn {
	
	public static float DEFAULT_YAW_VALUE = 0;
	public static float DEFAULT_PITCH_VALUE = 0;
	
	@Getter List<Location> possibles = new ArrayList<Location>();
	@Getter String kit;
	
	public MapTeamSpawn(List<Location> possibles) {
		this.possibles = possibles;
	}
	
	public MapTeamSpawn(ConfiguredRegion region) {
		this.possibles = region.getLocations();
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
	
	public MapTeamSpawn clone() {
		return new MapTeamSpawn(getPossibles());
	}
	
}
