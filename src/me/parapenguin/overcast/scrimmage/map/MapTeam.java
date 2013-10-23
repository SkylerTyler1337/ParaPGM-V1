package me.parapenguin.overcast.scrimmage.map;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.region.ConfiguredRegion;
import me.parapenguin.overcast.scrimmage.map.region.Region;
import me.parapenguin.overcast.scrimmage.map.region.RegionType;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.utils.ConversionUtil;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.dom4j.Element;

public class MapTeam {
	
	public static int DEFAULT_TEAM_CAP = 8;
	
	public static ChatColor getChatColorFromString(String string) {
		for(ChatColor color : ChatColor.values())
			if(color.name().equalsIgnoreCase(string.replaceAll(" ", "_")))
				return color;
		
		return null;
	}
	
	@Getter Map map;
	
	@Getter String name;
	@Getter ChatColor color;
	@Getter int cap;
	
	@Getter List<MapTeamSpawn> spawns;
	
	public MapTeam(Map map, String name, ChatColor color, int cap, List<MapTeamSpawn> spawns) {
		this.map = map;
		this.name = name;
		this.color = color;
		this.cap = cap;
		
		this.spawns = new ArrayList<MapTeamSpawn>();
		for(MapTeamSpawn spawn : spawns)
			this.spawns.add(spawn.clone());
	}
	
	public MapTeam(Map map, String name, ChatColor color, int cap) {
		this.map = map;
		this.name = name;
		if(color == ChatColor.AQUA) name = "Observers";
		this.color = color;
		this.cap = cap;
	}
	
	public MapTeam(Map map, String name, String color, String cap) {
		this(map, name, getChatColorFromString(color), ConversionUtil.convertStringToInteger(cap, DEFAULT_TEAM_CAP));
	}
	
	public MapTeam(Map map, String name, ChatColor color, String cap) {
		this(map, name, color, ConversionUtil.convertStringToInteger(cap, DEFAULT_TEAM_CAP));
	}
	
	public MapTeam(Map map, String name, String color, int cap) {
		this(map, name, getChatColorFromString(color), cap);
	}
	
	public void load(Element search) {
		String tag = "spawn";
		if(isObserver())
			tag = "default";
		
		List<MapTeamSpawn> spawns = new ArrayList<MapTeamSpawn>();
		List<Element> spawnElements = MapLoader.getElements(search, tag);
		
		List<Element> teamElements = new ArrayList<Element>();
		
		for(Element element : spawnElements)
			if(isObserver() || (element.attributeValue("team") != null
				&& getColorName().toLowerCase().contains(element.attributeValue("team").toLowerCase())))
				teamElements.add(element);
		
		if(!isObserver())
			for(Element element : MapLoader.getElements(search, "spawns"))
				if(element.attributeValue("team") != null
					&& getColorName().toLowerCase().contains(element.attributeValue("team").toLowerCase())) {
					search = element;
					
					spawnElements = MapLoader.getElements(search, tag);
					for(Element element2 : spawnElements)
						teamElements.add(element2);
				}
		
		/*
		 * Now I have to make these spawns into their actual spawn value regions/points... * fun *
		 * Dat class shift...
		 */
		
		List<Element> loadableElements = new ArrayList<Element>();
		for(Element element : teamElements)
			loadableElements.addAll(MapLoader.getElements(element));
		
		Region regions = new Region(map, loadableElements, RegionType.ALL);
		
		List<ConfiguredRegion> configured = regions.getRegions();
		for(ConfiguredRegion region : configured)
			spawns.add(new MapTeamSpawn(region));
		
		this.spawns = spawns;
		
		int spawnCount = spawns.size();
		int locationCount = 0;
		for(MapTeamSpawn spawn : this.spawns)
			locationCount += spawn.getPossibles().size();
		
		ServerLog.info("Loaded " + spawnCount + " spawn(s), containing " + locationCount + " location(s) for '" + name + "'!");
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
	
	public MapTeam clone() {
		return new MapTeam(getMap(), getName(), getColor(), getCap(), getSpawns());
	}
	
	public List<Client> getPlayers() {
		List<Client> clients = new ArrayList<Client>();
		
		for(Client client : Client.getClients())
			if(client.getTeam() == this)
				clients.add(client);
		
		return clients;
	}
	
	@SuppressWarnings("deprecation")
	public void loadout(Client client) {
		client.getPlayer().getInventory().clear();
		client.getPlayer().getInventory().setHelmet(null);
		client.getPlayer().getInventory().setChestplate(null);
		client.getPlayer().getInventory().setLeggings(null);
		client.getPlayer().getInventory().setBoots(null);
		client.getPlayer().updateInventory();
		
		if(isObserver()) {
			client.getPerms().setPermission("worldedit.navigation.thru.tool", true);
			client.getPerms().setPermission("worldedit.navigation.jump.tool", true);
			client.getPlayer().getInventory().setItem(0, new ItemStack(Material.COMPASS));
		}

		client.getPerms().unsetPermission("worldedit.navigation.thru.tool");
		client.getPerms().unsetPermission("worldedit.navigation.jump.tool");
		
	}
	
	public static MapTeam getTeamByChatColor(List<MapTeam> teams, ChatColor color) {
		for(MapTeam team : teams)
			if(team.getColor() == color)
				return team;
		
		return null;
	}
	
}
