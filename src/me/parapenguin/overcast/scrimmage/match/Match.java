package me.parapenguin.overcast.scrimmage.match;

import java.util.List;

import org.bukkit.ChatColor;

import lombok.Getter;
import lombok.Setter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapTeam;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.rotation.RotationSlot;
import me.parapenguin.overcast.scrimmage.utils.SchedulerUtil;

public class Match {

	@Getter Map map;
	@Getter RotationSlot slot;
	
	@Getter SchedulerUtil schedule;
	
	@Getter SchedulerUtil startingTask;
	@Getter int starting = 30;
	@Getter @Setter boolean currentlyStarting = false;

	@Getter SchedulerUtil timingTask;
	@Getter int timing = 0;
	@Getter int length;
	@Getter @Setter boolean currentlyRunning = false;

	@Getter SchedulerUtil cyclingTask;
	@Getter int cycling = 30;
	@Getter @Setter boolean currentlyCycling = false;
	
	public Match(RotationSlot slot, int length) {
		this.slot = slot;
		this.length = length;
		this.map = slot.getMap();
		
		this.startingTask = new SchedulerUtil() {
			
			@Override
			public void runnable() {
				starting();
			}
			
		};
		
		this.timingTask = new SchedulerUtil() {
			
			@Override
			public void runnable() {
				timing();
			}
			
		};
		
		this.cyclingTask = new SchedulerUtil() {
			
			@Override
			public void runnable() {
				cycling(Scrimmage.getRotation().getNext());
			}
			
		};
		
		map.update(true);
		setCurrentlyStarting(false);
		setCurrentlyRunning(false);
		setCurrentlyCycling(false);
	}
	
	public void start() {
		this.startingTask.repeat(20, 0);
	}
	
	private boolean starting() {
		setCurrentlyStarting(true);
		if(starting == 0) {
			startingTask.getTask().cancel();
			setCurrentlyStarting(false);
			
			timingTask.repeat(20, 0);
			
			for(MapTeam team : getMap().getTeams())
				for(Client client : team.getPlayers())
					client.setTeam(team, true);
			
			return true;
		}
		
		String p = "s";
		if(starting == 1) p = "";
		if(starting % 5 == 0 || starting <= 5)
			Scrimmage.broadcast(ChatColor.GREEN + "Match starting in " + ChatColor.DARK_RED + starting + ChatColor.GREEN + " second" + p + "!");
		
		starting--;
		return false;
	}
	
	private boolean timing() {
		setCurrentlyRunning(true);
		if(timing >= length) {
			end();
			setCurrentlyRunning(false);
			return true;
		}
		
		timing++;
		
		if(timing % (5*60) == 0) {
			String playing = ChatColor.DARK_PURPLE + "Currently playing " + ChatColor.GOLD + getMap().getName();
			String by = ChatColor.DARK_PURPLE + " by ";
			
			String creators = "";
			if(getMap().getAuthors().size() == 1)
				creators += ChatColor.GOLD + getMap().getAuthors().get(0).getName();
			else if(getMap().getAuthors().size() >= 2) {
				/*
				 * index 0 should prefix ""
				 * index 1 to (max index - 1) should prefix ", "
				 * index max index should prefix " and "
				 */
				
				int index = 0;
				while(index < getMap().getAuthors().size()) {
					if(index > 0) {
						creators += ChatColor.DARK_PURPLE;
						if(index == (getMap().getAuthors().size() -1))
							creators += " and ";
						else creators += ", ";
					}
					
					creators += ChatColor.RED + getMap().getAuthors().get(index).getName();
					index++;
				}
			}
			
			String broadcast = playing + by + creators + ChatColor.DARK_PURPLE + ".";
			Scrimmage.broadcast(broadcast);
		}
		return false;
	}
	
	public void end() {
		List<MapTeam> teams = getMap().getWinners();
		MapTeam winner = null;
		
		if(teams.size() == 1)
			winner = teams.get(0);
		
		end(winner);
	}
	
	public boolean end(MapTeam winner) {
		Scrimmage.broadcast(ChatColor.GOLD + "" + ChatColor.BOLD + "Game Over!");
		if(winner != null)
			Scrimmage.broadcast(winner.getColor() + winner.getDisplayName() + " wins!");
		
		timingTask.getTask().cancel();
		cyclingTask.repeatAsync(20, 20);
		return true;
	}
	
	private boolean cycling(RotationSlot next) {
		setCurrentlyCycling(true);
		if(cycling == 0) {
			cyclingTask.getTask().cancel();
			setCurrentlyCycling(false);
		}
		
		String p = "s";
		if(cycling == 1) p = "";
		if(cycling % 5 == 0 || cycling <= 5) {
			if(next != null)
				Scrimmage.broadcast(ChatColor.DARK_AQUA + "Cycling to " + ChatColor.AQUA + next.getLoader().getName() + ChatColor.DARK_AQUA
						+ " in " + ChatColor.DARK_RED + cycling + ChatColor.DARK_AQUA + " second" + p + "!");
			else Scrimmage.broadcast(ChatColor.AQUA + "Server restarting in " + ChatColor.DARK_RED + cycling + ChatColor.AQUA + " second" + p + "!");
		}
		
		cycling--;
		return false;
	}
	
}
