package com.lessnop.customevents.data.config;


import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.data.MessageManager;
import com.lessnop.customevents.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessagesConfigFile extends SimpleYamlFile {


	
	public MessagesConfigFile(String fileName) {
		super(fileName);
	}
	
	public void load() {
		HashMap<String, String> messages = new HashMap<String, String>();
		HashMap<String, List<String>> helps = new HashMap<String, List<String>>();
		if(config.contains("messages")) {
			for(String str : config.getConfigurationSection("messages").getKeys(true)) {
				messages.put(str, StringUtils.replaceAllColors(config.getString("messages." + str)));
			}
		}
		if(config.contains("cmdHelps")) {
			for(String str : config.getConfigurationSection("cmdHelps").getKeys(true)) {
				List<String> l = new ArrayList<String>();
				for(String msg : config.getStringList("cmdHelps." + str)) {
					l.add(StringUtils.replaceAllColors(msg));
				}
				helps.put(str, l);
			}
		}

		MessageManager messageManager = CustomEvents.getInstance().getMessageManager();
		messageManager.setMessages(messages);
		messageManager.setHelps(helps);

	}


	


	
}
