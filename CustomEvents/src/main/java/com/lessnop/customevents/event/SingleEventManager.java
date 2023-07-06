package com.lessnop.customevents.event;

import java.util.Set;

public abstract class SingleEventManager<T extends EventType> {

	public abstract T getEventTypeById(String eventId);
	public abstract void addEventType(String key, T itemsEventType);
	public abstract Set<String> getEventTypeNames();
	public abstract boolean containsEvent(String name);

}
