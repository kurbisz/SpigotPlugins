package com.lessnop.customevents.data;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageManager {

	public HashMap<String, String> messages = new HashMap<String, String>();
	public HashMap<String, List<String>> helps = new HashMap<String, List<String>>();

	public void setMessages(HashMap<String, String> messages) {
		this.messages = messages;
	}

	public void setHelps(HashMap<String, List<String>> helps) {
		this.helps = helps;
	}

	public String getMsg(String str) {
		String msg = messages.get(str);
		return msg != null ? msg : "";
	}

	public List<String> getCmdHelp(String str){
		List<String> helpCommands = helps.get(str);
		return helpCommands != null ? helpCommands : new ArrayList<>();
	}

}
