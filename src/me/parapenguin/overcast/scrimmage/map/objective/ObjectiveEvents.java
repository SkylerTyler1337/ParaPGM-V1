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
		*/
		
		/*
		if(map.getMonuments().size() == 0)
			ServerLog.info("No monuments found...");
		*/
		
		int x = event.getBlock().getLocation().getBlockX();
		int y = event.getBlock().getLocation().getBlockY();
		int z = event.getBlock().getLocation().getBlockZ();
		//ServerLog.info("Player (X:" + x + ", Y:" + y + ", Z:" + z + ")");
		
		MonumentObjective monument = map.getMonument(event.getBlock().getLocation());
		//ServerLog.info("Monument == null (" + (monument == null) + ")");
		if(monument == null) return;
		
		if(monument.getTeam() != client.getTeam()) {
			//ServerLog.info("Team == Client Team = " + (monument.getTeam() == client.getTeam()));
			event.setCancelled(true);
			return;
		}
		
		monument.addBreak(event.getBlock().getLocation(), client);
	}

}
