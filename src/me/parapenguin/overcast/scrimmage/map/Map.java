package me.parapenguin.overcast.scrimmage.map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.util.FileUtil;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.extras.Contributor;
import me.parapenguin.overcast.scrimmage.map.filter.Filter;
import me.parapenguin.overcast.scrimmage.map.region.RegionGroup;
import me.parapenguin.overcast.scrimmage.rotation.RotationSlot;

public class Map {
	
	/*
	 * When the Map object is loaded all locations in any lists must have the world updated - this means that there needs to be 1 map object per play
	 * An "update" method will be added where the RotationSlot must be specified, so that the world can be set to the correct name, etc, etc.
	 * 
	 * Or I could just supply the RotationSlot in the map object? I guess that could work ^.^
	 */
	
	@Getter MapLoader loader;
	@Getter RotationSlot slot;
	
	@Getter String name;
	@Getter String version;
	@Getter String objective;
	@Getter List<String> rules;
	@Getter List<String> authors;
	@Getter List<Contributor> contributors;
	@Getter List<MapTeam> teams;
	@Getter MapTeam observers;
	
	@Getter List<RegionGroup> regions;
	@Getter List<Filter> filters;
	
	public Map(MapLoader loader, RotationSlot slot, String name, String version, String objective, List<String> rules, List<String> authors,
			List<Contributor> contributors, List<MapTeam> teams, MapTeam observers, List<RegionGroup> regions, List<Filter> filters) {
		this.loader = loader;
		this.slot = slot;
		this.name = name;
		this.version = version;
		this.objective = objective;
		this.rules = rules;
		this.authors = authors;
		this.contributors = contributors;
		
		/*
		 * 
		 * Load things where the world has/will change(d)
		 * 
		 */
		
		List<MapTeam> mapTeams = new ArrayList<MapTeam>();
		for(MapTeam team : teams)
			mapTeams.add(team.clone());
		
		MapTeam mapObservers = observers.clone();
		
		List<RegionGroup> mapRegions = new ArrayList<RegionGroup>();
		if(regions != null)
			for(RegionGroup region : regions)
				mapRegions.add(region.clone());
		
		List<Filter> mapFilters = new ArrayList<Filter>();
		if(filters != null)
			for(Filter filter : filters)
				mapFilters.add(filter.clone(teams));
		
		/*
		 * Why isn't this happening?
		 */
		this.teams = mapTeams;
		Scrimmage.getInstance().getLogger().info("Found " + mapTeams.size() + " teams...");
		for(MapTeam team : this.teams) {
			String tName = team.getName();
			List<Location> spawns = new ArrayList<Location>();
			
			for(MapTeamSpawn mts : team.getSpawns()) {
				Scrimmage.getInstance().getLogger().info("Found a spawn for '" + tName + "' containing " + mts.getPossibles().size() + " spawns.");
				spawns.addAll(mts.getPossibles());
			}

			Scrimmage.getInstance().getLogger().info("Found '" + tName + "' with " + spawns.size() + " possible spawns.");
		}
		
		this.observers = mapObservers;
		this.regions = mapRegions;
		this.filters = mapFilters;
	}
	
	public void update() {
		String name = "playing" + Scrimmage.random(1, 10) + "rot" + Scrimmage.getRotation().getLocation(slot);
		/*
		 * What on earth could be null there?
		 * Scrimmage = not null (100% sure)
		 * Rotation = could be null, pretty sure it ain't tho.
		 * Nothing else could be null...
		 */
		update(Scrimmage.getInstance().getServer().getWorld(name) == null);
	}
	
	public void update(boolean load) {
		if(Scrimmage.getRotation() == null)
			Scrimmage.getInstance().getLogger().info("Rotation is null... WTF?!");
		
		String name = "playing" + Scrimmage.random(1, 10) + "rot" + Scrimmage.getRotation().getLocation(slot);
		World world = Scrimmage.getInstance().getServer().getWorld(name);
		
		if(load) {
			File region = new File(Scrimmage.getMapRoot(), "/" + loader.getFolder().getName() + "/region/");
			File levelDat = new File(Scrimmage.getMapRoot(), "/" + loader.getFolder().getName() + "/level.dat");

			File destRoot = new File(Scrimmage.getRootFolder(), "/" + name + "/");
			File destRegion = new File(Scrimmage.getRootFolder(), "/" + name + "/region/");
			File destLevelDat = new File(Scrimmage.getRootFolder(), "/" + name + "/level.dat");
			
			destRoot.mkdirs();
			FileUtil.copy(region, destRegion);
			FileUtil.copy(levelDat, destLevelDat);
			
			WorldCreator wc = new WorldCreator(name);
			world = wc.createWorld();
		}
		
		for(MapTeam team : getTeams())
			for(MapTeamSpawn spawn : team.getSpawns())
				for(Location location : spawn.getPossibles())
					location.setWorld(world);
		
		for(MapTeamSpawn spawn : observers.getSpawns())
			for(Location location : spawn.getPossibles())
				location.setWorld(world);
		
		for(RegionGroup group : regions)
			for(Location location : group.getLocations())
				location.setWorld(world);
	}
	
	public List<Filter> getFilters(Location loc) {
		/*
		List<Filter> found = new ArrayList<Filter>();
		
		for(Filter filter : filters) {
			if(filter)
		}
		*/
		
		return null;
	}
	
}
