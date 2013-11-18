package me.parapenguin.overcast.scrimmage.rotation;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.MapLoader;

public class Rotation {
	
	static @Getter List<MapLoader> loaded = new ArrayList<MapLoader>();
	@Getter List<RotationSlot> rotation = new ArrayList<RotationSlot>();
    int current;
    @Getter @Setter RotationSlot nextmapinrot;
    @Getter @Setter RotationSlot nextmap;
	@Getter @Setter RotationSlot slot;
	boolean didsetnext;

	public Rotation() {
		List<MapLoader> maps = new ArrayList<MapLoader>();
		List<RotationSlot> slots = new ArrayList<RotationSlot>();

		String rotation = Scrimmage.getInstance().getConfig().getString("rotation");
		if(rotation == null)
			maps.addAll(loaded);
		else {
			String[] split = rotation.split(",");
			for(String map : split)
				maps.add(getMap(loaded, map));
		}
		
		for(MapLoader loader : maps)
			slots.add(new RotationSlot(loader));
		
		this.rotation = slots;
		
		Scrimmage.getInstance().getLogger().info("Rotation: " + getRotationString());
		Scrimmage.getInstance().getConfig().set("rotation", getRotationString());
		Scrimmage.getInstance().saveConfig();
	}
	
	public void start() {
		RotationSlot slot = rotation.get(0);
		this.slot = slot;
		
		slot.load();
		
		Scrimmage.setOpen(true);
	}
	
	public String getRotationString() {
		String rotationString = "";
		for(RotationSlot slot : rotation) {
			rotationString += slot.getLoader().getName();
			if(rotation.get(rotation.size() - 1) != slot)
				rotationString += ",";
		}
		return rotationString;
	}
	
	public void setNext(RotationSlot slot) {
        didsetnext = true;
        this.nextmap = slot;
        current = getLocation(getSlot());
        nextmapinrot = getSlot(current + 1);
	}
	
	public RotationSlot getSlot(int slot) throws IndexOutOfBoundsException {
		return getRotation().get(slot);
	}
	
	public int getLocation(RotationSlot slot) {
		int s = 0;
		for(RotationSlot search : getRotation()) {
			if(search == slot)
				return s;
			s++;
		}
		
		return s;
	}
	
	public RotationSlot getNext() {
		if (this.nextmap != null) {
            RotationSlot next = this.nextmap;
            return next;
        }
        else if (this.nextmapinrot != null) {
            RotationSlot next = this.nextmapinrot;
            return next;
        } else {
            try {
                return getSlot(getLocation(getSlot()) + 1);
            } catch (IndexOutOfBoundsException ioob) {
                return null;
            }
        }
	}

    public void cyclingFinished () {
        if (didsetnext && nextmap != null) {
            nextmap = null;
            didsetnext = false;
        }
        else if (!didsetnext && nextmapinrot != null) {
            nextmapinrot = null;
        }
    }

	public static boolean addMap(MapLoader loader) {
		return loaded.add(loader);
	}
	
	public static MapLoader getMap(String name) {
		return getMap(loaded, name);
	}
	
	public static MapLoader getMap(List<MapLoader> loaded, String name) {
		for(MapLoader loader : loaded)
			if(loader.getName().equalsIgnoreCase(name))
				return loader;
		
		for(MapLoader loader : loaded)
			if(loader.getName().toLowerCase().startsWith(name.toLowerCase()))
				return loader;
		
		return null;
	}
	
}
