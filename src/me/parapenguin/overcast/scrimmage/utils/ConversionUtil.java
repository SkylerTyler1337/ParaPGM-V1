package me.parapenguin.overcast.scrimmage.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

public class ConversionUtil {
	
	public static int convertStringToInteger(String value) {
		return convertStringToInteger(value, -1);
	}
	
	public static int convertStringToInteger(String value, int fallback) {
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException e) {
			return fallback;
		}
	}
	
	public static Material convertStringToMaterial(String convert) {
		for(Material option : Material.values())
			if(option.name().replaceAll("_", " ").equalsIgnoreCase(convert) || option.name().equalsIgnoreCase(convert))
				return option;
		
		return Material.AIR;
	}
	
	public static Enchantment convertStringToEnchantment(String convert) {
		return Enchantment.getByName(convert);
	}
	
	public static PotionEffectType convertStringToPotionEffectType(String convert) {
		return PotionEffectType.getByName(convert);
	}
	
}
