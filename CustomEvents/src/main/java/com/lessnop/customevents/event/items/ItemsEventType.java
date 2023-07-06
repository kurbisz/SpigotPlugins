package com.lessnop.customevents.event.items;

import com.lessnop.customevents.event.EventType;

import java.util.List;

public class ItemsEventType extends EventType {

	protected int maxLevelDifference;
	protected double dropChance;
	protected List<String> mobList;

	public ItemsEventType(String eventId, String eventName, int maxLevelDifference, double dropChance, List<String> mobList, List<String> commandList) {
		super(eventId, eventName, commandList);
		this.maxLevelDifference = maxLevelDifference;
		this.dropChance = dropChance;
		this.mobList = mobList;
	}

	public int getMaxLevelDifference() {
		return maxLevelDifference;
	}

	public double getDropChance() {
		return dropChance;
	}

	public List<String> getMobList() {
		return mobList;
	}
}
