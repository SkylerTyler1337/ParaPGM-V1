package me.parapenguin.overcast.scrimmage.map.filter;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.filter.events.BlockChangeEvent;
import me.parapenguin.overcast.scrimmage.player.Client;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;

public class FilterEvents implements Listener {
	
	@EventHandler(priority = EventPriority.LOW)
	
	public void onBlockChange(BlockChangeEvent event) {
		Map map = event.getMap();
		
		if(event.getClient() != null) {
			Client client = event.getClient();
			
			if(event.getNewState().getLocation().getBlockY() > map.getMaxbuildheight()) {
				event.setCancelled(true);
				client.getPlayer().sendMessage(ChatColor.RED + "You have reached the maximum build height");
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Client client = Client.getClient(player);
		
		if(client.isObserver() || !Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning()) {
			event.setCancelled(true);
			return;
		}
		
		if(!client.isObserver()) {
			Map map = Scrimmage.getRotation().getSlot().getMap();
			BlockChangeEvent change = new BlockChangeEvent(event, map, client, event.getBlockReplacedState(), event.getBlockPlaced().getState());
			Scrimmage.callEvent(change);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Client client = Client.getClient(player);
		
		if(client.isObserver() || !Scrimmage.getRotation().getSlot().getMatch().isCurrentlyRunning()) {
			event.setCancelled(true);
			return;
		}
		
		if(!client.isObserver()) {
			Map map = Scrimmage.getRotation().getSlot().getMap();
			
			BlockState newState = event.getBlock().getState();
			newState.setData(new MaterialData(Material.AIR, (byte) 0));
			
			BlockChangeEvent change = new BlockChangeEvent(event, map, client, event.getBlock().getState(), newState);
			Scrimmage.callEvent(change);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Client client = Client.getClient(player);
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
		Map map = Scrimmage.getRotation().getSlot().getMap();
		
		BlockState newState = block.getState();
		Material update = Material.LAVA;
		if(event.getBucket() == Material.WATER_BUCKET) update = Material.WATER;
		newState.setData(new MaterialData(update, (byte) 0));
		
		BlockState oldState = block.getState();
		BlockChangeEvent change = new BlockChangeEvent(event, map, client, oldState, newState);
		Scrimmage.callEvent(change);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockForm(BlockFormEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();
		BlockChangeEvent change = new BlockChangeEvent(event, map, null, event.getBlock().getState(), event.getNewState());
		Scrimmage.callEvent(change);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockSpread(BlockSpreadEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();
		BlockChangeEvent change = new BlockChangeEvent(event, map, null, event.getBlock().getState(), event.getNewState());
		Scrimmage.callEvent(change);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockFromTo(BlockFromToEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();
		if (event.getToBlock().getType() != event.getBlock().getType()) {
			BlockChangeEvent change = new BlockChangeEvent(event, map, null, event.getBlock().getState(), event.getToBlock().getState());
			Scrimmage.callEvent(change);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityExplodeNormal(EntityExplodeEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();
		for(Block block : event.blockList()) {
			BlockState newState = block.getState();
			newState.setData(new MaterialData(Material.AIR, (byte) 0));
			BlockChangeEvent change = new BlockChangeEvent(event, map, null, block.getState(), newState);
			Scrimmage.callEvent(change);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBurn(BlockBurnEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();
		BlockState newState = event.getBlock().getState();
		newState.setData(new MaterialData(Material.AIR, (byte) 0));
		
		BlockChangeEvent change = new BlockChangeEvent(event, map, null, event.getBlock().getState(), newState);
		Scrimmage.callEvent(change);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockFade(BlockFadeEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();
		BlockState newState = event.getBlock().getState();
		newState.setData(new MaterialData(Material.AIR, (byte) 0));
		
		BlockChangeEvent change = new BlockChangeEvent(event, map, null, event.getBlock().getState(), newState);
		Scrimmage.callEvent(change);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();
		for (Block block : event.getBlocks()) {
			BlockState newState = block.getRelative(event.getDirection()).getState();
			newState.setData(new MaterialData(block.getType(), block.getData()));
			BlockChangeEvent change = new BlockChangeEvent(event, map, null, block.getRelative(event.getDirection()).getState(), newState);
			Scrimmage.callEvent(change);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		Map map = Scrimmage.getRotation().getSlot().getMap();
		if(event.isSticky()) {
			BlockState state = event.getBlock().getWorld().getBlockAt(event.getRetractLocation()).getState();
			BlockState newState = state;
			newState.setData(new MaterialData(Material.AIR, (byte) 0));
			BlockChangeEvent change = new BlockChangeEvent(event, map, null, state, newState);
			Scrimmage.callEvent(change);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
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
	
	@EventHandler(priority = EventPriority.LOW)
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
	
	@EventHandler(priority = EventPriority.LOW)
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
