package me.parapenguin.overcast.scrimmage.map.objective;

import java.util.List;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.extras.SidebarType;
import me.parapenguin.overcast.scrimmage.map.filter.events.BlockChangeEvent;
import me.parapenguin.overcast.scrimmage.match.events.PlayerDiedEvent;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.utils.ConversionUtil;
import me.parapenguin.overcast.scrimmage.utils.FireworkUtil;

import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class ObjectiveEvents implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDiedEvent(PlayerDiedEvent event) {
		Client killer = Client.getClient(event.getKiller());
		Client died = Client.getClient(event.getKilled());
		
		if(killer == null) died.getTeam().addScore(-1);
		else {
			killer.getTeam().addScore(1);
			if(event.getMap().getSidebar() == SidebarType.SCORE && killer.getTeam().getScore() >= event.getMap().getScoreLimit() && event.getMap().getScoreLimit() > 0)
				Scrimmage.getRotation().getSlot().getMatch().end(killer.getTeam());
		}
		
		event.getMap().reloadSidebar(false, SidebarType.SCORE);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onCoreBlockChange(BlockChangeEvent event) {
		if(event.getNewState().getWorld() != event.getMap().getWorld())
			return;
		
		if(event.getClient() != null) {
			Client client = event.getClient();
			List<CoreObjective> cores = event.getMap().getCores();
			
			if(event.getCause() instanceof BlockBreakEvent) {
				for(CoreObjective core : cores)
					if(core.isLocation(event.getNewState().getLocation()) && core.getTeam() == client.getTeam()) {
						event.setCancelled(true);
						client.getPlayer().sendMessage(ChatColor.RED + "You can't break your own core!");
						return;
					}
			}
			
			if(event.getNewState().getType() == Material.LAVA) {
				event.setCancelled(true);
				return;
			}
		} else {
			if(event.getNewState().getType() == Material.LAVA && event.getMap().getCoreLeak(event.getNewState().getLocation()) != null) {
				CoreObjective core = event.getMap().getCoreLeak(event.getNewState().getLocation());
				core.setComplete(true);
				
				event.getMap().reloadSidebar(true, SidebarType.OBJECTIVES);
				
				String who = core.getTeam().getColor() + core.getTeam().getDisplayName() + "'s";
				String leaked = ChatColor.DARK_AQUA + " " + core.getName();
				String has = ChatColor.RED + " has leaked!";
				String message = who + leaked + has;
				Scrimmage.broadcast(message);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlaceForWool(BlockPlaceEvent event) {
		Client client = Client.getClient(event.getPlayer());
		/*
		 * ServerLog.info("Placed Block: "
				+ "Z(" + event.getBlockPlaced().getLocation().getBlockX() + "), "
				+ "Y(" + event.getBlockPlaced().getLocation().getBlockY() + "), "
				+ "Z(" + event.getBlockPlaced().getLocation().getBlockZ() + ")");
		 */
		
		for(WoolObjective wool : client.getTeam().getMap().getWools())
			if(wool.isLocation(event.getBlockPlaced().getLocation()) && wool.getTeam() != client.getTeam()) {
				event.setCancelled(true);
				return;
			}
		
		if(event.getBlockPlaced().getType() != Material.WOOL) {
			if(Scrimmage.getMap().getWool(event.getBlock().getLocation()) != null) {
				event.setCancelled(true);
				return;
			}
			return;
		}

		WoolObjective wool = client.getTeam().getWool(event.getBlockPlaced());
		if(wool == null)
			return;
		
		if(wool.isLocation(event.getBlockPlaced().getLocation()) && wool.getTeam() != client.getTeam()) {
			event.setCancelled(true);
			return;
		}

		if(!wool.isLocation(event.getBlockPlaced().getLocation()))
			return;

		Builder builder = FireworkEffect.builder();
		builder.withColor(wool.getWool().getColor());
		builder.with(Type.BALL);
		builder.withTrail();

		FireworkEffect effect = builder.build();
		
		try {
			new FireworkUtil().playFirework(event.getBlockPlaced().getWorld(), event.getBlockPlaced().getLocation(), effect);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		String who = client.getTeam().getColor() + client.getPlayer().getName();
		String placed = ChatColor.WHITE + " placed the " + wool.getName().toUpperCase();
		String team = ChatColor.WHITE + " for " + client.getTeam().getColor() + client.getTeam().getDisplayName();
		String message = who + placed + team;
		Scrimmage.broadcast(message);
		
		wool.setComplete(true);
		client.getTeam().getMap().reloadSidebar(true, SidebarType.OBJECTIVES);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreakForWool(BlockBreakEvent event) {
		if(Scrimmage.getMap().getWool(event.getBlock().getLocation()) != null) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreakForMonument(BlockChangeEvent event) {
		Client client = event.getClient();
		Map map = event.getMap();
		
		if(event.getNewState().getType() != Material.AIR)
			return;
		
		/*
		for(MonumentObjective monument : map.getMonuments()) {
			try {
				int x = monument.getBlocks().get(0).getLocation().getBlockX();
				int y = monument.getBlocks().get(0).getLocation().getBlockY();
				int z = monument.getBlocks().get(0).getLocation().getBlockZ();
				ServerLog.info("Monument (" + monument.getBlocks().size() + " blocks starting at X:" + x + ", Y:" + y + ", Z:" + z + ")");
			} catch(IndexOutOfBoundsException e) {
				ServerLog.info("No Blocks found for Mounument ('" + monument.getName() + "')");
			}
		}
		
		if(map.getMonuments().size() == 0)
			ServerLog.info("No monuments found...");
		
		int x = event.getBlock().getLocation().getBlockX();
		int y = event.getBlock().getLocation().getBlockY();
		int z = event.getBlock().getLocation().getBlockZ();
		ServerLog.info("Player (X:" + x + ", Y:" + y + ", Z:" + z + ")");
		*/
		
		MonumentObjective monument = map.getMonument(event.getNewState().getLocation());
		//ServerLog.info("Monument == null (" + (monument == null) + ")");
		if(monument == null) return;
		
		try {
			if(monument.getTeam() != client.getTeam()) {
				//ServerLog.info("Team == Client Team = " + (monument.getTeam() == client.getTeam()));
				event.setCancelled(true);
				return;
			}
		} catch(NullPointerException e) {
			// probably another cause for the TNT being destroyed, and the client is null...
		}
		
		monument.addBreak(event.getNewState().getLocation(), client);
	}

}
