package me.parapenguin.overcast.scrimmage.map.extras;

public enum RegionGroupType {
	
	NEGATIVE(),
	UNION(),
	COMPLEMENT(),
	INTERSECT();
	
	public static RegionGroupType getByElementName(String name) {
		for(RegionGroupType type : values())
			if(type.name().equalsIgnoreCase(name))
				return type;
		
		return null;
	}
	
}
