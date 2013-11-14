package me.parapenguin.overcast.scrimmage.map.objective;

import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapTeam;
import me.parapenguin.overcast.scrimmage.utils.RegionUtil;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class CoreObjective extends TeamObjective {
	
	@Getter List<Location> blocks;
	@Getter int leak;
	@Getter CoreStage stage;
	
	public CoreObjective(Map map, MapTeam owner, String name, List<Location> blocks, int leak, CoreStage stage) {
		super(map, owner, name);
		this.blocks = blocks;
		this.leak = leak;
		this.stage = stage;
	}
	
	public boolean isLeak(Location location) {
		return RegionUtil.closest(location, blocks) >= leak;
	}
	
	public boolean isLocation(Location location) {
		return getBlock(location) != null;
	}
	
	public Location getBlock(Location location) {
		for(Location loc : blocks) {
			boolean x = loc.getBlockX() == location.getBlockX();
			boolean y = loc.getBlockY() == location.getBlockY();
			boolean z = loc.getBlockZ() == location.getBlockZ();
			if(x && y && z) return loc;
		}
		
		return null;
	}
	
	public void setStage(CoreStage newStage) {
		if(newStage == CoreStage.OTHER)
			return;
		
		this.stage = newStage;
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(Location location : blocks) {
					location.getBlock().setType(stage.getMaterial());
				}
			}
			
		};
	}
	
}
