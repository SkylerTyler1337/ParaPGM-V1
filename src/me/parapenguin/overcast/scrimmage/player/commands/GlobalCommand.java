package me.parapenguin.overcast.scrimmage.player.commands;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.player.Client;
import me.parapenguin.overcast.scrimmage.player.PlayerChatEvent;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args) {
		if(sender instanceof Player == false) {
			sender.sendMessage(ChatColor.RED + "This command is for players only!");
			return false;
		}
		
		if(args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Invalid Arguments supplied!");
			sender.sendMessage(ChatColor.RED + "/g <message>");
			return false;
		}
		
		String message = "";
		int i = 0;
		while(i < args.length) {
			message += " " + args[i];
			i++;
		}
		message = message.substring(1);
		Scrimmage.callEvent(new PlayerChatEvent(Client.getClient((Player) sender), message, false));
		
		return false;
	}
	
}
