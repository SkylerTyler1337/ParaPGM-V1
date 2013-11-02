package me.parapenguin.overcast.scrimmage.utils;

import java.awt.Color;

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
		for(Enchantment option : Enchantment.values())
			if(option.getName().replaceAll("_", " ").equalsIgnoreCase(convert) || option.getName().equalsIgnoreCase(convert))
				return option;
		
		return null;
	}
	
	public static PotionEffectType convertStringToPotionEffectType(String convert) {
		for(PotionEffectType option : PotionEffectType.values())
			if(option.getName().replaceAll("_", " ").equalsIgnoreCase(convert) || option.getName().equalsIgnoreCase(convert))
				return option;
		
		return null;
	}
	
	public static Color convertHexStringToColor(String colorStr) {
	    return new Color(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}
	
}
