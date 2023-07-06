package com.lessnop.customevents.data.config;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.scheduler.SchedulerManager;
import com.lessnop.customevents.utils.StringUtils;

public class SchedulersConfigFile extends SimpleYamlFile {

	public SchedulersConfigFile(String fileName) {
		super(fileName);
	}

	@Override
	public void load() {
		int placeholderRefreshTime = config.getInt("placeholderRefresh");
		SchedulerManager.setPlaceholderRefreshTime(placeholderRefreshTime);
	}

}
