package com.lessnop.customevents.event.mobs;

import com.lessnop.customevents.event.SingleEventManager;

import java.util.HashMap;
import java.util.Set;

public class MobsEventManager extends SingleEventManager<MobsEventType> {

	private HashMap<String, MobsEventType> mobsEventTypes = new HashMap<>();

	public MobsEventType getEventTypeById(String eventId) {
		return mobsEventTypes.get(eventId);
	}

	public void addEventType(String key, MobsEventType mobsEventType) {
		mobsEventTypes.put(key, mobsEventType);
	}

	public Set<String> getEventTypeNames() {
		return mobsEventTypes.keySet();
	}
	public boolean containsEvent(String name) {
		return mobsEventTypes.containsKey(name);
	}

}
