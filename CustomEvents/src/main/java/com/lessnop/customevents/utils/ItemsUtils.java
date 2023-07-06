package com.lessnop.customevents.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemsUtils {

	public static void replaceVarsInItem(ItemStack itemStack, String... replaces) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta.hasDisplayName())
			itemMeta.setDisplayName(StringUtils.replaceVars(itemMeta.getDisplayName(), replaces));
		if (itemMeta.hasLore()) {
			List<String> newLore = new ArrayList<>();
			for (String str : itemMeta.getLore()) {
				newLore.add(StringUtils.replaceVars(str, replaces));
			}
			itemMeta.setLore(newLore);
		}
		itemStack.setItemMeta(itemMeta);
	}

	public static void setPlayerOnHead(ItemStack itemStack, String nick) {
		if (itemStack == null || !itemStack.getType().equals(Material.PLAYER_HEAD) || nick == null) return;
		ItemMeta im = itemStack.getItemMeta();
		if (im instanceof SkullMeta) {
			SkullMeta headMeta = (SkullMeta) im;
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(nick);
			if (offlinePlayer == null) return;
			headMeta.setOwningPlayer(offlinePlayer);
			itemStack.setItemMeta(headMeta);
		}
	}

}
