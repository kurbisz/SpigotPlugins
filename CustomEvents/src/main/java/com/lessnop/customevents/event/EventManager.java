package com.lessnop.customevents.event;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.data.MessageManager;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.database.TimeData;
import com.lessnop.customevents.event.items.ItemsEventManager;
import com.lessnop.customevents.event.mobs.MobsEventManager;
import com.lessnop.customevents.listener.InventoryListener;
import com.lessnop.customevents.listener.ItemsOnKillListener;
import com.lessnop.customevents.listener.OXListener;
import com.lessnop.customevents.listener.ZuoListener;
import com.lessnop.customevents.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.*;

public class EventManager {

	private ItemsEventManager itemsEventManager;
	private MobsEventManager mobsEventManager;
	private List<SingleEventManager> eventManagers = new ArrayList<>();

	private List<Listener> eventListeners = new ArrayList<>();

	public EventManager() {
		itemsEventManager = new ItemsEventManager();
		eventManagers.add(itemsEventManager);

		mobsEventManager = new MobsEventManager();
		eventManagers.add(mobsEventManager);

		eventListeners.add(new ItemsOnKillListener());
		eventListeners.add(new InventoryListener());
		eventListeners.add(new OXListener());
		eventListeners.add(new ZuoListener());
	}

	public void init() {
		CustomEvents main = CustomEvents.getInstance();
		for (Listener listener : eventListeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, main);
		}
	}
	public ItemsEventManager getItemsEventManager() {
		return itemsEventManager;
	}
	public MobsEventManager getMobsEventManager() {
		return mobsEventManager;
	}

	public List<SingleEventManager> getEventManagers() {
		return eventManagers;
	}

	public void addTime(EventTypeEnum eventTypeEnum, String eventId, int time, OfflinePlayer player) {
		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		databaseManager.addTime(eventTypeEnum, eventId, time, false);
		databaseManager.addTime(eventTypeEnum, eventId, time, true);
		databaseManager.updateEvents();

		MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
		EventType eventType = eventTypeEnum.getEventManager().getEventTypeById(eventId);
		if (player != null) {
			String addTimeMsg = StringUtils.replaceVars(messageManager.getMsg("addEventTimePlayer"),
					"%time%", time + "",
					"%event%", eventType.getEventName(),
					"%player%", player.getName());
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.sendMessage(addTimeMsg);
			}
			databaseManager.saveMessage(addTimeMsg);
			databaseManager.addPlayerTimeToTop(player.getUniqueId(), time);
		}
		else {
			String addTimeMsg = StringUtils.replaceVars(messageManager.getMsg("addEventTime"),
					"%time%", time + "",
					"%event%", eventType.getEventName());
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.sendMessage(addTimeMsg);
			}
			databaseManager.saveMessage(addTimeMsg);
		}
	}

	public void setTime(EventTypeEnum eventTypeEnum, String eventId, int time, OfflinePlayer player) {
		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		databaseManager.setTime(eventTypeEnum, eventId, time, false);
		databaseManager.setTime(eventTypeEnum, eventId, time, true);
		databaseManager.updateEvents();

		MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
		EventType eventType = eventTypeEnum.getEventManager().getEventTypeById(eventId);
		if (player != null) {
			String setTimeMsg = StringUtils.replaceVars(messageManager.getMsg("setEventTimePlayer"),
					"%time%", time + "",
					"%event%", eventType.getEventName(),
					"%player%", player.getName());
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.sendMessage(setTimeMsg);
			}
			databaseManager.saveMessage(setTimeMsg);
			databaseManager.addPlayerTimeToTop(player.getUniqueId(), time);
		}
		else {
			String setTimeMsg = StringUtils.replaceVars(messageManager.getMsg("setEventTime"),
					"%time%", time + "",
					"%event%", eventType.getEventName());
			for (Player pl : Bukkit.getOnlinePlayers()) {
				pl.sendMessage(setTimeMsg);
			}
			databaseManager.saveMessage(setTimeMsg);
		}
	}

	public HashMap<EventTypeEnum, HashMap<EventType, TimeData>> getActualEvents() {
		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		return databaseManager.getActiveEvents();
	}

	public HashMap<EventType, TimeData> getActualEvents(EventTypeEnum eventTypeEnum) {
		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		HashMap<EventType, TimeData> events = databaseManager.getActiveEvents().get(eventTypeEnum);
		return events != null ? events : new HashMap<>();
	}

	public void updateActualEvents(HashMap<EventTypeEnum, HashMap<EventType, TimeData>> activeEvents) {
		for (Map.Entry<EventTypeEnum, HashMap<EventType, TimeData>> event : activeEvents.entrySet()) {
			for (Map.Entry<EventType, TimeData> entry : event.getValue().entrySet()) {
				TimeData timeData = entry.getValue();
				if (timeData.getTime() == 0) {
					endEvent(event.getKey(), entry.getKey());
				}
			}
		}
	}

	private void endEvent(EventTypeEnum eventTypeEnum, EventType eventType) {
		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		databaseManager.endEvent(eventTypeEnum, eventType);

		MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
		String endMessage = StringUtils.replaceVars(messageManager.getMsg("endEvent"),
				"%event%", eventType.getEventName());
		for (Player pl : Bukkit.getOnlinePlayers()) {
			pl.sendMessage(endMessage);
		}

		databaseManager.saveMessage(endMessage);
	}

	public void printEventsMessages(List<String> eventsMessages) {
		for (String str : eventsMessages) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(str);
			}
		}
	}
}
