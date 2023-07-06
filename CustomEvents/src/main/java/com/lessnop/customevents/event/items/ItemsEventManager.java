package com.lessnop.customevents.event.items;

import com.lessnop.customevents.event.SingleEventManager;

import java.util.HashMap;
import java.util.Set;

public class ItemsEventManager extends SingleEventManager<ItemsEventType> {

	private HashMap<String, ItemsEventType> itemsEventTypes = new HashMap<>();

	public ItemsEventType getEventTypeById(String eventId) {
		return itemsEventTypes.get(eventId);
	}

	public void addEventType(String key, ItemsEventType itemsEventType) {
		itemsEventTypes.put(key, itemsEventType);
	}

	public Set<String> getEventTypeNames() {
		return itemsEventTypes.keySet();
	}
	public boolean containsEvent(String name) {
		return itemsEventTypes.containsKey(name);
	}

}
