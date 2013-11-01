package me.parapenguin.overcast.scrimmage.map.objective;

import org.bukkit.ChatColor;

import lombok.Getter;
import lombok.Setter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapTeam;

public class TeamObjective {
	
	@Getter Map map;
	
	@Getter String name;
	@Getter MapTeam team;
	@Getter boolean complete;
	@Getter int used;
	
	@Getter @Setter int touched;
	
	public TeamObjective(Map map, MapTeam owner, String name) {
		this.name = name;
		this.team = owner;
		this.complete = false;
		
		this.used = 0;
		try {
			for(MapTeam team : map.getTeams())
				for(TeamObjective objective : team.getObjectives())
					if(objective.getName().equalsIgnoreCase(name))
						this.used++;
		} catch(Exception e) {
			// ignore, it literally just means that it should be 0.
		}
	}
	
	/*
	 * The integer of 'used' is there so I can add an extra space (or two) to the end of objectives for the scoreboard
	 */
	
	public ChatColor getColor() {
		if(complete) return ChatColor.GREEN;
		return ChatColor.RED;
	}
	
	public String getSpaces() {
		return Map.getSpaces(used);
	}
	
	public ObjectiveType getType() {
		if(this instanceof WoolObjective)
			return ObjectiveType.CTW;
		else if(this instanceof WoolObjective)
			return ObjectiveType.DTM;
		return ObjectiveType.NONE;
	}
	
	public void addTouch() {
		addTouch(1);
	}
	
	public void addTouch(int amount) {
		setTouched(getTouched() + amount);
	}
	
	public void setComplete(boolean complete) {
		this.complete = complete;
		getTeam().getMap().reloadSidebar(true);
		if(getTeam().getCompleted() == getTeam().getObjectives().size())
			Scrimmage.getRotation().getSlot().getMatch().end(getTeam());
	}
	
}
