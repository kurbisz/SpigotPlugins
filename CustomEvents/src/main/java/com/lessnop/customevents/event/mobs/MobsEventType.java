package com.lessnop.customevents.event.mobs;

import com.lessnop.customevents.event.EventType;

import java.util.List;

public class MobsEventType extends EventType {

	protected double spawnChance;
	protected List<String> mobList;

	public MobsEventType(String eventId, String eventName, double spawnChance, List<String> mobList, List<String> commandList) {
		super(eventId, eventName, commandList);
		this.spawnChance = spawnChance;
		this.mobList = mobList;
	}

	public double getSpawnChance() {
		return spawnChance;
	}

	public List<String> getMobList() {
		return mobList;
	}
}
