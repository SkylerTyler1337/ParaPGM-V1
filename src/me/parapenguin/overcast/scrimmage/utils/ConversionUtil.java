package me.parapenguin.overcast.scrimmage.utils;

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
	
}
