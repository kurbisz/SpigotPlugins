package com.lessnop.customevents.command.autocomplete;

import com.lessnop.customevents.event.EventTypeEnum;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventsAdminCompleter implements TabCompleter {


	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length == 1) {
			List<String> list = List.of("list", "add", "set", "off");
			return list;
		} else if (args.length == 2) {
			List<String> list = List.of("items", "mobs");
			return list;
		} else if (args.length == 3) {
			EventTypeEnum eventTypeEnum = EventTypeEnum.getByName(args[1]);
			List<String> list = new ArrayList<>(eventTypeEnum.getEventManager().getEventTypeNames());
			return list;
		}
		return new ArrayList<>();
	}

}
