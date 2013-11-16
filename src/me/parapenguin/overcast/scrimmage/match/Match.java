package me.parapenguin.overcast.scrimmage.match;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import lombok.Getter;
import lombok.Setter;
import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapTeam;
import me.parapenguin.overcast.scrimmage.map.extras.Contributor;
import me.parapenguin.overcast.scrimmage.map.extras.SidebarType;
import me.parapenguin.overcast.scrimmage.map.objective.CoreObjective;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.rotation.RotationSlot;
import me.parapenguin.overcast.scrimmage.utils.ConversionUtil;
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
		if(time == -2)
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
		if((timing >= length && length != -1) || end(false) || (getMap().getTimeLimit() > 0 && getMap().getTimeLimit() <= timing)) {
			end(true);
			setCurrentlyRunning(false);
			return true;
		}
		
		if(timing % (5*60) == 0) {
			String playing = ChatColor.DARK_PURPLE + "Currently playing " + ChatColor.GOLD + getMap().getName();
			String by = ChatColor.DARK_PURPLE + " by ";
			
			List<String> authors = new ArrayList<String>();
			for(Contributor author : getMap().getAuthors())
				authors.add(ChatColor.RED + author.getName());
			String creators = ConversionUtil.commaList(authors, ChatColor.DARK_PURPLE);
			String broadcast = playing + by + creators;
			Scrimmage.broadcast(broadcast);
		}
		
		for(CoreObjective core : getMap().getCores()) {
			if(core.getStage().getNext() != null && core.getStage().getNext().getTime() == timing)
				core.setStage(core.getStage().getNext());
		}
		
		if(getMap().getSidebar() == SidebarType.SCORE) {
			boolean timer = false;
			if(timing % 60 == 0) timer = true;
			else if(getMap().getTimeLimit() > 0) {
				if(getMap().getTimeLimit() - timing <= 60) {
					if(getMap().getTimeLimit() - timing % 15 == 0) timer = true;
					else if(getMap().getTimeLimit() - timing < 5) timer = true;
				}
			}
			
			if(timer) {
				String score = ChatColor.AQUA + "Score: ";
				for(MapTeam team : getMap().getTeams())
					score += team.getColor() + "" + team.getScore() + " ";
				
				if(getMap().getTimeLimit() > 0)
					score += ChatColor.RED + ConversionUtil.formatTime(getMap().getTimeLimit() - getTiming());
				Scrimmage.broadcast(score);
			}
		}
		
		timing++;
		
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
	
	public boolean end(List<MapTeam> winners) {
		end();
		if(winners.size() > 0) {
			List<String> teams = new ArrayList<String>();
			for(MapTeam team : winners)
				teams.add(team.getColor() + team.getDisplayName());
			
			String text = ConversionUtil.commaList(teams, ChatColor.GRAY) + " wins!";
			Scrimmage.broadcast(text);
		}
		
		return true;
	}
	
	public boolean end(MapTeam winner) {
		List<MapTeam> winners = new ArrayList<MapTeam>();
		if(winner != null) winners.add(winner);
		return end(winners);
	}
	
	private void end() {
		Scrimmage.broadcast(ChatColor.GOLD + "" + ChatColor.BOLD + "Game Over!");
		timingTask.getTask().cancel();
		cyclingTask.repeatAsync(20, 20);
		
		for(MapTeam team : getMap().getTeams())
			for(Client client : team.getPlayers())
				client.setTeam(getMap().getObservers(), true, false, false);
		
		setCurrentlyRunning(false);
		setCurrentlyCycling(true);
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
		
		if(cycling <= 5 && !loaded && next != null) {
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
