package me.parapenguin.overcast.scrimmage.event;

import java.util.List;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.filter.Filter;

import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.block.CraftBeacon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BeaconInventory;

public class BeaconEvents implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		
		List<Filter> filters = map.getFilters(event.getClickedBlock().getLocation());
	}
	
	@EventHandler
	public void onPotionEffectChange()
	
}
