package me.parapenguin.overcast.scrimmage.map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.util.FileUtil;
import org.dom4j.Element;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.extras.Contributor;
import me.parapenguin.overcast.scrimmage.map.filter.Filter;
import me.parapenguin.overcast.scrimmage.map.region.ConfiguredRegion;
import me.parapenguin.overcast.scrimmage.map.region.Region;
import me.parapenguin.overcast.scrimmage.map.region.RegionGroup;
import me.parapenguin.overcast.scrimmage.map.region.RegionGroupType;
import me.parapenguin.overcast.scrimmage.map.region.RegionType;
import me.parapenguin.overcast.scrimmage.rotation.RotationSlot;

public class Map {
	
	/*
	 * When the Map object is loaded all locations in any lists must have the world updated
	 * this means that there needs to be 1 map object per play
	 * An "update" method will be added where the RotationSlot must be specified, so that the world can be set to the correct name, etc, etc.
	 * 
	 * Or I could just supply the RotationSlot in the map object? I guess that could work ^.^
	 */
	
	@Getter MapLoader loader;
	@Getter RotationSlot slot;
	@Getter World world;
	
	@Getter String name;
	@Getter String version;
	@Getter String objective;
	@Getter List<String> rules;
	@Getter List<Contributor> authors;
	@Getter List<Contributor> contributors;
	@Getter List<MapTeam> teams;
	@Getter MapTeam observers;
	
	@Getter List<RegionGroup> regions;
	@Getter List<Filter> filters;
	
	public Map(MapLoader loader, RotationSlot slot, String name, String version, String objective, List<String> rules,
			List<Contributor> authors, List<Contributor> contributors, List<MapTeam> teams, MapTeam observers) {
		this.loader = loader;
		this.slot = slot;
		this.name = name;
		this.version = version;
		this.objective = objective;
		this.rules = rules;
		this.authors = authors;
		this.contributors = contributors;
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
	
	@SuppressWarnings("unchecked")
	public void update(boolean load) {
		long start = System.currentTimeMillis();
		Scrimmage.getInstance().getLogger().info("Started loading '" + name + "'!");
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
		
		Scrimmage.getInstance().getLogger().info("Loaded the World for '" + this.name + "' taking "
				+ (System.currentTimeMillis() - start) + "ms!");
		long step = System.currentTimeMillis();
		Scrimmage.getInstance().getLogger().info("Total load time for '" + this.name + "' is currently "
				+ (System.currentTimeMillis() - start) + "ms!");
		
		this.world = world;
		
		if(world != null) {
			Element root = loader.getDoc().getRootElement();
			
			teams = new ArrayList<MapTeam>();
			Element teamsElement = root.element("teams");
			List<Element> teamsList = teamsElement.elements("team");
			for(Element element : teamsList) {
				String teamName = element.getText();
				String teamCap = element.attributeValue("max");
				String teamColor = element.attributeValue("color");
				MapTeam team = new MapTeam(this, teamName, teamColor, teamCap);
				if(team.getColor() == null || team.getColor() == ChatColor.AQUA)
					Scrimmage.getInstance().getLogger().info("Failed to load team '"
							+ teamName + "' due to having an invalid color supplied!");
				else teams.add(team);
			}

			Scrimmage.getInstance().getLogger().info("Loaded the Teams for '" + this.name + "' taking "
					+ (System.currentTimeMillis() - step) + "ms!");
			step = System.currentTimeMillis();
			Scrimmage.getInstance().getLogger().info("Total load time for '" + this.name + "' is currently "
					+ (System.currentTimeMillis() - start) + "ms!");
			
			for(MapTeam team : teams)
				team.load(root.element("spawns"));
			observers = new MapTeam(this, "Observers", ChatColor.AQUA, -1);
			observers.load(root.element("spawns"));

			Scrimmage.getInstance().getLogger().info("Loaded the Spawns for '" + this.name + "' taking "
					+ (System.currentTimeMillis() - step) + "ms!");
			step = System.currentTimeMillis();
			Scrimmage.getInstance().getLogger().info("Total load time for '" + this.name + "' is currently "
					+ (System.currentTimeMillis() - start) + "ms!");

			regions = new ArrayList<RegionGroup>();
			
			Element regions = root.element("regions");
			if(regions != null) {
				Region shapes = new Region(this, regions, RegionType.ALL);
				for(ConfiguredRegion conf : shapes.getRegions())
					this.regions.add(new RegionGroup(conf.getName(), conf.getLocations()));
				
				List<String> names = new ArrayList<String>();
				names.add(RegionGroupType.NEGATIVE.name().toLowerCase());
				names.add(RegionGroupType.UNION.name().toLowerCase());
				names.add(RegionGroupType.COMPLEMENT.name().toLowerCase());
				names.add(RegionGroupType.INTERSECT.name().toLowerCase());
				names.add(RegionGroupType.APPLY.name().toLowerCase());
				
				List<Element> elements = MapLoader.getElements(regions, names);
				for(Element element : elements)
					this.regions.add(new RegionGroup(element, this));
			}

			Scrimmage.getInstance().getLogger().info("Loaded the Regions for '" + this.name + "' taking "
					+ (System.currentTimeMillis() - step) + "ms!");
			step = System.currentTimeMillis();
			Scrimmage.getInstance().getLogger().info("Total load time for '" + this.name + "' is currently "
					+ (System.currentTimeMillis() - start) + "ms!");
			
			/*
			List<Element> negatives = getElements(regions, RegionGroupType.NEGATIVE.name().toLowerCase());
			List<Element> unions = getElements(regions, RegionGroupType.UNION.name().toLowerCase());
			List<Element> complements = getElements(regions, RegionGroupType.COMPLEMENT.name().toLowerCase());
			List<Element> intersects = getElements(regions, RegionGroupType.INTERSECT.name().toLowerCase());
			
			List<Element> all = new ArrayList<Element>();
			all.addAll(negatives);
			all.addAll(unions);
			all.addAll(complements);
			all.addAll(intersects);
			*/
			
			/*
			 * Going to skip filters for now, I want to see the plugin working ;-;
			 */
		}
		long finish = System.currentTimeMillis();
		Scrimmage.getInstance().getLogger().info("Loaded '" + this.name + "' taking " + (finish - start) + "ms!");
	}
	
	public RegionGroup getRegionGroup(String name) {
		for(RegionGroup group : getRegions())
			if(group.getName().equalsIgnoreCase(name))
				return group;
		
		return null;
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
