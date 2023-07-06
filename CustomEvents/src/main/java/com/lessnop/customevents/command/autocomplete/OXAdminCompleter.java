package com.lessnop.customevents.command.autocomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OXAdminCompleter implements TabCompleter {

	private static List<String> startAliases = Arrays.asList("start", "rozpocznij");

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length == 1) {
			List<String> list = List.of("start", "cancel", "question", "question_random", "main_spawn", "ox_spawn");
			return list;
		}
		if (args.length == 3 || args.length == 4) {
			if (startAliases.contains(args[0].toLowerCase())) {
				List<String> list = List.of("yes", "no");
				return list;
			}
		}
		return new ArrayList<>();
	}

}
