package com.lessnop.customevents.scheduler;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.database.TimeData;
import com.lessnop.customevents.event.EventManager;
import com.lessnop.customevents.event.EventType;
import com.lessnop.customevents.event.EventTypeEnum;

import java.util.HashMap;
import java.util.List;

public class EventsScheduler implements Runnable {

	@Override
	public void run() {
		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		if (CustomEvents.isIsMainServer()) {
			databaseManager.decreaseTime();
		}
		HashMap<EventTypeEnum, HashMap<EventType, TimeData>> activeEvents = databaseManager.updateEvents();

		EventManager eventManager = CustomEvents.getInstance().getEventManager();
		eventManager.updateActualEvents(activeEvents);

		List<String> eventsMessages = databaseManager.getMessages();
		eventManager.printEventsMessages(eventsMessages);
		databaseManager.clearMessages();

	}

}
