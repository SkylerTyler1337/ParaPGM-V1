package me.parapenguin.overcast.scrimmage.map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.extras.Contributor;
import me.parapenguin.overcast.scrimmage.map.filter.Filter;
import me.parapenguin.overcast.scrimmage.map.region.ConfiguredRegion;
import me.parapenguin.overcast.scrimmage.map.region.Region;
import me.parapenguin.overcast.scrimmage.map.region.RegionGroup;
import me.parapenguin.overcast.scrimmage.map.region.RegionGroupType;
import me.parapenguin.overcast.scrimmage.map.region.RegionType;
import me.parapenguin.overcast.scrimmage.rotation.RotationSlot;

public class MapLoader {
	
	@Getter File folder;
	@Getter Document doc;
	
	@Getter Map map;

	@Getter String name;
	@Getter String version;
	@Getter String objective;
	@Getter List<String> rules;
	@Getter List<String> authors;
	@Getter List<Contributor> contributors;
	@Getter List<MapTeam> teams;
	@Getter MapTeam observers;
	
	@Getter List<RegionGroup> groups;
	@Getter List<Filter> filters;
	
	@Getter int maxBuildHeight;
	
	@SuppressWarnings("unchecked")
	private MapLoader(File file, Document doc) {
		long start = System.currentTimeMillis();
		
		/*
		 * Load the map and it's attributes now ready for loading into the rotation
		 * 
		 * I'm so retarded...
		 */
		
		this.doc = doc;
		this.folder = file.getParentFile();
		Element root = doc.getRootElement();
		
		this.name = root.elementText("name");
		this.version = root.elementText("version");
		this.objective = root.elementText("objective");
		this.authors = getList("authors", "author");
		this.rules = getList("rules", "rule");
		
		this.contributors = new ArrayList<Contributor>();
		Element contributorsElement = root.element("contributors");
		
		int cur = 0;
		if(contributorsElement != null) {
			while(contributorsElement.elements().size() < cur) {
				if(((Element) contributorsElement.elements().get(cur)).getName().equalsIgnoreCase("contributor")) {
					String contributorName = ((Element) contributorsElement.elements().get(cur)).getText();
					String contribution = ((Element) contributorsElement.elements().get(cur)).attributeValue("contribution");
					contributors.add(new Contributor(contributorName, contribution));
				}
				cur++;
			}
		}
		
		teams = new ArrayList<MapTeam>();
		Element teamsElement = root.element("teams");
		List<Element> teamsList = teamsElement.elements("team");
		for(Element element : teamsList) {
			String teamName = element.getText();
			String teamCap = element.attributeValue("max");
			String teamColor = element.attributeValue("color");
			MapTeam team = new MapTeam(teamName, teamColor, teamCap);
			if(team.getColor() == null || team.getColor() == ChatColor.AQUA)
				Scrimmage.getInstance().getLogger().info("Failed to load team '" + teamName + "' due to having an invalid color supplied!");
			else teams.add(team);
		}
		
		cur = 0;
		while(teamsElement.elements().size() < cur) {
			if(((Element) teamsElement.elements().get(cur)).getName().equalsIgnoreCase("team")) {
				String teamName = ((Element) contributorsElement.elements().get(cur)).getText();
				String teamCap = ((Element) contributorsElement.elements().get(cur)).attributeValue("max");
				String teamColor = ((Element) contributorsElement.elements().get(cur)).attributeValue("color");
				MapTeam team = new MapTeam(teamName, teamColor, teamCap);
				if(team.getColor() == null || team.getColor() == ChatColor.AQUA)
					Scrimmage.getInstance().getLogger().info("Failed to load team '" + teamName + "' due to having an invalid color supplied!");
				else teams.add(team);
			} else {
				ServerLog.info("Element inside <teams> isn't a team...");
			}
			cur++;
		}
		
		for(MapTeam team : teams)
			team.load(root.element("spawns"));
		observers = new MapTeam("Observers", ChatColor.AQUA, -1);
		observers.load(teamsElement);

		groups = new ArrayList<RegionGroup>();
		
		Element regions = root.element("regions");
		if(regions != null) {
			Region shapes = new Region(regions, RegionType.ALL);
			for(ConfiguredRegion conf : shapes.getRegions())
				groups.add(new RegionGroup(conf.getName(), conf.getLocations()));
			
			List<String> names = new ArrayList<String>();
			names.add(RegionGroupType.NEGATIVE.name().toLowerCase());
			names.add(RegionGroupType.UNION.name().toLowerCase());
			names.add(RegionGroupType.COMPLEMENT.name().toLowerCase());
			names.add(RegionGroupType.INTERSECT.name().toLowerCase());
			names.add(RegionGroupType.APPLY.name().toLowerCase());
			
			List<Element> elements = getElements(regions, names);
			for(Element element : elements)
				groups.add(new RegionGroup(element, this));
		}
		
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
		
		this.maxBuildHeight = Region.MAX_BUILD_HEIGHT;
		if(root.element("maxbuildheight") != null && root.element("maxbuildheight").getText() != null) {
			try {
				this.maxBuildHeight = Integer.parseInt(root.element("maxbuildheight").getText());
			} catch(NumberFormatException e) {
				Scrimmage.getInstance().getLogger().info("Failed to load max build height for '" + name + "'...");
			}
		}
		
		// this.filters = filters;
		
		long finish = System.currentTimeMillis();
		Scrimmage.getInstance().getLogger().info("Loaded '" + name + "' taking " + (finish - start) + "ms!");
	}
	
