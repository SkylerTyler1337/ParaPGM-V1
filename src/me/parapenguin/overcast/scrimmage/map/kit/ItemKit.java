package me.parapenguin.overcast.scrimmage.map.kit;

import java.util.List;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.player.Client;

public class ItemKit {
	
	@Getter List<ItemKit> parents;
	
	@Getter String name;
	@Getter List<ItemSlot> items;
	
	public ItemKit(String name, List<ItemSlot> items, List<ItemKit> parents) {
		this.parents = parents;
		this.name = name;
		this.items = items;
	}
	
	public void load(Client client) {
		for(ItemKit parent : parents) {
			parent.load(client);
		}
		
		for(ItemSlot slot : items)
			slot.give(client);
	}
	
}
