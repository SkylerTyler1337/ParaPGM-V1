package me.parapenguin.overcast.scrimmage.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.ServerLog;

public class ClassPathUtil {
	
	private static @Getter File libFolder;
	private static @Getter boolean loaded;
	
	public static @Getter List<File> libs;
	
	public static void load(File libFolder) {
		ClassPathUtil.libFolder = libFolder;
		ClassPathUtil.loaded = true;
	}
	
	public static boolean addJars(List<String> files) {
		libs = new ArrayList<File>();
		
		for (String stringFile : files) {
			if (libFolder.exists() && libFolder.isDirectory()) {
				libs.add(new File(libFolder.getAbsolutePath() + "/" + stringFile));
			} else if (!libFolder.exists()) {
				libFolder.mkdir();
				libs.add(new File(libFolder.getAbsolutePath() + "/" + stringFile));
			} else {
				ServerLog.warning("/" + libFolder.getParentFile().getName() + "/" + libFolder.getName() + " already exists and isn't a directory.");
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean loadJars() {
		int loaded = 0;
		for (File lib : libs) {
			try {
				if(!addClassPath(JarUtils.getJarUrl(lib))) {
					ServerLog.warning("ClassPathUtil has not been loaded!");
					continue;
				}
				
				ServerLog.info("'" + lib.getName() + "' has been loaded!");
				loaded++;
			} catch (IOException e) {
				ServerLog.warning("IOException fired when loading " + lib.getName() + " to the class path.");
				continue;
			}
		}
		
		if(loaded == libs.size())
			return true;
		
		return false;
	}
	
	private static boolean addClassPath(final URL url) throws IOException {
		if(!loaded) return false;
		
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
		
		return true;
	}
	
}
