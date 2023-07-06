package com.lessnop.customevents.data.config;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.utils.PrefixUtils;
import com.lessnop.customevents.zuo.SingleZuoEvent;
import com.lessnop.customevents.zuo.ZuoManager;
import com.lessnop.customevents.zuo.ZuoWave;
import com.lessnop.customevents.zuo.ZuoWaveMob;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ZUOConfigFile extends SimpleYamlFile {

	public ZUOConfigFile(String fileName) {
		super(fileName);
	}

	@Override
	public void load() {
		ZuoManager zuoManager = CustomEvents.getInstance().getZuoManager();
		ConfigurationSection eventsSection = config.getConfigurationSection("events");
		HashMap<String, SingleZuoEvent> events = new HashMap<>();
		for (String key : eventsSection.getKeys(false)) {
			ConfigurationSection zuoEventSection = eventsSection.getConfigurationSection(key);
			SingleZuoEvent singleZuoEvent = getSingleZuoEvent(zuoEventSection, key);
			if (singleZuoEvent == null) continue;
			events.put(key, singleZuoEvent);
		}
		zuoManager.setZuoEvents(events);
	}


	private SingleZuoEvent getSingleZuoEvent(ConfigurationSection section, String name) {
		BarColor barColor = BarColor.BLUE;
		try {
			barColor = BarColor.valueOf(section.getString("bossBarColor"));
		} catch (IllegalArgumentException | NullPointerException e) {}
		BarStyle barStyle = BarStyle.SOLID;
		try {
			barStyle = BarStyle.valueOf(section.getString("bossBarStyle"));
		} catch (IllegalArgumentException | NullPointerException e) {}
		String region = section.getString("region");

		int minY = section.getInt("minY");
		int maxY = section.getInt("maxY");

		ConfigurationSection wavesSection = section.getConfigurationSection("waves");
		int wavesAmount = wavesSection.getKeys(false).size();
		ZuoWave[] waves = new ZuoWave[wavesAmount];
		for (int i = 0; i < wavesAmount; i++) {
			ConfigurationSection waveSection = wavesSection.getConfigurationSection(i + "");
			if (waveSection == null) {
				Bukkit.getConsoleSender().sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
						"W evencie zuo " + name + " nie zostaly ustawione wszystkie fale (0-" + (wavesAmount - 1) + ")! Event zostanie pominiety!");
				return null;
			}
			waves[i] = getZuoWave(waveSection);
			if (waves[i].getMobs().size() == 0) {
				Bukkit.getConsoleSender().sendMessage(PrefixUtils.getPrefix(PrefixUtils.MessageType.ERROR) +
						"W evencie zuo " + name + " w fali " + i + " nie zostal dodany zaden mob! Event zostanie pominiety!");
				return null;
			}
		}
		return new SingleZuoEvent(name, region, minY, maxY, barColor, barStyle, waves);
	}

	private ZuoWave getZuoWave(ConfigurationSection section) {
		List<ZuoWaveMob> list = new ArrayList<>();
		for (String key : section.getKeys(false)) {
			ConfigurationSection zuoMobSection = section.getConfigurationSection(key);
			ZuoWaveMob zuoWaveMob = getZuoWaveMob(zuoMobSection);
			list.add(zuoWaveMob);
		}
		return new ZuoWave(list);
	}

	private ZuoWaveMob getZuoWaveMob(ConfigurationSection section) {
		String name = section.getString("name");
		int minAmount = section.getInt("minAmount");
		int maxAmount = section.getInt("maxAmount");
		int minLvl = section.getInt("minLvl");
		int maxLvl = section.getInt("maxLvl");
		return new ZuoWaveMob(name, minAmount, maxAmount, minLvl, maxLvl);
	}

}
