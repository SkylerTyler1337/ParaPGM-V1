package me.parapenguin.overcast.scrimmage.map.kit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.potion.PotionEffect;

import lombok.Getter;
import me.parapenguin.overcast.scrimmage.player.Client;

public class ItemKit {
	
	@Getter List<ItemKit> parents;
	
	@Getter String name;
	@Getter List<ItemSlot> items;
	@Getter List<PotionEffect> effects;
	
	public ItemKit(String name, List<ItemSlot> items, List<ItemKit> parents) {
		this(name, items, parents, new ArrayList<PotionEffect>());
	}
	
	public ItemKit(String name, List<ItemSlot> items, List<ItemKit> parents, List<PotionEffect> effects) {
		this.parents = parents;
		this.name = name;
		this.items = items;
		this.effects = effects;
	}
	
	public void load(Client client) {
		for(ItemKit parent : parents)
			parent.load(client);
		
		for(ItemSlot slot : items)
			slot.give(client);
		
		for(PotionEffect effect : effects)
			client.getPlayer().addPotionEffect(effect);
	}
	
}
