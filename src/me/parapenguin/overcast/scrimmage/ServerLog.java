package me.parapenguin.overcast.scrimmage;

public class ServerLog {
	
	public static void info(String message) {
		Scrimmage.getInstance().getLogger().info(message);
	}

	public static void warning(String message) {
		Scrimmage.getInstance().getLogger().warning(message);
	}

	public static void severe(String message) {
		Scrimmage.getInstance().getLogger().severe(message);
	}
	
}
