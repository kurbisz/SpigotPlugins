package com.lessnop.customevents.command.executor;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.utils.GameEventStatus;
import com.lessnop.customevents.ox.OXManager;
import com.lessnop.customevents.ox.Question;
import com.lessnop.customevents.exception.QuestionIsActiveException;
import com.lessnop.customevents.exception.WrongConfigurationException;
import com.lessnop.customevents.utils.Permissions;
import com.lessnop.customevents.utils.PrefixUtils;
import com.lessnop.customevents.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class OXAdminCommand extends EventCommandExecutor {

	private static List<String> startAliases = Arrays.asList("start", "rozpocznij"),
			cancelAliases = Arrays.asList("stop", "zatrzymaj", "cancel"),
			questionAliases = Arrays.asList("question", "pytanie", "ask"),
			randomQuestionAliases = Arrays.asList("question_random", "ask_random"),
			setMainSpawnAliases = Arrays.asList("main_spawn", "mainspawn"),
			setOXSpawnAliases = Arrays.asList("ox_spawn", "oxspawn");

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args) {
		if (!sender.hasPermission(Permissions.OX_ADMIN_COMMAND)) {
			sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
					"Nie masz uprawnien do uzycia tej komendy!");
			return false;
		}
		try {
			String key = args[0].toLowerCase();
			if (startAliases.contains(key)) {
				OXManager oxManager = CustomEvents.getInstance().getOxManager();
				if (!oxManager.getEventStatus().equals(GameEventStatus.OFF)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Najpierw musisz anulowac aktualny event OX poprzez /oxadmin cancel!");
					return false;
				}
				int time = validateNonNegativeNumber(sender, args[1]);
				if (time < 0) return false;
				boolean customQuestions = StringUtils.stringToBool(args[2]);
				boolean customRewards = StringUtils.stringToBool(args[3]);
				try {
					oxManager.start(time, customQuestions, customRewards);
				} catch (WrongConfigurationException e) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Event OX nie jest prawidlowo ustawiony (nieprawidlowe teleporty lub nazwy regionow)!");
				}
			} else if (questionAliases.contains(key)) {
				OXManager oxManager = CustomEvents.getInstance().getOxManager();
				if (oxManager.getEventStatus().equals(GameEventStatus.OFF)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Aktualnie nie trwa event OX!");
					return false;
				}
				String question = "";
				for (int i = 0; i < args.length - 1; i++) {
					question += args[i];
					if (i != args.length - 2) question += "";
				}
				question = StringUtils.replaceColors(question);
				boolean answer = StringUtils.stringToBool(args[args.length - 1]);
				Question questionObject = new Question(question, answer);
				try {
					oxManager.askQuestion(questionObject);
				} catch (QuestionIsActiveException e) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Zaczekaj az aktualne pytanie sie zakonczy!");
					return false;
				}
			} else if (randomQuestionAliases.contains(key)) {
				OXManager oxManager = CustomEvents.getInstance().getOxManager();
				if (oxManager.getEventStatus().equals(GameEventStatus.OFF)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Aktualnie nie trwa event OX!");
					return false;
				}
				try {
					oxManager.askRandomQuestion();
				} catch (QuestionIsActiveException e) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Zaczekaj az aktualne pytanie sie zakonczy!");
					return false;
				}
			} else if (cancelAliases.contains(key)) {
				OXManager oxManager = CustomEvents.getInstance().getOxManager();
				if (oxManager.getEventStatus().equals(GameEventStatus.OFF)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Aktualnie nie trwa event OX!");
					return false;
				}
				oxManager.cancel();
			} else if (setOXSpawnAliases.contains(key)) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Tylko gracz moze ustawic spawn eventu OX!");
					return false;
				}
				Player player = (Player) sender;
				DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
				databaseManager.setLocation("ox_event_spawn", player.getLocation());
				sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.INFO) +
						"Pomyslnie ustawiles spawn eventu OX!");
			} else if (setMainSpawnAliases.contains(key)) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
							"Tylko gracz moze ustawic spawn po zakonczeniu eventu OX!");
					return false;
				}
				Player player = (Player) sender;
				DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
				databaseManager.setLocation("ox_main_spawn", player.getLocation());
				sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.INFO) +
						"Pomyslnie ustawiles teleport powrotu z eventu OX!");
			} else help(sender);
		} catch (IndexOutOfBoundsException e) {
			help(sender);
		}
		return false;
	}

	private void help(CommandSender sender) {
		String pref = PrefixUtils.getCommandPrefix() + "/oxadmin ";
		sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.INFO) + "Dostepne komendy: ");
		sender.sendMessage(pref + "start <TIME> <CUSTOM_QUESTIONS> <CUSTOM_REWARDS> §7- rozpoczyna OX za TIME sekund (kolejne argumenty jako yes/no)");
		sender.sendMessage(pref + "ask <QUESTION> <yes/no> §7- zadaje pytanie w rozpoczetym evencie");
		sender.sendMessage(pref + "ask_random §7- zadaje losowe pytanie z puli z configu");
		sender.sendMessage(pref + "cancel §7- anuluje aktualny event ox");
		sender.sendMessage(pref + "ox_spawn §7- ustawia teleport do eventu ox");
		sender.sendMessage(pref + "main_spawn §7- ustawia teleport powrotu z eventu ox");
	}


}
