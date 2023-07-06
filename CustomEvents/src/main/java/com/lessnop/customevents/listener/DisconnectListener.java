package com.lessnop.customevents.listener;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.exception.PlayerNotInGameException;
import com.lessnop.customevents.utils.GameEventStatus;
import com.lessnop.customevents.ox.OXManager;
import com.lessnop.customevents.zuo.ZuoManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisconnectListener implements Listener {

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		OXManager oxManager = CustomEvents.getInstance().getOxManager();
		if (!oxManager.getEventStatus().equals(GameEventStatus.OFF)) {
			try {
				oxManager.leavePlayer(e.getPlayer());
			} catch (PlayerNotInGameException ex) {
			}
		}

		ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
		if (!zuoManager.getEventStatus().equals(GameEventStatus.OFF)) {
			try {
				zuoManager.leavePlayer(e.getPlayer());
			} catch (PlayerNotInGameException ex) {
			}
		}
	}

}
