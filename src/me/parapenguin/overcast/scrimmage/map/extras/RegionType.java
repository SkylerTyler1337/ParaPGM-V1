package me.parapenguin.overcast.scrimmage.map.extras;

public enum RegionType {
	
	ALL(),
	
	RECTANGLE(),
	CUBOID(),
	CIRCLE(),
	CYLINDER(),
	SPHERE(),
	BLOCK();
	
	public static RegionType getByElementName(String name) {
		for(RegionType type : values())
			if(type.name().equalsIgnoreCase(name))
				return type;
		
		return null;
	}
	
}
