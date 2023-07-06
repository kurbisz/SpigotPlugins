package com.lessnop.customevents.data.config;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.event.items.ItemsEventManager;
import com.lessnop.customevents.event.items.ItemsEventType;
import com.lessnop.customevents.event.mobs.MobsEventManager;
import com.lessnop.customevents.event.mobs.MobsEventType;
import com.lessnop.customevents.utils.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class MobsConfigFile extends SimpleYamlFile {

	public MobsConfigFile(String fileName) {
		super(fileName);
	}

	@Override
	public void load() {
		MobsEventManager itemsEventManager = CustomEvents.getInstance().getEventManager().getMobsEventManager();

		ConfigurationSection rootSection = config.getConfigurationSection("");
		for (String eventKey : rootSection.getKeys(false)) {
			ConfigurationSection eventSection = rootSection.getConfigurationSection(eventKey);
			String eventName = eventSection.getString("name");
			List<String> mythicMobNames = eventSection.getStringList("mobNames");
			List<String> spawnCommands = eventSection.getStringList("spawnCmds");
			double dropChance = eventSection.getDouble("spawnChance");
			MobsEventType mobsEventType = new MobsEventType(eventKey, StringUtils.replaceColors(eventName), dropChance, mythicMobNames, spawnCommands);
			itemsEventManager.addEventType(eventKey, mobsEventType);
		}
	}


}
