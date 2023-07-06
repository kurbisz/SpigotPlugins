package com.lessnop.customevents.data.config;

import com.lessnop.customevents.data.SimpleFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class SimpleYamlFile extends SimpleFile {

	protected FileConfiguration config;

	public SimpleYamlFile(String fileName) {
		super(fileName);
		this.config = YamlConfiguration.loadConfiguration(file);
	}
	
	public void save() {
		try {
			config.save(file);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	protected boolean containsPath(String str) {
		return config.contains(str);
	}

	public abstract void load();

}
