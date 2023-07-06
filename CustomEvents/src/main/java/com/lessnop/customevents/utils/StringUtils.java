package com.lessnop.customevents.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static String replaceColors(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public static String replaceAllColors(String message) {
		Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
		Matcher matcher = pattern.matcher(message);

		while (matcher.find()) {
			String color = message.substring(matcher.start(), matcher.end());
			message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
			matcher = pattern.matcher(message);
		}
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static String replaceVars(String msg, String... replaces) {
		int len = replaces.length;
		for (int i = 0; i < len / 2 - len % 2; i++) {
			if (replaces[i * 2 + 1] == null) continue;
			msg = msg.replaceAll(replaces[i * 2], replaces[i * 2 + 1]);
		}
		return msg;
	}

	public static boolean stringToBool(String str) {
		return str.startsWith("t") || str.startsWith("y");
	}

}
