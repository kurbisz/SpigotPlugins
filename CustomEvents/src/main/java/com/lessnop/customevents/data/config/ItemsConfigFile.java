package com.lessnop.customevents.data.config;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.event.items.ItemsEventManager;
import com.lessnop.customevents.event.items.ItemsEventType;
import com.lessnop.customevents.utils.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ItemsConfigFile extends SimpleYamlFile {

	public ItemsConfigFile(String fileName) {
		super(fileName);
	}

	@Override
	public void load() {
		ItemsEventManager itemsEventManager = CustomEvents.getInstance().getEventManager().getItemsEventManager();

		ConfigurationSection rootSection = config.getConfigurationSection("");
		for (String eventKey : rootSection.getKeys(false)) {
			ConfigurationSection eventSection = rootSection.getConfigurationSection(eventKey);
			String eventName = eventSection.getString("name");
			int maxDifference = eventSection.getInt("maxLvlDiff");
			List<String> mythicMobNames = eventSection.getStringList("mobNames");
			List<String> rewardCommands = eventSection.getStringList("rewardCmds");
			double dropChance = eventSection.getDouble("dropChance");
			ItemsEventType itemsEventType = new ItemsEventType(eventKey, StringUtils.replaceColors(eventName), maxDifference, dropChance, mythicMobNames, rewardCommands);
			itemsEventManager.addEventType(eventKey, itemsEventType);
		}
	}


}
