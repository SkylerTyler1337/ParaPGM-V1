package me.parapenguin.overcast.scrimmage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import me.parapenguin.overcast.scrimmage.event.PlayerEvents;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapLoader;
import me.parapenguin.overcast.scrimmage.map.MapTeam;
import me.parapenguin.overcast.scrimmage.map.filter.FilterEvents;
import me.parapenguin.overcast.scrimmage.map.objective.ObjectiveEvents;
import me.parapenguin.overcast.scrimmage.map.region.Region;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.player.commands.*;
import me.parapenguin.overcast.scrimmage.rotation.Rotation;
import me.parapenguin.overcast.scrimmage.tracker.GravityKillTracker;
import me.parapenguin.overcast.scrimmage.tracker.PlayerBlockChecker;
import me.parapenguin.overcast.scrimmage.tracker.TickTimer;
import me.parapenguin.overcast.scrimmage.utils.ClassPathUtil;
import me.parapenguin.overcast.scrimmage.utils.FileUtil;
import me.parapenguin.overcast.scrimmage.utils.ZipUtil;

public class Scrimmage extends JavaPlugin {
	
	static @Getter Scrimmage instance;
	static @Getter @Setter Rotation rotation;

	private TickTimer tickTimer;
	@Getter public GravityKillTracker gkt;
	
	static @Getter String team;
	static @Getter @Setter boolean open;
	
	private @Getter @Setter File rootDirectory;
	private @Getter @Setter String mapDirectory;
	
	@Getter public static double MINIMUM_MOVEMENT = 0.125;
	
	public void onEnable() {
		reloadConfig();
		setOpen(false);
		instance = this;
		Region.MAX_BUILD_HEIGHT = 256;
		
		this.rootDirectory = getServer().getWorldContainer();
		if(getConfig().getString("maps") != null)
			this.mapDirectory = getConfig().getString("maps");
		
		List<String> files = new ArrayList<String>();
		File libFolder = new File(getRootDirectory(), "libs");
		if(!libFolder.exists()) libFolder = getDataFolder().getParentFile().getParentFile().getParentFile().getParentFile();
		files.add("dom4j.jar");
		
		ClassPathUtil.load(libFolder);
		if(!ClassPathUtil.addJars(files)) {
			ServerLog.severe("Failed to load all of the required libraries - Server shutting down!");
			getServer().shutdown();
			return;
		}

		if(!ClassPathUtil.loadJars()) {
			ServerLog.severe("Failed to add all of the required libraries to the class path - Server shutting down!");
			getServer().shutdown();
			return;
		}
		
		startup();
		
		/*
		 * Auto un-zipper, this should be helpful instead of killing my internet :)
		 */
		
		File[] maps = getMapRoot().listFiles();
		File zips = new File(getMapRoot().getAbsolutePath() + "/zips/");
		
		for(File file : maps) {
			if(!file.getName().toLowerCase().contains(".zip"))
				continue;
			
			if(!zips.isDirectory())
				FileUtil.delete(zips);
			if(!zips.exists())
				zips.mkdirs();
			
			ZipUtil.unZip(file, getMapRoot());
			try {
				FileUtil.move(file, new File(zips.getAbsolutePath() + "/" + file.getName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startup() {
		team = getConfig().getString("team");
		if(team == null)
			team = "public";
		Scrimmage.getInstance().getConfig().set("team", team);
		Scrimmage.getInstance().saveConfig();
		
		// Load the maps from the local map repository (no github/download connections this time Harry...)
		File[] files = getMapRoot().listFiles();
		
		for(File file : files)
			if(file.isDirectory())
				for(File contains : file.listFiles())
					if(!contains.isDirectory() && contains.getName().endsWith(".xml") && MapLoader.isLoadable(contains)) {
						MapLoader loader = MapLoader.getLoader(contains);
						Rotation.addMap(loader);
					}
		
		setRotation(new Rotation());
		registerListener(new PlayerEvents());
		registerListener(new FilterEvents());
		registerListener(new ObjectiveEvents());
		getRotation().start();
		
		registerCommand("join", new JoinCommand());
		registerCommand("setteam", new SetTeamCommand());
		registerCommand("setnext", new SetNextCommand());
		registerCommand("global", new GlobalCommand());
		registerCommand("start", new StartCommand());
		registerCommand("cycle", new CycleCommand());
		registerCommand("end", new StopCommand());
		enableTracker();
	}

	public void enableTracker() {
		tickTimer = new TickTimer(this);
		tickTimer.start();

		gkt = new GravityKillTracker(tickTimer, new PlayerBlockChecker());
		getServer().getPluginManager().registerEvents(gkt, this);
		getServer().getPluginManager().registerEvents(tickTimer, this);
	}
	
	public static int random(int min, int max) {
		return (int) (min + (Math.random() * (max - min)));
	}
	
	public static File getRootFolder() {
		return instance.getRootDirectory();
	}
	
	public static File getMapRoot() {
		if(getInstance().getMapDirectory() != null)
			return new File(getInstance().getMapDirectory());
		else return new File(getRootFolder().getAbsolutePath() + "/maps/");
	}
	
	public static void broadcast(String message) {
		getInstance().getServer().broadcastMessage(message);
	}
	
	public static void broadcast(String message, MapTeam team) {
		if(team == null)
			getInstance().getServer().broadcastMessage(message);
		else
			for(Client client : team.getPlayers())
				client.getPlayer().sendMessage(message);
	}
	
	public static void registerCommand(String label, CommandExecutor cmdEx) {
		getInstance().getCommand(label).setExecutor(cmdEx);
	}
	
	public static void registerListener(Listener listener) {
		getInstance().getServer().getPluginManager().registerEvents(listener, getInstance());
	}
	
	public static void callEvent(Event event) {
		getInstance().getServer().getPluginManager().callEvent(event);
	}
	
	public static boolean isPublic() {
		return getTeam().equalsIgnoreCase("public");
	}
	
	public static int getID() {
		return getInstance().getServer().getPort() - 25560;
	}
	
	public static Map getMap() {
		return Scrimmage.getRotation().getSlot().getMap();
	}
	
}
