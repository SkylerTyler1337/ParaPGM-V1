package me.parapenguin.overcast.scrimmage.rotation;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.MapLoader;

public class Rotation {
	
	static @Getter List<MapLoader> loaded = new ArrayList<MapLoader>();
	@Getter List<RotationSlot> rotation = new ArrayList<RotationSlot>();
	
	public Rotation() {
		int id = 1;
		while(Scrimmage.getInstance().getConfig().getString("rotation." + id) != null) {
			String map = Scrimmage.getInstance().getConfig().getString("rotation." + id + ".name");
			
			new RotationSlot(getMap(loaded, map));
			
			id++;
		}
	}
	
	public static boolean addMap(MapLoader loader) {
		return loaded.add(loader);
	}
	
	public static MapLoader getMap(List<MapLoader> loaded, String name) {
		for(MapLoader loader : loaded)
			if(loader.getName().startsWith(name))
				return loader;
		
		return null;
	}
	
}
