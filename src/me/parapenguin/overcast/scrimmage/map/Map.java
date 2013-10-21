package me.parapenguin.overcast.scrimmage.map;

import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.map.extras.Contributor;

public class Map {
	
	@Getter String name;
	@Getter String version;
	@Getter String objective;
	@Getter List<String> authors;
	@Getter List<Contributor> contributors;
	@Getter List<MapTeam> teams;
	@Getter MapTeam observers;
	
}
