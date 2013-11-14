package me.parapenguin.overcast.scrimmage.player.commands;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.match.Match;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.utils.ConversionUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CycleCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args) {
		if(sender instanceof Player) {
			if(!Client.getClient((Player) sender).isRanked()) {
				sender.sendMessage(ChatColor.RED + "No permission!");
				return false;
			}
		}
		
		Match match = Scrimmage.getRotation().getSlot().getMatch();
		if(match.isCurrentlyRunning()) {
			match.end(true);
		}
		
		int time = 0;
		if(args.length == 1)
			if(ConversionUtil.convertStringToInteger(args[0], 0) => 5)
				time = ConversionUtil.convertStringToInteger(args[0], 0);
			else {
				sender.sendMessage(ChatColor.RED + "Please supply a valid time greater than or equal to 5");
				return false;
			}
		
		if(!match.isCurrentlyCycling()) Scrimmage.getRotation().getSlot().getMatch().stop();
		else Scrimmage.getRotation().getSlot().getMatch().setCycling(time);
		
		Scrimmage.getRotation().getSlot().getMatch().cycle(time);
		Scrimmage.broadcast(ChatColor.RED + sender.getName() + ChatColor.DARK_PURPLE + " has started the cycle.");
		
		return true;
	}
	
}
