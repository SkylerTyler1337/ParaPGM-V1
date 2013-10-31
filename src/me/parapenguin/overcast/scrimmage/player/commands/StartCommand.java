package me.parapenguin.overcast.scrimmage.player.commands;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.match.Match;
import me.parapenguin.overcast.scrimmage.player.Client;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args) {
		if(sender instanceof Player) {
			if(!Client.getClient((Player) sender).isRanked()) {
				sender.sendMessage(ChatColor.RED + "No permission!");
				return false;
			}
		}
		
		Match match = Scrimmage.getRotation().getSlot().getMatch();
		if(!match.isCurrentlyStarting()) {
			sender.sendMessage(ChatColor.RED + "A match is already running!");
			return false;
		}
		
		Scrimmage.getRotation().getSlot().getMatch().start();
		sender.sendMessage(ChatColor.RED + sender.getName() + ChatColor.DARK_PURPLE + " has started the countdown.");
		
		return true;
	}
	
}
