package com.lessnop.customevents.scheduler;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.database.TimeData;
import com.lessnop.customevents.event.EventManager;
import com.lessnop.customevents.event.EventType;
import com.lessnop.customevents.event.EventTypeEnum;

import java.util.HashMap;

public class EventsTopScheduler implements Runnable {

	@Override
	public void run() {
		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		databaseManager.updateTopPlayers();

	}

}
