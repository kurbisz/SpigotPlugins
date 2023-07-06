package com.lessnop.customevents;

import com.lessnop.customevents.command.CommandManager;
import com.lessnop.customevents.data.ConfigFilesManager;
import com.lessnop.customevents.data.MessageManager;
import com.lessnop.customevents.database.DatabaseManager;
import com.lessnop.customevents.event.EventManager;
import com.lessnop.customevents.inventory.InventoryManager;
import com.lessnop.customevents.ox.OXManager;
import com.lessnop.customevents.placeholder.PlaceholderManager;
import com.lessnop.customevents.scheduler.SchedulerManager;
import com.lessnop.customevents.zuo.ZuoManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class CustomEvents extends JavaPlugin {

	private static CustomEvents inst;
	private static boolean debugMode;

	private static String actualServer;
	private static List<String> serverList;
	private static boolean isMainServer;

	private MessageManager messageManager;
	private DatabaseManager databaseManager;

	private PlaceholderManager placeholderManager;
	private EventManager eventManager;
	private OXManager oxManager;
	private ZuoManager zuoManager;
	private ConfigFilesManager configFilesManager;
	private CommandManager commandManager;
	private SchedulerManager schedulerManager;
	private InventoryManager inventoryManager;

	public static CustomEvents getInstance() {
		return inst;
	}

	public static boolean isDebugMode() {
		return debugMode;
	}

	public static void setDebugMode(boolean debugMode) {
		CustomEvents.debugMode = debugMode;
	}

	public static String getActualServer() {
		return actualServer;
	}

	public static void setActualServer(String actualServer) {
		CustomEvents.actualServer = actualServer;
	}

	public static List<String> getServerList() {
		return serverList;
	}

	public static void setServerList(List<String> serverList) {
		CustomEvents.serverList = serverList;
	}

	public static boolean isIsMainServer() {
		return isMainServer;
	}

	public static void setIsMainServer(boolean isMainServer) {
		CustomEvents.isMainServer = isMainServer;
	}

	@Override
	public void onEnable() {
		inst = this;
		inventoryManager = new InventoryManager();
		messageManager = new MessageManager();
		databaseManager = new DatabaseManager();
		placeholderManager = new PlaceholderManager();
		eventManager = new EventManager();
		oxManager = new OXManager();
		zuoManager = new ZuoManager();
		configFilesManager = new ConfigFilesManager();
		commandManager = new CommandManager();
		schedulerManager = new SchedulerManager();

		eventManager.init();

		databaseManager.connect();
		databaseManager.init();

		placeholderManager.init();

		schedulerManager.init();

		commandManager.registerCommands();

	}

	@Override
	public void onDisable() {
		oxManager.cancel(false);
		zuoManager.cancel(false);
		placeholderManager.unload();
		databaseManager.closeConnections();
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public MessageManager getMessageManager() {
		return messageManager;
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public PlaceholderManager getPlaceholderManager() {
		return placeholderManager;
	}

	public SchedulerManager getSchedulerManager() {
		return schedulerManager;
	}

	public OXManager getOxManager() {
		return oxManager;
	}

	public ZuoManager getZuoManager() {
		return zuoManager;
	}
}
