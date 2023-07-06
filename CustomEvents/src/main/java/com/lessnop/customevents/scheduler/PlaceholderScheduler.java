package com.lessnop.customevents.scheduler;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.database.TimeData;
import com.lessnop.customevents.event.EventManager;
import com.lessnop.customevents.event.EventType;
import com.lessnop.customevents.placeholder.PlaceholderManager;
import com.lessnop.customevents.event.EventTypeEnum;
import org.bukkit.Bukkit;

import java.util.*;

public class PlaceholderScheduler implements Runnable {

	Random r = new Random();

	@Override
	public void run() {
		EventManager eventManager = CustomEvents.getInstance().getEventManager();
		HashMap<EventTypeEnum, HashMap<EventType, TimeData>> actualEvents = eventManager.getActualEvents();
		Map.Entry<EventType, TimeData> randomEvent = null;
		List<Map.Entry<EventType, TimeData>> list = new ArrayList<>();
		for (HashMap<EventType, TimeData> map : actualEvents.values()) {
			for (Map.Entry<EventType, TimeData> entry : map.entrySet()) {
				if (entry.getValue().getTime() > 0) {
					list.add(entry);
				}
			}
		}

		if (list.size() > 0) {
			Map.Entry<EventType, TimeData> finalRandomEvent = list.get(r.nextInt(list.size()));

			randomEvent = new Map.Entry<EventType, TimeData>() {
				@Override
				public EventType getKey() {
					return finalRandomEvent.getKey();
				}

				@Override
				public TimeData getValue() {
					return finalRandomEvent.getValue();
				}

				@Override
				public TimeData setValue(TimeData value) {
					return null;
				}
			};
		}

		PlaceholderManager placeholderManager = CustomEvents.getInstance().getPlaceholderManager();
		placeholderManager.setActualEvent(randomEvent);

	}

}
