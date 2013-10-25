package me.parapenguin.overcast.scrimmage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import lombok.Setter;
import me.parapenguin.overcast.scrimmage.event.PlayerEvents;
import me.parapenguin.overcast.scrimmage.map.MapLoader;
import me.parapenguin.overcast.scrimmage.map.MapTeam;
import me.parapenguin.overcast.scrimmage.map.filter.FilterEvents;
import me.parapenguin.overcast.scrimmage.map.objective.ObjectiveEvents;
import me.parapenguin.overcast.scrimmage.map.region.Region;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.player.commands.JoinCommand;
import me.parapenguin.overcast.scrimmage.rotation.Rotation;
import me.parapenguin.overcast.scrimmage.utils.JarUtils;

public class Scrimmage extends JavaPlugin {
	
	static @Getter Scrimmage instance;
	static @Getter @Setter Rotation rotation;
	@Getter List<File> libs = new ArrayList<File>();
	@Getter List<String> files = new ArrayList<String>();
	
	static @Getter String team;
	static @Getter @Setter boolean open;
	
	public void onEnable() {
		setOpen(false);
		instance = this;
		Region.MAX_BUILD_HEIGHT = 256;
		
		File libFolder = new File("/home/servers/scrim/libs/");
		files.add("dom4j.jar");
		
		for (String stringFile : files) {

			if (libFolder.exists() && libFolder.isDirectory()) {
				libs.add(new File(libFolder.getAbsolutePath() + "/" + stringFile));
			} else if (!libFolder.exists()) {
				libFolder.mkdir();
				libs.add(new File(libFolder.getAbsolutePath() + "/" + stringFile));
			} else {
				getLogger().warning("/" + libFolder.getParentFile().getName() + "/" + libFolder.getName() + " already exists and isn't a directory.");
				Bukkit.getServer().getPluginManager().disablePlugin(this);
			}
		}

		reloadConfig();
		loadJars();
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
	}
	
	public void loadJars() {
		/*
		 * That awkward moment when you forget to upload the jar file... hahah!
		 */
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for (File lib : libs) {
					try {
						addClassPath(JarUtils.getJarUrl(lib));
						getLogger().info("'" + lib.getName() + "' has been loaded!");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				startup();
			}
			
		}.runTask(instance);
	}
	
	public void addClassPath(final URL url) throws IOException {
		final URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		final Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			final Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { url });
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new IOException("Error adding " + url + " to system classloader");
		}
	}
	
	public static int random(int min, int max) {
		return (int) (min + (Math.random() * (max - min)));
	}
	
	public static File getRootFolder() {
		return new File("");
	}
	
	public static File getMapRoot() {
		return new File("/home/servers/scrim/maps/");
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
	
}
