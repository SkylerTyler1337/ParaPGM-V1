package me.parapenguin.overcast.scrimmage.map.kit;

import org.dom4j.Element;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.map.MapLoader;

public class KitLoader {
	
	@Getter Element element;
	
	public KitLoader(Element element) {
		this.element = element;
	}
	
	public ItemKit load() {
		ItemKit kit = null;
		
		for(Element element : MapLoader.getElements(this.element, "item")) {
			/*
			 * Load the Items with their Item Slawt and sheeeeeeeeet!
			 * Example: <item slot="0">iron sword</item>
			 */
			
			
		}
		
		return kit;
	}
	
}
