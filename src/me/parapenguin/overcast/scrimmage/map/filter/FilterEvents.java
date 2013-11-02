package me.parapenguin.overcast.scrimmage.map.filter;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.player.Client;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class FilterEvents implements Listener {
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Client client = Client.getClient(player);
		
		if(client.isObserver() || !Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning()) {
			event.setCancelled(true);
			return;
		}
		
		if(!client.isObserver()) {
			Map map = Scrimmage.getRotation().getSlot().getMap();
			
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Client client = Client.getClient(player);
		
		if(client.isObserver() || !Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning()) {
			event.setCancelled(true);
			return;
		}
		
		if(!client.isObserver()) {
			Map map = Scrimmage.getRotation().getSlot().getMap();
			
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Client client = Client.getClient(player);

		if(client.isObserver() || !Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning()) {
			event.setCancelled(true);
			if(event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST && event.getAction() == Action.RIGHT_CLICK_BLOCK)
				player.openInventory(((Chest) event.getClickedBlock().getState()).getBlockInventory());
			return;
		}
		
		if(!client.isObserver()) {
			Map map = Scrimmage.getRotation().getSlot().getMap();
			
		}
	}
	
	@EventHandler
	public void onPlayerDropItems(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Client client = Client.getClient(player);
		
		if(client.isObserver() || !Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning()) {
			event.setCancelled(true);
			return;
		}
		
		if(!client.isObserver()) {
			Map map = Scrimmage.getRotation().getSlot().getMap();
			
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked() instanceof Player == false) {
			event.setCancelled(true);
			return;
		}
		
		Player player = (Player) event.getWhoClicked();
		Client client = Client.getClient(player);
		
		if((client.isObserver() || !Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning()) && event.getInventory().getType() != InventoryType.PLAYER) {
			event.setCancelled(true);
			return;
		}
		
		if(!client.isObserver()) {
			Map map = Scrimmage.getRotation().getSlot().getMap();
			
		}
	}
	
}
