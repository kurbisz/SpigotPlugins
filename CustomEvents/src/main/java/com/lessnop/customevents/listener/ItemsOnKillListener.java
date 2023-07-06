package com.lessnop.customevents.listener;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.database.TimeData;
import com.lessnop.customevents.event.EventType;
import com.lessnop.customevents.event.items.ItemsEventType;
import com.lessnop.customevents.event.EventTypeEnum;
import com.lessnop.customevents.event.mobs.MobsEventType;
import com.lessnop.customevents.utils.StringUtils;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ItemsOnKillListener implements Listener {

	Random r = new Random();


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMythicMobKill(MythicMobDeathEvent e) {
		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		HashMap<EventTypeEnum, HashMap<EventType, TimeData>> events = databaseManager.getActiveEvents();

		HashMap<EventType, TimeData> itemsEvents = events.get(EventTypeEnum.ITEMS_EVENT_TYPE);
		if (itemsEvents != null && !itemsEvents.isEmpty()) {
			LivingEntity livingEntity = e.getKiller();
			if (livingEntity != null && livingEntity instanceof Player) {
				Player player = (Player) livingEntity;
				Location loc = player.getLocation();

				String mobType = e.getMob().getMobType();
				double mobLvl = e.getMobLevel();
				double playerLvl = PlayerData.get(player.getUniqueId()).getLevel();
				double lvlDiff = mobLvl - playerLvl;
				for (Map.Entry<EventType, TimeData> singleEvent : itemsEvents.entrySet()) {
					if (singleEvent.getValue().getTime() <= 0) continue;
					ItemsEventType eventType = (ItemsEventType) singleEvent.getKey();
					if (lvlDiff < eventType.getMaxLevelDifference() && eventType.getMobList().contains(mobType) && r.nextDouble() < eventType.getDropChance()) {
						for (String cmd : eventType.getCommandList()) {
							String res = StringUtils.replaceVars(cmd,
									"%player%", player.getName(),
									"%world%", loc.getWorld().getName(),
									"%x%", loc.getX() + "",
									"%y%", loc.getY() + "",
									"%z%", loc.getZ() + "");
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), res);
						}
					}
				}
			}
		}

		HashMap<EventType, TimeData> mobsEvents = events.get(EventTypeEnum.MOBS_EVENT_TYPE);
		if (mobsEvents != null && !mobsEvents.isEmpty()) {
			Location loc = e.getEntity().getLocation();
			String mobType = e.getMob().getMobType();
			double mobLevel = e.getMobLevel();

			for (Map.Entry<EventType, TimeData> singleEvent : mobsEvents.entrySet()) {
				if (singleEvent.getValue().getTime() <= 0) continue;
				MobsEventType eventType = (MobsEventType) singleEvent.getKey();
				if (eventType.getMobList().contains(mobType) && r.nextDouble() < eventType.getSpawnChance()) {
					for (String cmd : eventType.getCommandList()) {
						String res = StringUtils.replaceVars(cmd,
								"%world%", loc.getWorld().getName(),
								"%x%", loc.getX() + "",
								"%y%", loc.getY() + "",
								"%z%", loc.getZ() + "",
								"%type%", mobType,
								"%lvl%", mobLevel + "");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), res);
					}
				}
			}
		}


	}

}
