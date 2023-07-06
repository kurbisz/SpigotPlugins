package com.lessnop.customevents.listener;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.database.TimeData;
import com.lessnop.customevents.event.EventType;
import com.lessnop.customevents.event.EventTypeEnum;
import com.lessnop.customevents.event.items.ItemsEventType;
import com.lessnop.customevents.event.mobs.MobsEventType;
import com.lessnop.customevents.ox.OXManager;
import com.lessnop.customevents.utils.GameEventStatus;
import com.lessnop.customevents.utils.StringUtils;
import com.lessnop.customevents.zuo.ZuoManager;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ZuoListener implements Listener {

	Random r = new Random();


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMythicMobKill(MythicMobDeathEvent e) {
		ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
		if (zuoManager.getEventStatus().equals(GameEventStatus.ACTIVE)) {
			zuoManager.removeMob(e.getMob());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
		if (zuoManager != null && zuoManager.getEventStatus().equals(GameEventStatus.WAITING_FOR_PLAYERS)) {
			zuoManager.addPlayerToBossBar(e.getPlayer());
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
		if (zuoManager != null) {
			zuoManager.removePlayerFromBossBar(e.getPlayer());
		}
	}

}
