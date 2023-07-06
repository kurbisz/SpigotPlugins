package com.lessnop.customevents.event;

import java.util.List;

public class EventType {

	protected String eventId, eventName;
	protected List<String> commandList;

	public EventType(String eventId, String eventName, List<String> commandList) {
		this.eventId = eventId;
		this.eventName = eventName;
		this.commandList = commandList;
	}

	public String getEventId() {
		return eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public List<String> getCommandList() {
		return commandList;
	}
}
