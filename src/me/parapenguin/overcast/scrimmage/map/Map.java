package me.parapenguin.overcast.scrimmage.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;
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
	
	public Map(RotationSlot slot, String name, String version, String objective, List<String> rules, List<String> authors,
			List<Contributor> contributors, List<MapTeam> teams, MapTeam observers, List<RegionGroup> regions, List<Filter> filters) {
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
		for(RegionGroup region : regions)
			mapRegions.add(region.clone());
		
		List<Filter> mapFilters = new ArrayList<Filter>();
		for(Filter filter : filters)
			mapFilters.add(filter.clone(teams));
		
		this.teams = mapTeams;
		this.observers = mapObservers;
		this.regions = mapRegions;
		this.filters = mapFilters;
	}
	
	public void update(World world) {
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
	
}
