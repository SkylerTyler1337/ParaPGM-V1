package me.parapenguin.overcast.scrimmage;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import me.parapenguin.overcast.scrimmage.map.MapLoader;
import me.parapenguin.overcast.scrimmage.rotation.Rotation;

public class Scrimmage extends JavaPlugin {
	
	static @Getter Scrimmage instance;
	static @Getter @Setter Rotation rotation;
	
	public void onEnable() {
		instance = this;
		
		// Load the maps from the local map repository (no github/download connections this time Harry...)
		File[] files = getMapRoot().listFiles();
		
		for(File file : files)
			if(file.isDirectory())
				for(File contains : file.listFiles())
					if(!contains.isDirectory() && contains.getName().endsWith(".xml") && MapLoader.isLoadable(contains)) {
						MapLoader loader = MapLoader.getLoader(contains);
						Rotation.addMap(loader);
					}
	}
	
	public static int random(int min, int max) {
		return (int) (min + (Math.random() * (max - min)));
	}
	
	public static File getRootFolder() {
		return new File("");
	}
	
	public static File getMapRoot() {
		return new File(getRootFolder().getAbsolutePath() + "/maps/");
	}
	
}
