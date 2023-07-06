package com.lessnop.customevents.command.executor;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.data.MessageManager;
import com.lessnop.customevents.event.EventTypeEnum;
import com.lessnop.customevents.event.SingleEventManager;
import com.lessnop.customevents.utils.PrefixUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class EventCommandExecutor implements CommandExecutor {

	protected boolean validateCommandLength(CommandSender commandSender, String[] args, int min, int max) {
		int len = args.length;
		if (len < min || len > max) {
			commandSender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
					"Opcja " + args[0] + " wymaga " + min + " - " + max + " argumentow!");
			return false;
		}
		return true;
	}

	protected boolean validateCommandLength(CommandSender commandSender, String[] args, int val) {
		int len = args.length;
		if (len != val) {
			commandSender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
					"Opcja " + args[0] + " wymaga " + val + " argumentow!");
			return false;
		}
		return true;
	}

	protected boolean validateEventName(CommandSender commandSender, String eventName, EventTypeEnum eventTypeEnum) {
		SingleEventManager singleEventManager = eventTypeEnum.getEventManager();
		if (!singleEventManager.containsEvent(eventName)) {
			commandSender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
					"Event o nazwie " + eventName + " nie istnieje (typ " + eventTypeEnum.getName() + ")!");
			return false;
		}
		return true;
	}

	protected int validateNonNegativeNumber(CommandSender commandSender, String numberString) {
		try {
			int value = Integer.parseInt(numberString);
			if (value < 0) {
				commandSender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
						"Argument " + numberString + " musi byc wiekszy lub rowny 0!");
				return -1;
			}
			return value;
		} catch (NumberFormatException nfe) {
			commandSender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
					"Argument " + numberString + " powinien byc liczba!");
			return -1;
		}
	}

	protected OfflinePlayer validatePlayer(CommandSender commandSender, String name) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		if (player == null) {
			commandSender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
					"Gracz o nicku " + name + " nie byl nigdy na serwerze!");
		}
		return player;
	}

	protected void printHelp(CommandSender sender, String str) {
		MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
		for (String msg : messageManager.getCmdHelp(str)) {
			sender.sendMessage(msg);
		}
	}

}
