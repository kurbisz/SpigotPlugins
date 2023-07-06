package com.lessnop.customevents.database;

import com.lessnop.customevents.event.EventTypeEnum;

import java.sql.*;
import java.util.*;

public class SQLQueryManager {

	private final String topPlayersTableName = "topPlayers";
	private final String locationsTableName = "locations";

	private String getMessagesTableName(String serverName) {
		return "messages_" + serverName;
	}

	public void createEventsTable(Connection connection, EventTypeEnum eventTypeEnum) throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE IF NOT EXISTS " + eventTypeEnum.getEventTimeTableName() + " (name VARCHAR(255) PRIMARY KEY, time INT);");
		statement.execute("CREATE TABLE IF NOT EXISTS " + eventTypeEnum.getEventMaxTimeTableName() + " (name VARCHAR(255) PRIMARY KEY, time INT);");
	}

	public void createTopPlayersTable(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE IF NOT EXISTS " + topPlayersTableName + " (uuid VARCHAR(63) PRIMARY KEY, time INT);");
	}

	public void createLocationsTable(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE IF NOT EXISTS " + locationsTableName + " (name VARCHAR(255) PRIMARY KEY, "
				+ "world VARCHAR(255), x FLOAT, y FLOAT, z FLOAT, yaw FLOAT, pitch FLOAT);");
	}

	public void createMessagesTables(Connection connection, String serverName) throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute("CREATE TABLE IF NOT EXISTS " + getMessagesTableName(serverName) + " (message VARCHAR(255));");
	}

	public void createAndUseDatabase(Connection connection, String database) throws SQLException {
		Statement statement = connection.createStatement();
		statement.execute("CREATE DATABASE IF NOT EXISTS " + database + ";");
		statement.execute("USE " + database + ";");
	}

	public int getEventTime(Connection connection, String eventName, boolean maxTime, EventTypeEnum eventTypeEnum) throws SQLException {
		String table = maxTime ? eventTypeEnum.getEventMaxTimeTableName() : eventTypeEnum.getEventTimeTableName();
		PreparedStatement statement = connection.prepareStatement("SELECT time FROM " + table + " WHERE name=?;");
		statement.setString(1, eventName);
		ResultSet resultSet = statement.executeQuery();
		if (resultSet.next()) {
			return resultSet.getInt(1);
		}
		return -2;
	}

	public void initEventTime(Connection connection, String eventName, int time, boolean maxTime, EventTypeEnum eventTypeEnum) throws SQLException {
		String table = maxTime ? eventTypeEnum.getEventMaxTimeTableName() : eventTypeEnum.getEventTimeTableName();
		PreparedStatement statement = connection.prepareStatement("INSERT INTO " + table + " VALUES (?, ?);");
		statement.setString(1, eventName);
		statement.setInt(2, time);
		statement.executeUpdate();
	}

	public HashMap<String, TimeData> getActiveEvents(Connection connection, EventTypeEnum eventTypeEnum) throws SQLException {
		String table = eventTypeEnum.getEventTimeTableName();
		PreparedStatement statement = connection.prepareStatement("SELECT name, time FROM " + table + " WHERE time >= 0;");
		ResultSet resultSet = statement.executeQuery();
		HashMap<String, TimeData> result = new HashMap<>();
		while (resultSet.next()) {
			String name = resultSet.getString(1);
			int time = resultSet.getInt(2);
			result.put(name, new TimeData(time, -1));
		}

		String maxTable = eventTypeEnum.getEventMaxTimeTableName();
		PreparedStatement maxStatement = connection.prepareStatement("SELECT name, time FROM " + maxTable + ";");
		ResultSet maxResultSet = maxStatement.executeQuery();
		while (maxResultSet.next()) {
			String name = maxResultSet.getString(1);
			int maxTime = maxResultSet.getInt(2);
			TimeData timeData = result.get(name);
			if (timeData == null) continue;
			timeData.setMaxTime(maxTime);
		}

		return result;
	}

	public void countDownTime(Connection connection, EventTypeEnum eventTypeEnum) throws SQLException {
		String table = eventTypeEnum.getEventTimeTableName();
		Statement statement = connection.createStatement();
		statement.executeUpdate("UPDATE " + table + " SET time = time - 1 WHERE time > 0;");
	}

	public void setEventTime(Connection connection, String eventName, int time, boolean maxTime, EventTypeEnum eventTypeEnum) throws SQLException {
		String table = maxTime ? eventTypeEnum.getEventMaxTimeTableName() : eventTypeEnum.getEventTimeTableName();
		PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET time=? WHERE name=?;");
		statement.setInt(1, time);
		statement.setString(2, eventName);
		statement.executeUpdate();
	}

	public void addEventTime(Connection connection, String eventName, int time, boolean maxTime, EventTypeEnum eventTypeEnum) throws SQLException {
		String table = maxTime ? eventTypeEnum.getEventMaxTimeTableName() : eventTypeEnum.getEventTimeTableName();
		PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET time = time + ? WHERE name=?;");
		statement.setInt(1, time);
		statement.setString(2, eventName);
		statement.executeUpdate();
	}

	public int addPlayerTimeToTop(Connection connection, UUID uuid, int time) throws SQLException {
		PreparedStatement preStatement = connection.prepareStatement("SELECT time FROM " + topPlayersTableName + " WHERE uuid=?;");
		preStatement.setString(1, uuid.toString());
		ResultSet res = preStatement.executeQuery();
		if (res.next()) {
			int newVal = res.getInt(1) + time;
			PreparedStatement statement = connection.prepareStatement("UPDATE " + topPlayersTableName + " SET time=? WHERE uuid=?;");
			statement.setInt(1, newVal);
			statement.setString(2, uuid.toString());
			statement.executeUpdate();
			return newVal;
		}
		else {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO " + topPlayersTableName + " VALUES (?, ?);");
			statement.setString(1, uuid.toString());
			statement.setInt(2, time);
			statement.executeUpdate();
			return time;
		}
	}

	public LinkedHashMap<UUID, Integer> getTopPlayers(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT uuid, time FROM " + topPlayersTableName + ";");
		LinkedHashMap<UUID, Integer> res = new LinkedHashMap<>();
		while (resultSet.next()) {
			String uuid = resultSet.getString(1);
			int maxTime = resultSet.getInt(2);
			res.put(UUID.fromString(uuid), maxTime);
		}
		return res;
	}

	public void loadLocations(Connection connection, LinkedHashMap<String, LocationData> locations) throws SQLException {
		locations.clear();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT name, world, x, y, z, yaw, pitch FROM " + locationsTableName + ";");
		while (resultSet.next()) {
			String name = resultSet.getString(1);
			String world = resultSet.getString(2);
			float x = resultSet.getFloat(3);
			float y = resultSet.getFloat(4);
			float z = resultSet.getFloat(5);
			float yaw = resultSet.getFloat(6);
			float pitch = resultSet.getFloat(7);
			locations.put(name, new LocationData(world, x, y, z, yaw, pitch));
		}
	}

	public void setLocation(Connection connection, String key, LocationData locationData) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM " + locationsTableName + " WHERE name = ?;");
		preparedStatement.setString(1, key);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			PreparedStatement statement = connection.prepareStatement("UPDATE " + locationsTableName
					+ " SET world=?,x=?,y=?,z=?,yaw=?,pitch=? WHERE name=?;");
			statement.setString(1, locationData.getWorld());
			statement.setFloat(2, locationData.getX());
			statement.setFloat(3, locationData.getY());
			statement.setFloat(4, locationData.getZ());
			statement.setFloat(5, locationData.getYaw());
			statement.setFloat(6, locationData.getPitch());
			statement.setString(7, key);
			statement.executeUpdate();
		} else {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO " + locationsTableName
					+ " VALUES (?,?,?,?,?,?,?);");
			statement.setString(1, key);
			statement.setString(2, locationData.getWorld());
			statement.setFloat(3, locationData.getX());
			statement.setFloat(4, locationData.getY());
			statement.setFloat(5, locationData.getZ());
			statement.setFloat(6, locationData.getYaw());
			statement.setFloat(7, locationData.getPitch());
			statement.executeUpdate();
		}
	}

	public List<String> getMessages(Connection connection, String actualServer) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT message FROM " + getMessagesTableName(actualServer) + ";");
		List<String> messages = new ArrayList<>();
		while (resultSet.next()) {
			String msg = resultSet.getString(1);
			messages.add(msg);
		}
		return messages;
	}

	public void saveMessage(Connection connection, String serverName, String message) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + getMessagesTableName(serverName) + " VALUES (?);");
		preparedStatement.setString(1, message);
		preparedStatement.executeUpdate();
	}

	public void clearMessages(Connection connection, String actualServer) throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeUpdate("DELETE FROM " + getMessagesTableName(actualServer) + ";");
	}
}
