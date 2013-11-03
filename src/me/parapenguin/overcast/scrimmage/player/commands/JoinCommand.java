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

public class JoinCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args) {
		if(sender instanceof Player == false) {
			sender.sendMessage(ChatColor.RED + "This command is for players only!");
			return false;
		}
		
		Map map = Scrimmage.getRotation().getSlot().getMap();
		Client client = Client.getClient((Player) sender);
		
		MapTeam team = map.getObservers();
		if(args.length == 0) {
			team = map.getLowest();
		} else if(args.length == 1) {
			team = map.getTeam(args[0]);
			if(team == null) {
				sender.sendMessage(ChatColor.RED + "Could not find a team by that string!");
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Invalid Arguments supplied!");
			sender.sendMessage(ChatColor.RED + "/join [team]");
			return false;
		}

		client.setTeam(team);
		sender.sendMessage(ChatColor.GRAY + "You have joined the " + team.getColor() + team.getDisplayName() + ChatColor.GRAY + ".");
		
		return false;
	}
	
}