	public Map getMap(RotationSlot slot) {
		return new Map(this, slot, name, version, objective, rules, authors, contributors, teams, observers, groups, filters);
	}
	
	public static boolean isLoadable(File file) {
		SAXReader reader = new SAXReader();
		try {
			reader.read(file);
			return true;
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static MapLoader getLoader(File file) {
		if(!isLoadable(file)) {
			Scrimmage.getInstance().getLogger().info("File !isLoadable() when trying to getLoader() - return null;");
			return null;
		}
		
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(file);
		} catch (DocumentException e) {
			Scrimmage.getInstance().getLogger().info("File fired DocumentException when trying to getLoader() - return null;");
			return null;
		}
		
		if(doc == null)
			Scrimmage.getInstance().getLogger().info("Document is null?");
		return new MapLoader(file, doc);
	}
	
	public RegionGroup getRegionGroup(String name) {
		for(RegionGroup group : getGroups())
			if(group.getName().equalsIgnoreCase(name))
				return group;
		
		return null;
	}
	
	private List<String> getList(String container, String contains) {
		if(doc == null)
			Scrimmage.getInstance().getLogger().info("Document is null?");
		
		Element root = doc.getRootElement();
		
		List<String> contents = new ArrayList<String>();
		Element containerElement = root.element(container);
		
		int cur = 0;
		if(containerElement != null) {
			while(containerElement.elements().size() < cur) {
				if(((Element) containerElement.elements().get(cur)).getName().equalsIgnoreCase(contains))
					contents.add(((Element) containerElement.elements().get(cur)).getText());
				cur++;
			}
		}
		
		return contents;
	}
	
	public static List<Element> getElements(Element from, List<String> names) {
		List<Element> elements = new ArrayList<Element>();
		
		for(String name : names)
			for(Element element : getElements(from))
				if(element.getName().equalsIgnoreCase(name))
					elements.add(element);
		
		return elements;
	}
	
	public static List<Element> getElements(Element from, String name) {
		List<Element> elements = new ArrayList<Element>();
		
		for(Element element : getElements(from))
			if(element.getName().equalsIgnoreCase(name))
				elements.add(element);
		
		return elements;
	}
	
	public static List<Element> getElements(Element from) {
		List<Element> elements = new ArrayList<Element>();
		
		for(Object obj : from.elements())
			if(obj instanceof Element)
				elements.add((Element) obj);
		
		return elements;
	}
	
}
