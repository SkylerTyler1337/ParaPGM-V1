package me.parapenguin.overcast.scrimmage.match;

import org.bukkit.ChatColor;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapLoader;
import me.parapenguin.overcast.scrimmage.rotation.RotationSlot;
import me.parapenguin.overcast.scrimmage.utils.SchedulerUtil;

public class Match {
	
	@Getter MapLoader loader;
	@Getter RotationSlot slot;
	
	@Getter Map map;
	
	@Getter SchedulerUtil schedule;
	
	@Getter int starting;
	
	@Getter int timing;
	@Getter int length;
	
	@Getter int cycling;
	
	public Match(MapLoader loader, RotationSlot slot, int length) {
		this.loader = loader;
		this.slot = slot;
		this.length = length;
		
		this.map = loader.getMap(slot);
	}
	
	private boolean starting() {
		String p = "s";
		if(starting == 1) p = "";
		if(starting % 5 == 0 || starting <= 5)
			Scrimmage.broadcast(ChatColor.GREEN + "Match starting in " + ChatColor.DARK_RED + starting + ChatColor.GREEN + " second" + p + "!");
		
		if(starting == 0)
			return true;
		
		starting--;
		return false;
	}
	
	private boolean timing() {
		if(timing >= length)
			return true;
		
		timing++;
		return false;
	}
	
	private boolean cycling(MapLoader next) {
		String p = "s";
		if(cycling == 1) p = "";
		if(cycling % 5 == 0 || cycling <= 5)
			Scrimmage.broadcast("Cycling to " + ChatColor.AQUA + next.getName() + ChatColor.DARK_AQUA + " in " + ChatColor.DARK_RED + cycling + ChatColor.DARK_AQUA + " second" + p + "!");
		
		if(cycling == 0)
			return true;
		
		cycling--;
		return false;
	}
	
}
