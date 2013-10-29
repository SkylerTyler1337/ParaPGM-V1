package me.parapenguin.overcast.scrimmage.map.kit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.dom4j.Element;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapLoader;
import me.parapenguin.overcast.scrimmage.utils.ConversionUtil;

public class KitLoader {
	
	@Getter Map map;
	@Getter Element element;
	
	public KitLoader(Map map, Element element) {
		this.map = map;
		this.element = element;
	}
	
	public ItemKit load() {
		String name = this.element.attributeValue("name").replaceAll(" ", "_");
		List<ItemSlot> slots = new ArrayList<ItemSlot>();
		List<ItemKit> parents = new ArrayList<ItemKit>();
		
		for(Element element : MapLoader.getElements(this.element, "item")) {
			/*
			 * Load the Items with their Item Slawt and sheeeeeeeeet!
			 * Example: <item slot="0">iron sword</item>
			 */
			
			try {
				int slot = Integer.parseInt(element.attributeValue("slot"));
				Material material = ConversionUtil.convertStringToMaterial(element.getText());

				if(material == Material.AIR)
					ServerLog.info("Failed to load an item because '" + element.getText() + "' does not exist (or the item was air)!");
				else slots.add(new ItemSlot(slot, new ItemStack(material, ConversionUtil.convertStringToInteger(element.attributeValue("amount"), 1))));
			} catch(Exception e) {
				ServerLog.info("Failed to load an item because it threw an exception");
				e.printStackTrace();
			}
		}
		
		String sparents = this.element.attributeValue("parents");
		if(sparents != null) {
			String[] values = sparents.split(" ");
			for(String parent : values)
				if(getMap().getKit(parent) != null)
					parents.add(getMap().getKit(parent));
		}
		
		return new ItemKit(name, slots, parents);
	}
	
}
