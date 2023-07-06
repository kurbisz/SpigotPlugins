package com.lessnop.customevents.command.autocomplete;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.zuo.ZuoManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZuoAdminCompleter implements TabCompleter {

	private static List<String> startAliases = Arrays.asList("start", "rozpocznij");

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length == 1) {
			List<String> list = List.of("start", "cancel", "list", "main_spawn", "zuo_spawn");
			return list;
		}
		if (args.length == 2) {
			ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
			List<String> list = new ArrayList<>(zuoManager.getEventTypes());
			return list;
		}
		return new ArrayList<>();
	}

}
