package com.lessnop.customevents.scheduler;

import com.lessnop.customevents.CustomEvents;
import org.bukkit.Bukkit;

public class SchedulerManager {

	private static int eventPeriodInSec = 60;
	private static int placeholderPeriodInSec = 7;

	private EventsScheduler eventsScheduler;
	private EventsTopScheduler eventsTopScheduler;
	private PlaceholderScheduler placeholderScheduler;

	public void init() {
		eventsScheduler = new EventsScheduler();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomEvents.getInstance(), eventsScheduler, 4*20, eventPeriodInSec*20);

		eventsTopScheduler = new EventsTopScheduler();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomEvents.getInstance(), eventsTopScheduler, 10*10, eventPeriodInSec*20);

		placeholderScheduler = new PlaceholderScheduler();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomEvents.getInstance(), placeholderScheduler, 4*20, placeholderPeriodInSec*20);

	}

}
