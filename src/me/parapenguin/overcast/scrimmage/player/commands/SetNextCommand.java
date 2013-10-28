package me.parapenguin.overcast.scrimmage.player.commands;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.MapLoader;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.rotation.Rotation;
import me.parapenguin.overcast.scrimmage.rotation.RotationSlot;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetNextCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args) {
		if(sender instanceof Player) {
			if(!Client.getClient((Player) sender).isRanked()) {
				sender.sendMessage(ChatColor.RED + "No permission!");
				return false;
			}
		}
		
		if(args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Invalid Arguments supplied!");
			sender.sendMessage(ChatColor.RED + "/setnext <map>");
			return false;
		}
		
		String name = "";
		int i = 0;
		while(i < args.length) {
			name += " " + args[i];
			i++;
		}
		name = name.substring(1);
		
		MapLoader found = Rotation.getMap(name);
		if(found == null) {
			sender.sendMessage(ChatColor.GRAY + "We could not find a map by the name '" + ChatColor.GOLD + name + ChatColor.GRAY + "'.");
			return false;
		}
		
		Rotation rot = Scrimmage.getRotation();
		RotationSlot after = rot.getNext();
		
		rot.setNext(new RotationSlot(found));
		
		RotationSlot current = rot.getSlot();
		if(current.getMatch().isLoaded()) {
			after.getMap().unload();
			rot.getNext().load();
			after.getMatch().setLoaded(true);
		}
		
		sender.sendMessage(ChatColor.RED + sender.getName() + ChatColor.DARK_PURPLE + " set the next map to " + ChatColor.GOLD + found.getName() + ChatColor.DARK_PURPLE + "!");
		
		return false;
	}
	
}
