package me.parapenguin.overcast.scrimmage.map;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.extras.ConfiguredRegion;
import me.parapenguin.overcast.scrimmage.map.extras.Region;
import me.parapenguin.overcast.scrimmage.map.extras.RegionType;
import me.parapenguin.overcast.scrimmage.utils.ConversionUtil;
import me.parapenguin.overcast.scrimmage.utils.RegionUtil;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.dom4j.Element;

public class MapTeam {
	
	public static int DEFAULT_TEAM_CAP = 8;
	
	public static ChatColor getChatColorFromString(String string) {
		for(ChatColor color : ChatColor.values())
			if(color.name().equalsIgnoreCase(string.replaceAll(" ", "_")))
				return color;
		
		return null;
	}
	
	@Getter String name;
	@Getter ChatColor color;
	@Getter int cap;
	
	@Getter List<MapTeamSpawn> spawns;
	
	public MapTeam(String name, ChatColor color, int cap) {
		this.name = name;
		this.color = color;
		this.cap = cap;
	}
	
	public MapTeam(String name, String color, String cap) {
		this(name, getChatColorFromString(color), ConversionUtil.convertStringToInteger(cap, DEFAULT_TEAM_CAP));
	}
	
	public MapTeam(String name, ChatColor color, String cap) {
		this(name, color, ConversionUtil.convertStringToInteger(cap, DEFAULT_TEAM_CAP));
	}
	
	public MapTeam(String name, String color, int cap) {
		this(name, getChatColorFromString(color), cap);
	}
	
	public void load(Element search) {
		String tag = "spawn";
		if(isObserver())
			tag = "default";
		
		List<MapTeamSpawn> spawns = new ArrayList<MapTeamSpawn>();
		List<Element> spawnElements = MapLoader.getElements(search, tag);
		
		List<Element> teamElements = new ArrayList<Element>();
		
		for(Element element : spawnElements)
			if(isObserver() || element.attributeValue("team").equalsIgnoreCase(getColorName()))
				teamElements.add(element);
		
		if(!isObserver()) {
			for(Element element : MapLoader.getElements(search, "spawns"))
				if(element.attributeValue("team").equalsIgnoreCase(getColorName())) {
					search = element;
					
					spawnElements = MapLoader.getElements(search, tag);
					for(Element element2 : spawnElements)
						teamElements.add(element2);
				}
					
		}
		
		/*
		 * Now I have to make these spawns into their actual spawn value regions/points... * fun *
		 * Dat class shift...
		 */

		Region regions = new Region(teamElements, RegionType.ALL);
		
		List<ConfiguredRegion> configured = regions.getRegions();
		for(ConfiguredRegion region : configured)
			spawns.add(new MapTeamSpawn(region));
	}
	
	public Location getSpawn() {
		try {
			return spawns.get(Scrimmage.random(0, spawns.size() - 1)).getSpawn();
		} catch(IndexOutOfBoundsException ioobe) {
			// What a lovely Exception label... hahah
			ioobe.printStackTrace();
		}
		
		return null;
	}
	
	public String getColorName() {
		return color.name().replaceAll("_", " ");
	}
	
	public boolean isObserver() {
		return ChatColor.AQUA == getColor();
	}
	
}
