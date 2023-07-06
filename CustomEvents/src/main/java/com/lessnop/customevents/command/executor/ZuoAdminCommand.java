package com.lessnop.customevents.command.executor;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.exception.EventNotFoundException;
import com.lessnop.customevents.exception.WrongConfigurationException;
import com.lessnop.customevents.utils.GameEventStatus;
import com.lessnop.customevents.utils.Permissions;
import com.lessnop.customevents.utils.PrefixUtils;
import com.lessnop.customevents.zuo.ZuoManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ZuoAdminCommand extends EventCommandExecutor {

	private static List<String> startAliases = Arrays.asList("start", "rozpocznij"),
			cancelAliases = Arrays.asList("stop", "zatrzymaj", "cancel"),
			list = Arrays.asList("list", "lista", "all"),
			setMainSpawnAliases = Arrays.asList("mainspawn", "main_spawn"),
			setZuoSpawnAliases = Arrays.asList("zuospawn", "zuo_spawn");

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args) {
		if (!sender.hasPermission(Permissions.ZUO_ADMIN_COMMAND)) {
			sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
					"Nie masz uprawnien do uzycia tej komendy!");
			return false;
		}
		try {
			String key = args[0].toLowerCase();
			if (startAliases.contains(key)) {
				ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
				if (!zuoManager.getEventStatus().equals(GameEventStatus.OFF)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Najpierw musisz anulowac aktualny event Zuo poprzez /zuoadmin cancel!");
					return false;
				}
				int time = validateNonNegativeNumber(sender, args[2]);
				if (time < 0) return false;
				String type = args[1];
				try {
					zuoManager.start(type, time);
				} catch (WrongConfigurationException e) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Event Zuo nie jest prawidlowo ustawiony (nieprawidlowe teleporty lub nazwy regionow)!");
				} catch (EventNotFoundException e) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Event Zuo o nazwie " + type + " nie istnieje!");
					return false;
				}
			} else if (cancelAliases.contains(key)) {
				ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
				if (zuoManager.getEventStatus().equals(GameEventStatus.OFF)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Aktualnie nie trwa event Zuo!");
					return false;
				}
				zuoManager.cancel();
			} else if (setZuoSpawnAliases.contains(key)) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Tylko gracz moze ustawic spawn eventu Zuo!");
					return false;
				}
				String type = args[1];
				ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
				if (!zuoManager.eventExists(type)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Event Zuo o nazwie " + type + " nie istnieje!");
					return false;
				}

				Player player = (Player) sender;
				DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
				databaseManager.setLocation("zuo_" + type + "_event_spawn", player.getLocation());
				sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.INFO) +
						"Pomyslnie ustawiles spawn eventu Zuo " + type + "!");
			} else if (setMainSpawnAliases.contains(key)) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Tylko gracz moze ustawic spawn po zakonczeniu eventu OX!");
					return false;
				}
				String type = args[1];
				ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
				if (!zuoManager.eventExists(type)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Event Zuo o nazwie " + type + " nie istnieje!");
					return false;
				}

				Player player = (Player) sender;
				DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
				databaseManager.setLocation("zuo_" + type + "_main_spawn", player.getLocation());
				sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.INFO) +
						"Pomyslnie ustawiles teleport powrotu z eventu Zuo " + type + "!");
			} else if (list.contains(key)) {
				ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
				sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.INFO) +
						"Rodzaje eventow Zuo:");
				for (String event : zuoManager.getEventTypes()) {
					sender.sendMessage(PrefixUtils.getCommandPrefix() + event);
				}
			} else help(sender);
		} catch (IndexOutOfBoundsException e) {
			help(sender);
		}
		return false;
	}

	private void help(CommandSender sender) {
		String pref = PrefixUtils.getCommandPrefix() + "/zuoadmin ";
		sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.INFO) + "Dostepne komendy: ");
		sender.sendMessage(pref + "start <TYPE> <TIME> §7- rozpoczyna dany event Zuo za TIME sekund");
		sender.sendMessage(pref + "cancel §7- anuluje aktualny event Zuo");
		sender.sendMessage(pref + "list §7- wyswietla dostepne rodzaje eventow Zuo");
		sender.sendMessage(pref + "zuo_spawn <TYPE> §7- ustawia teleport do eventu Zuo");
		sender.sendMessage(pref + "main_spawn <TYPE> §7- ustawia teleport powrotu z eventu Zuo");
	}


}
