package me.parapenguin.overcast.scrimmage.rotation;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapLoader;

public class RotationSlot {
	
	@Getter Map map;
	@Getter MapLoader loader;
	
	public RotationSlot(MapLoader loader) {
		this.loader = loader;
	}
	
	public void load() {
		
	}
	
}
