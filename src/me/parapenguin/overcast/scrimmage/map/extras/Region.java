package me.parapenguin.overcast.scrimmage.map.extras;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.MapLoader;
import me.parapenguin.overcast.scrimmage.map.MapTeamSpawn;
import me.parapenguin.overcast.scrimmage.utils.RegionUtil;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.dom4j.Element;

public class Region {
	
	private static int MAX_BUILD_HEIGHT = 0;
	
	@Getter List<Location> locations;
	
	public Region(Element element, RegionType type) {
		MAX_BUILD_HEIGHT = Scrimmage.getInstance().getServer().getWorlds().get(0).getMaxHeight();
		locations = new ArrayList<Location>();
		
		if(type == RegionType.RECTANGLE) {
			List<Element> rectangles = MapLoader.getElements(element, "rectangle");
			for(Element rectangle : rectangles)
				locations.addAll(getRectangle(rectangle));
		}
		
		if(type == RegionType.CUBOID) {
			List<Element> cuboids = MapLoader.getElements(element, "cuboid");
			for(Element cuboid : cuboids)
				locations.addAll(getCuboid(cuboid));
		}
		
		List<Element> circles = MapLoader.getElements(element, "circle");
		for(Element circle : circles) {
			/*
			 * Setup Circle Regions
			 * Example: <circle name="something" center="X1,Z1" radius="R"/>
			 * Notes: From 0 to Max Build Height
			 */
			
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
				
				List<Location> possibles = RegionUtil.circle(centerL, cR, MAX_BUILD_HEIGHT, false, false);
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
	
	public List<Location> getRectangle(Element rectangle) {
		/*
		 * Setup Rectangle Regions
		 * Example: <rectangle name="something" min="X1,Z1" max="X2,Z2"/>
		 * Notes: Not sure if there is no Y value because it's meant to be infinite Y (bottom -> top) or just that 1 layer...
		 * It's just 1 layer. Nope, it has to be infinite otherwise it would be at void level.
		 * For now I'm only going to do the min & max attrs because this is a spawn-region rather than a region-region.
		 * To anybody who has read this paragraph of mine... gj. lol
		 */
		
		List<Location> locations = new ArrayList<Location>();
		boolean failed = false;
		String min = rectangle.attributeValue("min");
		String max = rectangle.attributeValue("max");
		
		double minX = 0;
		double minY = 0;
		double minZ = 0;
		
		double maxX = 0;
		double maxY = MAX_BUILD_HEIGHT;
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
			
			locations.addAll(possibles);
		}
		
		return locations;
	}
	
	public List<Location> getCuboid(Element cuboid) {
		/*
		 * Setup Cuboid Regions
		 * Example: <cuboid name="something" min="X1,Y1,Z1" max="X2,Y2,Z2"/>
		 * Notes: N/A
		 */

		List<Location> locations = new ArrayList<Location>();
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
			
			locations.addAll(possibles);
		}
		
		return locations;
	}
	
}
