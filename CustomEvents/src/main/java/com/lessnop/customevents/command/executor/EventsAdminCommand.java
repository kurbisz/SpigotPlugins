package com.lessnop.customevents.command.executor;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.event.EventManager;
import com.lessnop.customevents.event.EventTypeEnum;
import com.lessnop.customevents.event.items.ItemsEventManager;
import com.lessnop.customevents.event.mobs.MobsEventManager;
import com.lessnop.customevents.utils.Permissions;
import com.lessnop.customevents.utils.PrefixUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class EventsAdminCommand extends EventCommandExecutor {

	private static List<String> addAliases = Arrays.asList("a", "add", "dodaj"),
			listAliases = Arrays.asList("list", "lista", "all"),
			setAliases = Arrays.asList("s", "set", "ustaw"),
			offAliases = Arrays.asList("off", "wylacz");

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args) {
		if (!sender.hasPermission(Permissions.EVENTS_ADMIN_COMMAND)) {
			sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
					"Nie masz uprawnien do uzycia tej komendy!");
			return false;
		}
		if (args.length > 0) {
			String key = args[0].toLowerCase();
			if (addAliases.contains(key) || setAliases.contains(key)) {
				if (!validateCommandLength(sender, args, 4, 5)) return false;

				EventTypeEnum eventTypeEnum = EventTypeEnum.getByName(args[1]);
				String eventName = args[2];
				if(!validateEventName(sender, eventName, eventTypeEnum)) return false;

				int time = validateNonNegativeNumber(sender, args[3]);
				if (time < 0) return false;

				OfflinePlayer player = args.length == 5 ? validatePlayer(sender, args[4]) : null;
				EventManager eventManager = CustomEvents.getInstance().getEventManager();
				if (addAliases.contains(key)) {
					eventManager.addTime(eventTypeEnum, eventName, time, player);
				}
				else {
					eventManager.setTime(eventTypeEnum, eventName, time, player);
				}
			}
			else if (offAliases.contains(key)) {
				if (!validateCommandLength(sender, args, 3)) return false;
				EventTypeEnum eventTypeEnum = EventTypeEnum.getByName(args[1]);
				String eventName = args[2];
				if(!validateEventName(sender, eventName, eventTypeEnum)) return false;
				EventManager eventManager = CustomEvents.getInstance().getEventManager();
				eventManager.setTime(eventTypeEnum, eventName, 0, null);
			} else if (listAliases.contains(key)) {
				sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.INFO) +
						"Rodzaje eventow z dropem itemow:");
				ItemsEventManager itemsEventManager = CustomEvents.getInstance().getEventManager().getItemsEventManager();
				for (String name : itemsEventManager.getEventTypeNames()) {
					sender.sendMessage(PrefixUtils.getCommandPrefix() + name);
				}
				sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.INFO) +
						"Rodzaje eventow z respem bossow:");
				MobsEventManager mobsEventManager = CustomEvents.getInstance().getEventManager().getMobsEventManager();
				for (String name : mobsEventManager.getEventTypeNames()) {
					sender.sendMessage(PrefixUtils.getCommandPrefix() + name);
				}
			} else help(sender);
		} else help(sender);
		return false;
	}

	private void help(CommandSender sender) {
		String pref = PrefixUtils.getCommandPrefix() + "/szkatyadmin ";
		sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.INFO) + "Dostepne komendy: ");
		sender.sendMessage(pref + "list §7- lista wszystkich eventow w configu");
		sender.sendMessage(pref + "add <items/mobs> <name> <MIN> [PLAYER] §7- dodaje czas do aktywnych szkat");
		sender.sendMessage(pref + "set <items/mobs> <name> <MIN> [PLAYER] §7- ustawia czas aktywnych szkat");
		sender.sendMessage(pref + "off <items/mobs> <name> §7- wylacza aktywne szkaty");
	}


}
