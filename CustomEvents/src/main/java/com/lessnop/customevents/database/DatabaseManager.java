package com.lessnop.customevents.database;

import com.lessnop.customevents.event.SingleEventManager;
import com.lessnop.customevents.event.EventType;
import com.lessnop.customevents.event.EventTypeEnum;
import com.lessnop.customevents.utils.PrefixUtils;
import com.lessnop.customevents.utils.SortUtils;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager {

	private SQLQueryManager sqlQueryManager;

	private String host, port, database, url, username, password;

	private Connection connection;

	private HashMap<EventTypeEnum, HashMap<EventType, TimeData>> activeEvents = new HashMap<>();
	private LinkedHashMap<UUID, Integer> topPlayers = new LinkedHashMap<>();
	private LinkedHashMap<String, LocationData> locations = new LinkedHashMap<>();

	public DatabaseManager() {
		sqlQueryManager = new SQLQueryManager();
	}

	public void setCredentials(String host, String port, String database, String username, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.url = "jdbc:mysql://" + host + ":" + port;
		this.username = username;
		this.password = password;
	}

	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(this.url, this.username, this.password);
			sqlQueryManager.createAndUseDatabase(connection, database);
		} catch (SQLException e) {
			PrefixUtils.printMessage("Error while connecting to database!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			PrefixUtils.printMessage("Could not find java driver for DB connection!", PrefixUtils.MessageType.ERROR);
		}
	}

	public void init() {
		if (!isConnected()) return;
		try {
			sqlQueryManager.createTopPlayersTable(connection);
			sqlQueryManager.createLocationsTable(connection);
			for (EventTypeEnum eventTypeEnum : EventTypeEnum.values()) {
				sqlQueryManager.createEventsTable(connection, eventTypeEnum);

				SingleEventManager singleEventManager = eventTypeEnum.getEventManager();
				Set<String> set = singleEventManager.getEventTypeNames();
				for (String eventName : set) {
					int time = sqlQueryManager.getEventTime(connection, eventName, false, eventTypeEnum);
					if (time == -2)
						sqlQueryManager.initEventTime(connection, eventName, -1, false, eventTypeEnum);

					int maxTime = sqlQueryManager.getEventTime(connection, eventName, true, eventTypeEnum);
					if (maxTime == -2)
						sqlQueryManager.initEventTime(connection, eventName, -1, true, eventTypeEnum);

				}
			}
			sqlQueryManager.loadLocations(connection, locations);

		} catch (SQLException e) {
			PrefixUtils.printMessage("Error while creating and initializing tables in DB!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		}
	}

	public void closeConnections() {
		if (!isConnected()) return;
		try {
			connection.close();
		} catch (SQLException e) {
			PrefixUtils.printMessage("Error while closing connecting to database!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		}
	}

	private boolean isConnected() {
		try {
			return connection != null && !connection.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	public HashMap<EventType, TimeData> getActiveEventTypes(EventTypeEnum eventTypeEnum) {
		SingleEventManager singleEventManager = eventTypeEnum.getEventManager();
		HashMap<EventType, TimeData> map = new HashMap<>();
		if (!isConnected()) return map;
		try {
			HashMap<String, TimeData> events = sqlQueryManager.getActiveEvents(connection, eventTypeEnum);
			for (Map.Entry<String, TimeData> eventEntry : events.entrySet()) {
				EventType itemsEventType = singleEventManager.getEventTypeById(eventEntry.getKey());
				if (itemsEventType == null) continue;
				map.put(itemsEventType, eventEntry.getValue());
			}
		} catch (SQLException e) {
			PrefixUtils.printMessage("Error while getting data from database!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		}
		return map;
	}

	public void addTime(EventTypeEnum eventTypeEnum, String eventName, int time, boolean maxTime) {
		try {
			sqlQueryManager.addEventTime(connection, eventName, time, maxTime, eventTypeEnum);
		} catch (SQLException e) {
			PrefixUtils.printMessage("Error adding time to event " + eventName + " (type " + eventTypeEnum.getName() + ")!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		}
	}

	public void setTime(EventTypeEnum eventTypeEnum, String eventName, int time, boolean maxTime) {
		try {
			sqlQueryManager.setEventTime(connection, eventName, time, maxTime, eventTypeEnum);
		} catch (SQLException e) {
			PrefixUtils.printMessage("Error set time of event " + eventName + " (type " + eventTypeEnum.getName() + ")!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		}
	}

	public void countDownTime(EventTypeEnum eventTypeEnum) {
		try {
			sqlQueryManager.countDownTime(connection, eventTypeEnum);
		} catch (SQLException e) {
			PrefixUtils.printMessage("Error decreasing time of event of type " + eventTypeEnum.getName() + "!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		}
	}

	public void endEvent(EventTypeEnum eventTypeEnum, EventType eventType) {
		try {
			HashMap<EventType, TimeData> times = activeEvents.get(eventTypeEnum);
			if (times != null) {
				TimeData data = times.get(eventType);
				if (data != null) {
					data.setTime(-1);
					data.setMaxTime(-1);
				}
			}
			sqlQueryManager.setEventTime(connection, eventType.getEventId(), -1, false, eventTypeEnum);
			sqlQueryManager.setEventTime(connection, eventType.getEventId(), -1, true, eventTypeEnum);
		} catch (SQLException e) {
			PrefixUtils.printMessage("Error decreasing time of event of type " + eventTypeEnum.getName() + "!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		}
	}

	public HashMap<EventTypeEnum, HashMap<EventType, TimeData>> updateEvents() {
		activeEvents.clear();
		for (EventTypeEnum eventTypeEnum : EventTypeEnum.values()) {
			activeEvents.put(eventTypeEnum, getActiveEventTypes(eventTypeEnum));
		}
		return activeEvents;
	}

	public void addPlayerTimeToTop(UUID uuid, int time) {
		try {
			int newVal = sqlQueryManager.addPlayerTimeToTop(connection, uuid, time);
			topPlayers.put(uuid, newVal);
		} catch (SQLException e) {
			PrefixUtils.printMessage("Error while adding time to player's data to database - " + uuid + " (+" + time + "min)!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		}
	}

	public LinkedHashMap<UUID, Integer> updateTopPlayers() {
		topPlayers.clear();
		if (!isConnected()) return topPlayers;
		try {
			LinkedHashMap<UUID, Integer> map = sqlQueryManager.getTopPlayers(connection);
			SortUtils.sortAndPutInMap(map, topPlayers);

		} catch (SQLException e) {
			PrefixUtils.printMessage("Error while getting data from database!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		}
		return topPlayers;
	}

	public LinkedHashMap<UUID, Integer> getTopPlayers() {
		return topPlayers;
	}

	public void decreaseTime() {
		for (EventTypeEnum eventTypeEnum : EventTypeEnum.values()) {
			countDownTime(eventTypeEnum);
		}
	}

	public HashMap<EventTypeEnum, HashMap<EventType, TimeData>> getActiveEvents() {
		return activeEvents;
	}

	public LocationData getLocationData(String key) {
		return locations.get(key);
	}

	public void setLocation(String key, Location location) {
		LocationData locationData = LocationData.fromLocation(location);
		try {
			sqlQueryManager.setLocation(connection, key, locationData);
		} catch (SQLException e) {
			PrefixUtils.printMessage("Error while saving location to database!", PrefixUtils.MessageType.ERROR);
			e.printStackTrace();
		}
		locations.put(key, locationData);
	}

}
