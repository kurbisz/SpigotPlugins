package com.lessnop.customevents.listener;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.data.MessageManager;
import com.lessnop.customevents.utils.GameEventStatus;
import com.lessnop.customevents.ox.OXManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OXListener implements Listener {

	@EventHandler
	public void onChat(AsyncChatEvent e) {
		OXManager oxManager = CustomEvents.getInstance().getOxManager();
		if (oxManager != null && oxManager.getEventStatus().equals(GameEventStatus.ACTIVE)) {
			e.setCancelled(true);
			MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
			e.getPlayer().sendMessage(messageManager.getMsg("ox.cannotChat"));
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		OXManager oxManager = CustomEvents.getInstance().getOxManager();
		if (oxManager != null && oxManager.getEventStatus().equals(GameEventStatus.WAITING_FOR_PLAYERS)) {
			oxManager.addPlayerToBossBar(e.getPlayer());
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		OXManager oxManager = CustomEvents.getInstance().getOxManager();
		if (oxManager != null) {
			oxManager.removePlayerFromBossBar(e.getPlayer());
		}
	}

}
