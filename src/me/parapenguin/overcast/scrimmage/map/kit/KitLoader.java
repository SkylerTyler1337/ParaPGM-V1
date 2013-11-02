package me.parapenguin.overcast.scrimmage.map.kit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
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
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		
		String[] types = new String[]{"item", "helmet", "chestplate", "leggings", "boots"};
		for(String search : types)
			for(Element element : MapLoader.getElements(this.element, search))
				slots.add(compileItem(element, search));
		
		String sparents = this.element.attributeValue("parents");
		if(sparents != null) {
			String[] values = sparents.split(" ");
			for(String parent : values)
				if(getMap().getKit(parent) != null)
					parents.add(getMap().getKit(parent));
		}
		
		/*
		 * Load Potion Effects!
		 * <potion duration="5" amplifier="1">heal</potion>
		 */
		
		for(Element element : MapLoader.getElements(this.element, search))
			slots.add(compileItem(element, search));
		
		if(effects.size() == 0)
			return new ItemKit(name, slots, parents);
		else return new ItemKit(name, slots, parents, effects);
	}
	
	public ItemSlot compileItem(Element element, String name) {
		/*
		 * Load the Items with their Item Slawt and sheeeeeeeeet!
		 * Example: <item slot="0">iron sword</item>
		 */
		
		try {
			int slot = 0;
			if(name.equalsIgnoreCase("item")) slot = Integer.parseInt(element.attributeValue("slot"));
			else if(name.equalsIgnoreCase("helmet")) slot = -1;
			else if(name.equalsIgnoreCase("chestplate")) slot = -2;
			else if(name.equalsIgnoreCase("leggings")) slot = -3;
			else if(name.equalsIgnoreCase("boots")) slot = -4;
			
			Material material = ConversionUtil.convertStringToMaterial(element.getText());
			ItemStack stack = new ItemStack(material, ConversionUtil.convertStringToInteger(element.attributeValue("amount"), 1));
			
			if(isLeather(material) && element.attributeValue("color") != null) {
				int rgb = java.awt.Color.decode(element.attributeValue("color")).getRGB();
				Color color = Color.fromRGB(rgb);
				LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
				meta.setColor(color);
				stack.setItemMeta(meta);
			}
			
			if(element.attributeValue("damage") != null)
				try { stack.setDurability(Short.parseShort(element.attributeValue("damage"))); } catch(NumberFormatException e) { }
			
			if(element.attributeValue("name") != null) {
				String display = element.attributeValue("name").replaceAll("`", "§");
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(display);
				stack.setItemMeta(meta);
			}
			
			if(element.attributeValue("lore") != null) {
				String lS = element.attributeValue("lore");
				List<String> loreStrings = new ArrayList<String>();
				
				if(!lS.contains("|")) loreStrings.add(lS);
				else {
					for(String lore : lS.split("|"))
						loreStrings.add(lore);
				}
				
				String display = element.attributeValue("name").replaceAll("`", "§");
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(display);
				stack.setItemMeta(meta);
			}
			
			String enString = element.attributeValue("enchantment");
			if(enString != null) {
				List<String> enchParse = new ArrayList<String>();
				if(enString.contains(";"))
					for(String enchant : enString.split(";"))
						enchParse.add(enchant);
				else enchParse.add(enString);
				
				for(String enchant : enchParse) {
					String title = "";
					int level = 1;
					if(enchant.contains(":")) {
						title = enchant.split(":")[0];
						
						try {
							level = Integer.parseInt(enchant.split(":")[0]);
						} catch(NumberFormatException e) {
							// ignore, leave level as 1.
						}
					}
					
					Enchantment enchantment = ConversionUtil.convertStringToEnchantment(title);
					if(enchantment != null) stack.addEnchantment(enchantment, level);
				}
			}

			if(material == Material.AIR)
				ServerLog.info("Failed to load an item because '" + element.getText() + "' does not exist (or the item was air)!");
			else return new ItemSlot(slot, stack);
		} catch(Exception e) {
			ServerLog.info("Failed to load an item because it threw an exception");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean isLeather(Material material) {
		return material == Material.LEATHER_BOOTS || material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_HELMET;
	}
	
}
