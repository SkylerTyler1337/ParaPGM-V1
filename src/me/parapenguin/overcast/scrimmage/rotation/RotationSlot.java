package me.parapenguin.overcast.scrimmage.rotation;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.map.Map;

public class RotationSlot {
	
	@Getter Map map;
	
	public RotationSlot(Map map) {
		this.map = map;
	}
	
}
