package com.lessnop.customevents.data.config;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.inventory.EventsInventoryHolder;
import com.lessnop.customevents.inventory.InventoryManager;
import com.lessnop.customevents.utils.FileUtils;
import com.lessnop.customevents.utils.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class InventoryConfigFile extends SimpleYamlFile {

	public InventoryConfigFile(String fileName) {
		super(fileName);
	}

	@Override
	public void load() {
		String title = StringUtils.replaceColors(config.getString("title"));

		ConfigurationSection itemsConfiguration = config.getConfigurationSection("items");
		ItemStack headTemplate = FileUtils.getItemFromFileConfiguration(itemsConfiguration, "head");
		ItemStack prev = FileUtils.getItemFromFileConfiguration(itemsConfiguration, "prev");
		ItemStack next = FileUtils.getItemFromFileConfiguration(itemsConfiguration, "next");
		ItemStack info = FileUtils.getItemFromFileConfiguration(itemsConfiguration, "info");
		ItemStack empty = FileUtils.getItemFromFileConfiguration(itemsConfiguration, "empty");

		InventoryManager inventoryManager = CustomEvents.getInstance().getInventoryManager();
		inventoryManager.setItems(headTemplate, prev, next, info, empty);
		inventoryManager.setTitle(title);
	}

}
