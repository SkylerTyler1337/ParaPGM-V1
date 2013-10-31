package me.parapenguin.overcast.scrimmage.rotation;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapLoader;
import me.parapenguin.overcast.scrimmage.match.Match;

public class RotationSlot {
	
	@Getter Map map;
	@Getter MapLoader loader;
	@Getter Match match;
	
	public RotationSlot(MapLoader loader) {
		this.loader = loader;
	}
	
	public Match load(int length) {
		map = loader.getMap(this);
		match = new Match(this, length);
		return match;
	}
	
	public Match load() {
		// return load(45*60);
		return load(-1);
	}
	
}
