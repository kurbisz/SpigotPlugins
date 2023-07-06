package com.lessnop.customevents.data.config;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.placeholder.PlaceholderManager;
import com.lessnop.customevents.utils.StringUtils;

import java.util.List;

public class MainConfigFile extends SimpleYamlFile {


	public MainConfigFile(String fileName) {
		super(fileName);
	}

	@Override
	public void load() {
		boolean isDebugActive = config.getBoolean("debug");
		CustomEvents.setDebugMode(isDebugActive);

		String actualServer = config.getString("actualServer");
		CustomEvents.setActualServer(actualServer);
		boolean isMainServer = config.getBoolean("mainServer");
		CustomEvents.setIsMainServer(isMainServer);
		List<String> serverList = config.getStringList("serverList");
		CustomEvents.setServerList(serverList);

		String host = config.getString("mysql.host");
		String port = config.getString("mysql.port");
		String database = config.getString("mysql.database");
		String username = config.getString("mysql.username");
		String password = config.getString("mysql.password");

		DatabaseManager databaseManager = CustomEvents.getInstance().getDatabaseManager();
		databaseManager.setCredentials(host, port, database, username, password);

		String placeholderGeneral = StringUtils.replaceColors(config.getString("placeholder.general"));
		PlaceholderManager placeholderManager = CustomEvents.getInstance().getPlaceholderManager();
		placeholderManager.setGeneralFormatString(placeholderGeneral);

		String placeholderDefaultGeneral = StringUtils.replaceColors(config.getString("placeholder.defaultGeneral"));
		placeholderManager.setDefaultGeneralFormatString(placeholderDefaultGeneral);

		String placeholderTime = StringUtils.replaceColors(config.getString("placeholder.time"));
		placeholderManager.setTimeFormatString(placeholderTime);

		String placeholderZeroTime = StringUtils.replaceColors(config.getString("placeholder.zeroTime"));
		placeholderManager.setZeroTimeString(placeholderZeroTime);

	}


}
