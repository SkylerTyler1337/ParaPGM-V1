package me.parapenguin.overcast.scrimmage.map;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
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
		 */
		
		for(Element element : teamElements) {
			int maxHeight = Scrimmage.getInstance().getServer().getWorlds().get(0).getMaxHeight();
			
			/*
			 * Setup Rectangle Regions
			 * Example: <rectangle name="something" min="X1,Z1" max="X2,Z2"/>
			 * Notes: Not sure if there is no Y value because it's meant to be infinite Y (bottom -> top) or just that 1 layer...
			 * It's just 1 layer. Nope, it has to be infinite otherwise it would be at void level.
			 * For now I'm only going to do the min & max attrs because this is a spawn-region rather than a region-region.
			 * To anybody who has read this paragraph of mine... gj. lol
			 */
			
			List<Element> rectangles = MapLoader.getElements(element, "rectangle");
			for(Element rectangle : rectangles) {
				boolean failed = false;
				String min = rectangle.attributeValue("min");
				String max = rectangle.attributeValue("max");
				
				double minX = 0;
				double minY = 0;
				double minZ = 0;
				
				double maxX = 0;
				double maxY = maxHeight;
				double maxZ = 0;
				
				String[] minSplit = min.split(",");
				String minXS = minSplit[0];
				String minZS = minSplit[1];
				
				String[] maxSplit = max.split(",");
				String maxXS = maxSplit[0];
				String maxZS = maxSplit[1];
				
				try {
					minX = Double.parseDouble(minXS);
					minZ = Double.parseDouble(minZS);
					
					maxX = Double.parseDouble(maxXS);
					maxZ = Double.parseDouble(maxZS);
				} catch(NumberFormatException nfe) {
					nfe.printStackTrace();
					failed = true;
				}
				
				if(!failed) {
					Location minL = new Location(null, minX, minY, minZ);
					Location maxL = new Location(null, maxX, maxY, maxZ);
					List<Block> possibleBlocks = RegionUtil.contains(minL, maxL);
					
					List<Location> possibles = new ArrayList<Location>();
					for(Block block : possibleBlocks)
						possibles.add(block.getLocation());
					
					MapTeamSpawn mts = new MapTeamSpawn(possibles);
					spawns.add(mts);
				}
			}
			
			/*
			 * Setup Cuboid Regions
			 * Example: <cuboid name="something" min="X1,Y1,Z1" max="X2,Y2,Z2"/>
			 * Notes: N/A
			 */
			
			List<Element> cuboids = MapLoader.getElements(element, "cuboid");
			for(Element cuboid : cuboids) {
				boolean failed = false;
				String min = cuboid.attributeValue("min");
				String max = cuboid.attributeValue("max");
				
				double minX = 0;
				double minY = 0;
				double minZ = 0;
				
				double maxX = 0;
				double maxY = 0;
				double maxZ = 0;
				
				String[] minSplit = min.split(",");
				String minXS = minSplit[0];
				String minYS = minSplit[1];
				String minZS = minSplit[2];
				
				String[] maxSplit = max.split(",");
				String maxXS = maxSplit[0];
				String maxYS = maxSplit[1];
				String maxZS = maxSplit[2];
				
				try {
					minX = Double.parseDouble(minXS);
					minY = Double.parseDouble(minYS);
					minZ = Double.parseDouble(minZS);
					
					maxX = Double.parseDouble(maxXS);
					maxY = Double.parseDouble(maxYS);
					maxZ = Double.parseDouble(maxZS);
				} catch(NumberFormatException nfe) {
					nfe.printStackTrace();
					failed = true;
				}
				
				if(!failed) {
					Location minL = new Location(null, minX, minY, minZ);
					Location maxL = new Location(null, maxX, maxY, maxZ);
					List<Block> possibleBlocks = RegionUtil.contains(minL, maxL);
					
					List<Location> possibles = new ArrayList<Location>();
					for(Block block : possibleBlocks)
						possibles.add(block.getLocation());
					
					MapTeamSpawn mts = new MapTeamSpawn(possibles);
					spawns.add(mts);
				}
			}
			
			/*
			 * Setup Circle Regions
			 * Example: <circle name="something" center="X1,Z1" radius="R"/>
			 * Notes: From 0 to Max Build Height
			 */
			
			List<Element> circles = MapLoader.getElements(element, "circle");
			for(Element circle : circles) {
				boolean failed = false;
				String center = circle.attributeValue("center");
				String radius = circle.attributeValue("radius");
				
				double cR = 0;
				double cX = 0;
				double cY = 0;
				double cZ = 0;
				
				String[] cSplit = center.split(",");
				String cXS = cSplit[0];
				String cYS = cSplit[1];
				String cZS = cSplit[2];
				
				try {
					cR = Double.parseDouble(radius);
					cX = Double.parseDouble(cXS);
					cY = Double.parseDouble(cYS);
					cZ = Double.parseDouble(cZS);
				} catch(NumberFormatException nfe) {
					nfe.printStackTrace();
					failed = true;
				}
				
				if(!failed) {
					Location centerL = new Location(null, cX, cY, cZ);
					
					List<Location> possibles = RegionUtil.circle(centerL, cR, maxHeight, false, false);
					MapTeamSpawn mts = new MapTeamSpawn(possibles);
					spawns.add(mts);
				}
			}
			
			/*
			 * Setup Cylinder Regions
			 * Example: <cylinder name="something" base="X1,Y1,Z1" radius="R" height="H"/>
			 * Notes: From 0 to the defined height
			 */
			
			List<Element> cylinders = MapLoader.getElements(element, "cylinder");
			for(Element cylinder : cylinders) {
				boolean failed = false;
				String center = cylinder.attributeValue("center");
				String radius = cylinder.attributeValue("radius");
				String height = cylinder.attributeValue("height");
				
				double cR = 0;
				double cH = 0;
				double cX = 0;
				double cY = 0;
				double cZ = 0;
				
				String[] cSplit = center.split(",");
				String cXS = cSplit[0];
				String cYS = cSplit[1];
				String cZS = cSplit[2];
				
				try {
					cR = Double.parseDouble(radius);
					cH = Double.parseDouble(height);
					cX = Double.parseDouble(cXS);
					cY = Double.parseDouble(cYS);
					cZ = Double.parseDouble(cZS);
				} catch(NumberFormatException nfe) {
					nfe.printStackTrace();
					failed = true;
				}
				
				if(!failed) {
					Location centerL = new Location(null, cX, cY, cZ);
					
					List<Location> possibles = RegionUtil.circle(centerL, cR, cH, false, false);
					MapTeamSpawn mts = new MapTeamSpawn(possibles);
					spawns.add(mts);
				}
			}
			
			/*
			 * Setup Sphere Regions
			 * Example: <sphere name="something" origin="X1,Y1,Z1" radius="R"/>
			 * Notes: I can't remember how my RegionUtil works. That sucks, I guess... hahah
			 */
			
			List<Element> spheres = MapLoader.getElements(element, "sphere");
			for(Element sphere : spheres) {
				boolean failed = false;
				String center = sphere.attributeValue("center");
				String radius = sphere.attributeValue("radius");
				
				double cR = 0;
				double cH = 0;
				double cX = 0;
				double cY = 0;
				double cZ = 0;
				
				String[] cSplit = center.split(",");
				String cXS = cSplit[0];
				String cYS = cSplit[1];
				String cZS = cSplit[2];
				
				try {
					cR = Double.parseDouble(radius);
					cH = cR;
					cX = Double.parseDouble(cXS);
					cY = Double.parseDouble(cYS);
					cZ = Double.parseDouble(cZS);
				} catch(NumberFormatException nfe) {
					nfe.printStackTrace();
					failed = true;
				}
				
				if(!failed) {
					Location centerL = new Location(null, cX, cY, cZ);
					
					List<Location> possibles = RegionUtil.circle(centerL, cR, cH, false, false);
					MapTeamSpawn mts = new MapTeamSpawn(possibles);
					spawns.add(mts);
				}
			}
			
			/*
			 * Setup Point/Block Regions
			 * Example: <block name="something">X,Y,Z</block> or <point>X,Y,Z</point>
			 * Notes: N/A
			 */
			
			List<Element> points = MapLoader.getElements(element, "point");
			points.addAll(MapLoader.getElements(element, "block"));
			for(Element point : points) {
				boolean failed = false;
				String center = point.getText();
				
				double cX = 0;
				double cY = 0;
				double cZ = 0;
				
				String[] cSplit = center.split(",");
				String cXS = cSplit[0];
				String cYS = cSplit[1];
				String cZS = cSplit[2];
				
				try {
					cX = Double.parseDouble(cXS);
					cY = Double.parseDouble(cYS);
					cZ = Double.parseDouble(cZS);
				} catch(NumberFormatException nfe) {
					nfe.printStackTrace();
					failed = true;
				}
				
				if(!failed) {
					Location location = new Location(null, cX, cY, cZ);
					
					List<Location> possibles = new ArrayList<Location>();
					possibles.add(location);
					MapTeamSpawn mts = new MapTeamSpawn(possibles);
					spawns.add(mts);
				}
			}
		}
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
