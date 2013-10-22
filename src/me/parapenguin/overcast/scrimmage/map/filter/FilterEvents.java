package me.parapenguin.overcast.scrimmage.map.filter;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.player.Client;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class FilterEvents implements Listener {
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Client client = Client.getClient(player);
		
		if(client.isObserver() && !player.hasPermission("scrimmage.observer.place")) {
			event.setCancelled(true);
			return;
		}
		
		if(!client.isObserver()) {
			Map map = Scrimmage.getRotation().getSlot().getMap();
			
		}
	}
	
}
