package me.parapenguin.overcast.scrimmage.map;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.objective.TeamObjective;
import me.parapenguin.overcast.scrimmage.map.objective.WoolObjective;
import me.parapenguin.overcast.scrimmage.map.region.ConfiguredRegion;
import me.parapenguin.overcast.scrimmage.map.region.Region;
import me.parapenguin.overcast.scrimmage.map.region.RegionType;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.utils.ConversionUtil;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.scoreboard.Team;
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
	@Getter Team team;
	
	@Getter String name;
	@Getter ChatColor color;
	@Getter int cap;
	
	@Getter String displayName;
	@Getter List<MapTeamSpawn> spawns;
	@Getter List<TeamObjective> objectives;
	
	private MapTeam(Map map, String name, ChatColor color, int cap, List<MapTeamSpawn> spawns) {
		this.map = map;
		this.name = name;
		setDisplayName();
		this.color = color;
		this.cap = cap;
		
		this.spawns = new ArrayList<MapTeamSpawn>();
		for(MapTeamSpawn spawn : spawns)
			this.spawns.add(spawn.clone());
	}
	
	public MapTeam(Map map, String name, ChatColor color, int cap) {
		this.map = map;
		this.name = name;
		setDisplayName();
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
	
	public void setDisplayName() {
		setDisplayName(name, false);
	}
	
	public void setDisplayName(String name, boolean update) {
		this.displayName = name;
		if(update) getMap().reloadSidebar(false);
	}
	
	public void loadTeam() {
		this.team = getMap().getBoard().registerNewTeam(getName());
		this.team.setPrefix(getColor() + "");
		this.team.setDisplayName(getColor() + getName());
		this.team.setCanSeeFriendlyInvisibles(true);
	}
	
	public int loadTeamObjectives(int start) {
		return loadTeamObjectives(true, start);
	}
	
	public int loadTeamObjectives(boolean objectives, int start) {
		if(this.objectives == null)
			this.objectives = new ArrayList<TeamObjective>();
		
		if(objectives) {
			this.objectives = new ArrayList<TeamObjective>();
			Element root = getMap().getLoader().getDoc().getRootElement();
			
			// LOAD CTW OBJECTIVES HERE...
			for(Element element : MapLoader.getElements(root, "wools")) {
				List<Element> wools = new ArrayList<Element>();
				if(element.attributeValue("team") != null && isThisTeam(element.attributeValue("team"))) {
					wools.addAll(MapLoader.getElements(element, "wool"));
					ServerLog.info("Found " + MapLoader.getElements(element, "wool").size() + " wools!");
				} else
					for(Element element2 : MapLoader.getElements(element, "wools"))
						if(element.attributeValue("team") != null && isThisTeam(element.attributeValue("team")))
							wools.add(element2);
				
				for(Element wool : wools) {
					ServerLog.info("Found wool '" + wool.attributeValue("color") + "'!");
					Element block = wool.element("block");
					Location place = null;
					try {
						String[] xyz = block.getText().split(",");
						double x = Double.parseDouble(xyz[0]);
						double y = Double.parseDouble(xyz[1]);
						double z = Double.parseDouble(xyz[2]);
						
						place = new Location(getSpawn().getSpawn().getWorld(), x, y, z);
					} catch(Exception e) {
						e.printStackTrace();
					}
					
					DyeColor dye = WoolObjective.getDye(wool.attributeValue("color"));
					String display = WordUtils.capitalizeFully(wool.attributeValue("color") + " WOOL");
					if(dye != null && place != null)
						this.objectives.add(new WoolObjective(getMap(), this, display, place, dye));
				}
			}
			
			// LOAD DTM OBJECTIVES HERE...
			
		}

		List<String> names = new ArrayList<String>();
		for(TeamObjective objective : this.objectives) {
			String name = " " + objective.getColor() + objective.getName() + objective.getSpaces();
			if(name.length() > 16) {
				int extra = name.length() - 16;
				String trimmed = objective.getName().substring(0, objective.getName().length() - 1 - extra);
				name = " " + objective.getColor() + trimmed + objective.getSpaces();
			}
			
			names.add(name);
		}
		
		String name = getColor() + getDisplayName();
		if(name.length() > 16) {
			int extra = name.length() - 16;
			name = name.substring(0, name.length() - 1 - extra);
		}
		
		names.add(name);
		
		int score = start;
		for(String offlineName : names) {
			OfflinePlayer player = Scrimmage.getInstance().getServer().getOfflinePlayer(offlineName);
			getMap().getBoardObjective().getScore(player).setScore(score);
			score++;
		}
		
		return names.size(); // start at score 1. Have for example, 4 objecitves 1 team. 5. return 5 + 1, for the next team.
	}
	
	public void load(Element search) {
		load(search, 0);
	}
	
	public void load(Element search, int i) {
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
			spawns.add(new MapTeamSpawn(getMap(), region, region.getElement().getParent().attributeValue("kit")));
		
		this.spawns = spawns;
		
		int spawnCount = spawns.size();
		int locationCount = 0;
		for(MapTeamSpawn spawn : this.spawns)
			locationCount += spawn.getPossibles().size();
		
		ServerLog.info("Loaded " + spawnCount + " spawn(s), containing " + locationCount + " location(s) for '" + name + "'!");
		
		if(!isObserver() && i != -1) loadTeamObjectives(true, i);
	}
	
	public MapTeamSpawn getSpawn() {
		try {
			return spawns.get(Scrimmage.random(0, spawns.size() - 1));
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
	public MapTeamSpawn loadout(Client client, boolean teleport, boolean clear) {
		if(clear) {
			client.getPlayer().getInventory().clear();
			client.getPlayer().getInventory().setHelmet(null);
			client.getPlayer().getInventory().setChestplate(null);
			client.getPlayer().getInventory().setLeggings(null);
			client.getPlayer().getInventory().setBoots(null);
		}
		
		MapTeamSpawn spawn = getSpawn();
		if(teleport) client.getPlayer().teleport(spawn.getSpawn());
		
		if(isObserver()) {
			client.getPerms().setPermission("worldedit.navigation.thru.tool", true);
			client.getPerms().setPermission("worldedit.navigation.jumpto.tool", true);
			client.getPlayer().setGameMode(GameMode.CREATIVE);
			client.getPlayer().setCollidesWithEntities(false);
			client.getPlayer().getInventory().setItem(0, new ItemStack(Material.COMPASS));
		} else {
			client.getPerms().unsetPermission("worldedit.navigation.thru.tool");
			client.getPerms().unsetPermission("worldedit.navigation.jumpto.tool");
			client.getPlayer().setGameMode(GameMode.SURVIVAL);
			client.getPlayer().setCollidesWithEntities(true);
			
			// Load the kit here.
			if(spawn.getKit() != null)
				spawn.getKit().load(client);
		}

		client.getPlayer().updateInventory();
		return spawn;
	}
	
	public boolean isThisTeam(String check) {
		return getColorName().toLowerCase().contains(check.toLowerCase());
	}
	
	public List<WoolObjective> getWools() {
		List<WoolObjective> wools = new ArrayList<WoolObjective>();
		
		if(getObjectives() == null)
			return wools;
		
		for(TeamObjective obj : getObjectives())
			if(obj instanceof WoolObjective)
				wools.add((WoolObjective) obj);
		
		return wools;
	}
	
	@SuppressWarnings("deprecation")
	public WoolObjective getWool(Block block) {
		for(WoolObjective wool : getWools())
			if(new Wool(block.getType(), block.getData()).getColor() == wool.getWool())
				return wool;
		
		return null;
	}
	
	public WoolObjective getWool(Location location) {
		for(WoolObjective wool : getWools())
			if(wool.isLocation(location))
				return wool;
		
		return null;
	}
	
	public int getCompleted() {
		int complete = 0;
		
		for(TeamObjective objective : getObjectives())
			if(objective.isComplete())
				complete++;
		
		return complete;
	}
	
	public int getTouches() {
		int complete = 0;
		
		for(TeamObjective objective : getObjectives())
			complete += objective.getTouched();
		
		return complete;
	}
	
	public static MapTeam getTeamByChatColor(List<MapTeam> teams, ChatColor color) {
		for(MapTeam team : teams)
			if(team.getColor() == color)
				return team;
		
		return null;
	}
	
}
