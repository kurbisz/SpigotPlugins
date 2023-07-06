package com.lessnop.customevents.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class PrefixUtils {

	private static String cmdPrefix = ChatColor.GRAY + "> " + ChatColor.GOLD;
	public static String pluginName = "CustomEvents";

	public static String getPrefix(MessageType messageType) {
		return messageType.getColor() + "[" + pluginName + "] ";
	}

	public static String getCommandPrefix() {
		return cmdPrefix;
	}

	public static void printMessage(String msg, MessageType messageType) {
		Bukkit.getConsoleSender().sendMessage(getPrefix(messageType) + msg);
	}

	public enum MessageType {
		INFO {
			public ChatColor getColor() {
				return ChatColor.GRAY;
			}
		}, WARNING {
			public ChatColor getColor() {
				return ChatColor.YELLOW;
			}
		}, ERROR {
			public ChatColor getColor() {
				return ChatColor.RED;
			}
		};

		public ChatColor getColor() {
			return ChatColor.RESET;
		}

	}

}
