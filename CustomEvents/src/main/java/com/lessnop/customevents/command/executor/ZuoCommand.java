package com.lessnop.customevents.command.executor;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.data.MessageManager;
import com.lessnop.customevents.exception.PlayerNotInGameException;
import com.lessnop.customevents.utils.GameEventStatus;
import com.lessnop.customevents.utils.PrefixUtils;
import com.lessnop.customevents.zuo.ZuoManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ZuoCommand extends EventCommandExecutor {

	private static List<String> joinAliases = Arrays.asList("dolacz", "join"),
			leaveAliases = Arrays.asList("opusc", "leave");

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
					"Tylko gracz moze wykonac te komende!");
			return false;
		}
		Player player = (Player) sender;
		if (args.length != 1) {
			printHelp(player);
			return false;
		}
		ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
		MessageManager messageManager = CustomEvents.getInstance().getMessageManager();

		String key = args[0].toLowerCase();
		if (joinAliases.contains(key)) {
			if (zuoManager.getEventStatus().equals(GameEventStatus.OFF)) {
				player.sendMessage(messageManager.getMsg("zuo.cannotJoin"));
				return false;
			}
			zuoManager.joinPlayer(player);
			player.sendMessage(messageManager.getMsg("zuo.joined"));
		}
		else if (leaveAliases.contains(key)) {
			try {
				zuoManager.leavePlayer(player);
				player.sendMessage(messageManager.getMsg("zuo.left"));
			} catch (PlayerNotInGameException e) {
				player.sendMessage(messageManager.getMsg("zuo.notInGame"));
			}
		}
		else printHelp(player);

		return false;
	}

	private void printHelp(CommandSender sender) {
		super.printHelp(sender, "zuo");
	}


}
