package com.lessnop.customevents.data;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.data.config.*;

public class ConfigFilesManager {

	private InfoFile infoFile;
	private MainConfigFile mainConfigFile;
	private MessagesConfigFile messagesConfigFile;
	private ItemsConfigFile itemsConfigFile;
	private MobsConfigFile mobsConfigFile;
	private OXConfigFile oxConfigFile;
	private ZUOConfigFile zuoConfigFile;
	private InventoryConfigFile inventoryConfigFile;
	private SchedulersConfigFile schedulersConfigFile;

	public ConfigFilesManager() {
		CustomEvents main = CustomEvents.getInstance();
		if (!main.getDataFolder().exists()) {
			main.getDataFolder().mkdirs();
		}

		infoFile = new InfoFile("info.md");

		mainConfigFile = new MainConfigFile("config.yml");
		mainConfigFile.load();

		messagesConfigFile = new MessagesConfigFile("messages.yml");
		messagesConfigFile.load();

		itemsConfigFile = new ItemsConfigFile("items-config.yml");
		itemsConfigFile.load();

		mobsConfigFile = new MobsConfigFile("mobs-config.yml");
		mobsConfigFile.load();

		oxConfigFile = new OXConfigFile("ox-config.yml");
		oxConfigFile.load();

		zuoConfigFile = new ZUOConfigFile("zuo-config.yml");
		zuoConfigFile.load();

		inventoryConfigFile = new InventoryConfigFile("inventory.yml");
		inventoryConfigFile.load();

		schedulersConfigFile = new SchedulersConfigFile("schedulers.yml");
		schedulersConfigFile.load();
	}




}
