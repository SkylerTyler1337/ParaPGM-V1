package me.parapenguin.overcast.scrimmage.rotation;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.MapLoader;

public class Rotation {
	
	static @Getter List<MapLoader> loaded = new ArrayList<MapLoader>();
	@Getter List<RotationSlot> rotation = new ArrayList<RotationSlot>();
	
	@Getter RotationSlot slot;
	
	public Rotation() {
		int id = 1;
		while(Scrimmage.getInstance().getConfig().getString("rotation." + id) != null) {
			String map = Scrimmage.getInstance().getConfig().getString("rotation." + id + ".name");
			rotation.add(new RotationSlot(getMap(loaded, map)));
			id++;
		}
	}
	
	public void setNext(RotationSlot slot) {
		int current = getLocation(slot);
		
		List<RotationSlot> pre = rotation.subList(0, current);
		List<RotationSlot> aft = rotation.subList(current + 1, rotation.size() - 1);
		
		List<RotationSlot> rotation = new ArrayList<RotationSlot>();
		rotation.addAll(pre);
		rotation.add(slot);
		rotation.addAll(aft);
	}
	
	public int getLocation(RotationSlot slot) {
		int s = 0;
		for(RotationSlot search : getRotation()) {
			if(search == slot)
				return s;
			s++;
		}
		
		return s;
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
