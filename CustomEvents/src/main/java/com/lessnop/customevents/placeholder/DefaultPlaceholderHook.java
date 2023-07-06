package com.lessnop.customevents.placeholder;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.database.TimeData;
import com.lessnop.customevents.event.EventManager;
import com.lessnop.customevents.event.EventType;
import com.lessnop.customevents.event.EventTypeEnum;
import com.lessnop.customevents.event.items.ItemsEventType;
import com.lessnop.customevents.event.mobs.MobsEventType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class DefaultPlaceholderHook extends PlaceholderExpansion {


	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String getAuthor() {
		return CustomEvents.getInstance().getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier() {
		return "customevents";
	}

	@Override
	public String getVersion() {
		return CustomEvents.getInstance().getDescription().getVersion();
	}


	@Override
	public String onPlaceholderRequest(Player p, String params) {

		if (p == null) {
			return null;
		}

		if (params.equalsIgnoreCase("general")) {
			PlaceholderManager placeholderManager = CustomEvents.getInstance().getPlaceholderManager();
			return placeholderManager.getActualEvent();
		}

		if (params.startsWith("items") || params.startsWith("mobs")) {
			PlaceholderManager placeholderManager = CustomEvents.getInstance().getPlaceholderManager();

			String eventId = params.split("_")[1];
			EventManager eventManager = CustomEvents.getInstance().getEventManager();
			EventTypeEnum eventTypeEnum = params.startsWith("items") ? EventTypeEnum.ITEMS_EVENT_TYPE : EventTypeEnum.MOBS_EVENT_TYPE;
			HashMap<EventType, TimeData> events = eventManager.getActualEvents(eventTypeEnum);
			for (Map.Entry<EventType, TimeData> entry : events.entrySet()) {
				if (eventTypeEnum.equals(EventTypeEnum.ITEMS_EVENT_TYPE) && entry.getKey() instanceof MobsEventType) continue;
				if (eventTypeEnum.equals(EventTypeEnum.MOBS_EVENT_TYPE) && entry.getKey() instanceof ItemsEventType) continue;
				if (entry.getKey().getEventId().equalsIgnoreCase(eventId)) {
					int time = entry.getValue().getTime();
					if (time > 0)
						return placeholderManager.getFormattedTime(time);
					else
						return placeholderManager.getZeroTime();
				}
			}
			return "ERROR";
		}


		return null;
	}


}
