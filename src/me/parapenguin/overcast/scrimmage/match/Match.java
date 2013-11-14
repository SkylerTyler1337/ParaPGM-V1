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
	@Getter @Setter int starting = 30;
	@Getter @Setter boolean currentlyStarting = false;

	@Getter SchedulerUtil timingTask;
	@Getter int timing = 0;
	@Getter @Setter int length;
	@Getter @Setter boolean currentlyRunning = false;

	@Getter SchedulerUtil cyclingTask;
	@Getter @Setter int cycling = 30;
	@Getter @Setter boolean loaded = false;
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
		setCurrentlyStarting(true);
		setCurrentlyRunning(false);
		setCurrentlyCycling(false);
	}
	
	public void start() {
		start(30);
	}
	
	public void start(int time) {
		if(time == 0)
			time = 30;
		
		try {
			stop();
		} catch(Exception e) {
			// meh
		}
		
		this.timing = 0;
		this.cycling = 30;
		this.starting = time;
		this.startingTask.repeat(20, 0);
	}
	
	public void cycle() {
		cycle(30);
	}
	
	public void cycle(int time) {
		if(time == 0)
			time = 30;
		
		try {
			stop();
		} catch(Exception e) {
			// meh
		}
		
		this.timing = 0;
		this.cycling = time;
		this.starting = 30;
		this.cyclingTask.repeat(20, 0);
	}
	
	public void stop() throws NullPointerException {
		if(this.startingTask.getTask() != null) this.startingTask.getTask().cancel();
		if(this.cyclingTask.getTask() != null) this.cyclingTask.getTask().cancel();
		if(this.timingTask.getTask() != null) this.timingTask.getTask().cancel();
		
		setCurrentlyStarting(true);
		setCurrentlyRunning(false);
		setCurrentlyCycling(false);
	}
	
	private boolean starting() {
		setCurrentlyStarting(true);
		if(starting == 0) {
			startingTask.getTask().cancel();
			setCurrentlyStarting(false);
			
			timingTask.repeat(20, 0);
			
			for(MapTeam team : getMap().getTeams())
				for(Client client : team.getPlayers())
					client.setTeam(team, true, true, true);
			
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
		if((timing >= length && length != -1) || end(false)) {
			end(true);
			setCurrentlyRunning(false);
			return true;
		}
		
		timing++;
		
		if(timing % (5*60) == 0) {
			String playing = ChatColor.DARK_PURPLE + "Currently playing " + ChatColor.GOLD + getMap().getName();
			String by = ChatColor.DARK_PURPLE + " by ";
			
			String creators = "";
			if(getMap().getAuthors().size() == 1)
				creators += ChatColor.RED + getMap().getAuthors().get(0).getName();
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
	
	public boolean end(boolean force) {
		List<MapTeam> teams = getMap().getWinners();
		MapTeam winner = null;
		
		if(teams.size() == 1)
			winner = teams.get(0);
		
		if(!force) return false;
		end(winner);
		return true;
	}
	
	public boolean end(MapTeam winner) {
		Scrimmage.broadcast(ChatColor.GOLD + "" + ChatColor.BOLD + "Game Over!");
		if(winner != null)
			Scrimmage.broadcast(winner.getColor() + winner.getDisplayName() + " wins!");
		
		timingTask.getTask().cancel();
		cyclingTask.repeatAsync(20, 20);
		
		for(MapTeam team : getMap().getTeams())
			for(Client client : team.getPlayers())
				client.setTeam(getMap().getObservers(), true, false, false);
		
		setCurrentlyRunning(false);
		setCurrentlyCycling(true);
		return true;
	}
	
	private boolean cycling(RotationSlot next) {
		setCurrentlyCycling(true);
		if(cycling == 0) {
			cyclingTask.getTask().cancel();
			setCurrentlyCycling(false);
			
			if(next == null) {
				Scrimmage.getInstance().getServer().shutdown();
				return true;
			}

			Scrimmage.getRotation().setSlot(next);
			for(Client client : Client.getClients())
				client.setTeam(next.getMap().getObservers(), true, true, true);
			next.getMatch().start();
			
			return true;
		}
		
		if(cycling == 5 && !loaded && next != null) {
			setLoaded(true);
			next.load();
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
