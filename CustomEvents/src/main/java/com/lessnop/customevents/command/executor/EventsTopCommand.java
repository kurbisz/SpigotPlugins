package com.lessnop.customevents.command.executor;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.inventory.EventsInventoryHolder;
import com.lessnop.customevents.utils.PrefixUtils;
import com.lessnop.customevents.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EventsTopCommand extends EventCommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args) {

		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
						"Tylko gracz moze wykonac te komende!");
				return false;
			}
			Player player = (Player) sender;
			EventsInventoryHolder eventsInventoryHolder = new EventsInventoryHolder();
			player.openInventory(eventsInventoryHolder.getInventory());
		} else {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
			int am = 0;
			if (offlinePlayer != null) {
				DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
				Integer amount = databaseManager.getTopPlayers().get(offlinePlayer.getUniqueId());
				if (amount != null) am = amount;
			}
			String msg = CustomEvents.getInstance().getMessageManager().getMsg("eventCommandAmount");
			msg = StringUtils.replaceVars(msg, "%player%", args[0], "%am%", am + "");
			sender.sendMessage(msg);
		}
		return false;
	}


}
