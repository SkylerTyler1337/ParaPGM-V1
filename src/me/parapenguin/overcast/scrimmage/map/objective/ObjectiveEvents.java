package me.parapenguin.overcast.scrimmage.map.objective;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.utils.FireworkUtil;

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
		
		wool.setComplete(true);
		client.getTeam().getMap().reloadSidebar(true);
		
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreakForWool(BlockBreakEvent event) {
		if(Scrimmage.getMap().getWool(event.getBlock().getLocation()) != null) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreakForMonument(BlockBreakEvent event) {
		Client client = Client.getClient(event.getPlayer());
		Map map = client.getTeam().getMap();
		
		MonumentObjective monument = map.getMonument(event.getBlock().getLocation());
		if(monument == null) return;
		
		if(monument.getTeam() != client.getTeam()) {
			event.setCancelled(true);
			return;
		}
		
		monument.addBreak(event.getBlock().getLocation(), client);
	}

}
