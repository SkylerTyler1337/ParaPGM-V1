package me.parapenguin.overcast.scrimmage.rotation;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;

public class Rotation {
	
	static @Getter List<Map> loaded = new ArrayList<Map>();
	@Getter List<RotationSlot> rotation = new ArrayList<RotationSlot>();
	
	public Rotation() {
		int id = 1;
		while(Scrimmage.getInstance().getConfig().getString("rotation." + id) != null) {
			String map = Scrimmage.getInstance().getConfig().getString("rotation." + id + ".name");
			
			new RotationSlot(getMap(loaded, map));
			
			id++;
		}
	}
	
	public static boolean addMap(Map map) {
		return loaded.add(map);
	}
	
	public static Map getMap(List<Map> maps, String name) {
		for(Map map : maps)
			if(map.getName().startsWith(name))
				return map;
		
		return null;
	}
	
}
