package me.parapenguin.overcast.scrimmage.map.region;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.MapLoader;
import me.parapenguin.overcast.scrimmage.utils.RegionUtil;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.dom4j.Element;

public class Region {
	
	@Getter @Setter public static int MAX_BUILD_HEIGHT = 0;
	
	@Getter List<ConfiguredRegion> regions;
	@Getter List<Element> elements;
	
	public Region(Element element) {
		List<Element> elements = new ArrayList<Element>();
		elements.add(element);
		
		Region region = new Region(elements, RegionType.getByElementName(element.getName()));
		this.elements = region.getElements();
		this.regions = region.getRegions();
	}
	
	public Region(List<Element> elements, @NonNull RegionType type) {
		regions = new ArrayList<ConfiguredRegion>();
		this.elements = new ArrayList<Element>();
		
		for(Element element : elements) {
			if(type == RegionType.RECTANGLE && element.getName().equalsIgnoreCase("rectangle")) {
				this.elements.add(element);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getRectangle(element)));
			}
			
			if(type == RegionType.CUBOID && element.getName().equalsIgnoreCase("cuboid")) {
				this.elements.add(element);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getCuboid(element)));
			}
			
			if(type == RegionType.CIRCLE && element.getName().equalsIgnoreCase("circle")) {
				this.elements.add(element);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getCircle(element)));
			}
			
			if(type == RegionType.CYLINDER && element.getName().equalsIgnoreCase("cylinder")) {
				this.elements.add(element);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getCylinder(element)));
			}
			
			if(type == RegionType.SPHERE && element.getName().equalsIgnoreCase("sphere")) {
				this.elements.add(element);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getSphere(element)));
			}
			
			if(type == RegionType.BLOCK && element.getName().equalsIgnoreCase("point")) {
				this.elements.add(element);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getPoint(element)));
			}
		}
	}
	
	/*
	 * The element variable is actually the root element - pretty obvious from the code...
	 */
	public Region(Element element, @NonNull RegionType type) {
		MAX_BUILD_HEIGHT = Scrimmage.getInstance().getServer().getWorlds().get(0).getMaxHeight();
		regions = new ArrayList<ConfiguredRegion>();
		
		List<Element> rectangles = MapLoader.getElements(element, "rectangle");
		List<Element> cuboids = MapLoader.getElements(element, "cuboid");
		List<Element> circles = MapLoader.getElements(element, "circle");
		List<Element> cylinders = MapLoader.getElements(element, "cylinder");
		List<Element> spheres = MapLoader.getElements(element, "sphere");
		List<Element> points = MapLoader.getElements(element, "point");
		
		elements = new ArrayList<Element>();
		
		if(type == RegionType.RECTANGLE || type == RegionType.ALL)
			for(Element rectangle : rectangles) {
				elements.add(rectangle);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getRectangle(rectangle)));
			}
		
		if(type == RegionType.CUBOID || type == RegionType.ALL)
			for(Element cuboid : cuboids) {
				elements.add(cuboid);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getCuboid(cuboid)));
			}
		
		if(type == RegionType.CIRCLE || type == RegionType.ALL)
			for(Element circle : circles) {
				elements.add(circle);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getCircle(circle)));
			}
		
		if(type == RegionType.CYLINDER || type == RegionType.ALL)
			for(Element cylinder : cylinders) {
				elements.add(cylinder);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getCylinder(cylinder)));
			}
		
		if(type == RegionType.SPHERE || type == RegionType.ALL)
			for(Element sphere : spheres) {
				elements.add(sphere);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getSphere(sphere)));
			}
		
		if(type == RegionType.BLOCK || type == RegionType.ALL) {
			points.addAll(MapLoader.getElements(element, "block"));
			for(Element point : points) {
				elements.add(point);
				String name = null;
				if(element.attributeValue("name") != null) name = element.attributeValue("name");
				regions.add(new ConfiguredRegion(name, getPoint(point)));
			}
		}
	}
	
	public List<Location> getLocations() {
		List<Location> locations = new ArrayList<Location>();
		
		for(ConfiguredRegion conf : getRegions())
			locations.addAll(conf.getLocations());
		
		return locations;
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
	
	public List<Location> getCircle(Element circle) {
		/*
		 * Setup Circle Regions
		 * Example: <circle name="something" center="X1,Z1" radius="R"/>
		 * Notes: From 0 to Max Build Height
		 */
		
		List<Location> locations = new ArrayList<Location>();
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
			locations.addAll(possibles);
		}
		
		return locations;
	}
	
	public List<Location> getCylinder(Element cylinder) {
		/*
		 * Setup Cylinder Regions
		 * Example: <cylinder name="something" base="X1,Y1,Z1" radius="R" height="H"/>
		 * Notes: From 0 to the defined height
		 */
		
		List<Location> locations = new ArrayList<Location>();
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
			locations.addAll(possibles);
		}
		
		return locations;
	}
	
	public List<Location> getSphere(Element sphere) {
		/*
		 * Setup Sphere Regions
		 * Example: <sphere name="something" origin="X1,Y1,Z1" radius="R"/>
		 * Notes: I can't remember how my RegionUtil works. That sucks, I guess... hahah
		 */
		
		List<Location> locations = new ArrayList<Location>();
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
			locations.addAll(possibles);
		}
		
		return locations;
	}
	
	public List<Location> getPoint(Element point) {
		/*
		 * Setup Point/Block Regions
		 * Example: <block name="something">X,Y,Z</block> or <point>X,Y,Z</point>
		 * Notes: N/A
		 */
		
		List<Location> locations = new ArrayList<Location>();
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
			locations.addAll(possibles);
		}
		
		return locations;
	}
	
}
