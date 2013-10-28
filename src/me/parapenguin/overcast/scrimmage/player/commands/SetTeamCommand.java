package me.parapenguin.overcast.scrimmage.player.commands;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.map.Map;
import me.parapenguin.overcast.scrimmage.map.MapTeam;
import me.parapenguin.overcast.scrimmage.player.Client;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTeamCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args) {
		if(sender instanceof Player) {
			if(!Client.getClient((Player) sender).isRanked()) {
				sender.sendMessage(ChatColor.RED + "No permission!");
				return false;
			}
		}
		
		Map map = Scrimmage.getRotation().getSlot().getMap();
		
		if(args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Invalid Arguments supplied!");
			sender.sendMessage(ChatColor.RED + "/join [team]");
			return false;
		}
		
		MapTeam team = map.getTeam(args[0]);
		
		String name = "";
		int i = 1;
		while(i < args.length) {
			name += " " + args[i];
			i++;
		}
		name = name.substring(1);
		
		team.setDisplayName(name, true);
		sender.sendMessage(team.getColor() + team.getName() + ChatColor.GRAY + " has been changed to " + team.getColor() + team.getDisplayName() + ChatColor.GRAY + ".");
		
		return false;
	}
	
}
