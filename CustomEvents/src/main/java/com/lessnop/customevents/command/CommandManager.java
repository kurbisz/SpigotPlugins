package com.lessnop.customevents.command;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.command.autocomplete.*;
import com.lessnop.customevents.command.executor.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

	private HashMap<String, CommandExecutor> commands = new HashMap<>();
	private HashMap<String, TabCompleter> completers = new HashMap<>();

	public CommandManager() {
		commands.put("customeventsadmin", new EventsAdminCommand());
		commands.put("customeventstop", new EventsTopCommand());
		commands.put("oxadmin", new OXAdminCommand());
		commands.put("ox", new OXCommand());
		commands.put("zuoadmin", new ZuoAdminCommand());
		commands.put("zuo", new ZuoCommand());

		completers.put("customeventsadmin", new EventsAdminCompleter());
		completers.put("oxadmin", new OXAdminCompleter());
		completers.put("ox", new OXCompleter());
		completers.put("zuoadmin", new ZuoAdminCompleter());
		completers.put("zuo", new ZuoCompleter());
	}

	public void registerCommands() {
		CustomEvents customEvents = CustomEvents.getInstance();
		for (Map.Entry<String, CommandExecutor> entry : commands.entrySet()) {
			customEvents.getCommand(entry.getKey()).setExecutor(entry.getValue());
		}

		for (Map.Entry<String, TabCompleter> entry : completers.entrySet()) {
			customEvents.getCommand(entry.getKey()).setTabCompleter(entry.getValue());
		}
	}

}
