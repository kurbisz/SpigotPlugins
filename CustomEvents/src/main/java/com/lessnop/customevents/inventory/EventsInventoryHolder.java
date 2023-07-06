package com.lessnop.customevents.inventory;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.utils.ItemsUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.max;

public class EventsInventoryHolder implements InventoryHolder {


	private Inventory inventory;
	private int page = 0;

	public EventsInventoryHolder() {
		InventoryManager inventoryManager = CustomEvents.getInstance().getInventoryManager();
		inventory = Bukkit.createInventory(this, inventoryManager.getSlots(), inventoryManager.getTitle());
		int act = 0;
		LinkedHashMap<UUID, Integer> topPlayers = CustomEvents.getInstance().getDatabaseManager().getTopPlayers();
		for (Map.Entry<UUID, Integer> data : topPlayers.entrySet()) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(data.getKey());
			String playerName = player != null ? player.getName() : "???";
			inventory.setItem(act, getFormattedItem(inventoryManager, act + 1, playerName, data.getValue()));
			act++;
			if (act >= 36) break;
		}
		for (int i = 36; i < 45; i++)
			inventory.setItem(i, inventoryManager.getEmpty());
		int max = getMaxValue(topPlayers);
		if (maxToPages(max) != 0) {
			inventory.setItem(inventoryManager.getNextSlot(), inventoryManager.getNextPage());
		}
		inventory.setItem(inventoryManager.getInfoSlot(), inventoryManager.getInfo());
	}


	@Override
	public @NotNull Inventory getInventory() {
		return inventory;
	}

	private ItemStack getFormattedItem(InventoryManager inventoryManager, int place, String playerName, int am) {
		ItemStack item = inventoryManager.getHeadTemplate().clone();
		ItemsUtils.replaceVarsInItem(item, "%player%", playerName, "%nr%", place + "", "%am%", am + "");
		ItemsUtils.setPlayerOnHead(item, playerName);
		return item;
	}

	public void nextPage() {
		int max = getMaxValue();
		if(maxToPages(max) != page) page++;
		refreshPage();
	}

	public void prevPage() {
		if (page > 0) {
			page--;
		}
		refreshPage();
	}

	private void refreshPage() {
		InventoryManager inventoryManager = CustomEvents.getInstance().getInventoryManager();
		refreshHeads(inventoryManager);
		int max = getMaxValue();
		if (maxToPages(max) == page) inventory.setItem(inventoryManager.getNextSlot(), null);
		else inventory.setItem(inventoryManager.getNextSlot(), inventoryManager.getNextPage());

		if (page == 0) inventory.setItem(inventoryManager.getPrevSlot(), null);
		else inventory.setItem(inventoryManager.getPrevSlot(), inventoryManager.getPreviousPage());
	}

	private void refreshHeads(InventoryManager inventoryManager) {
		int act = 0;
		LinkedHashMap<UUID, Integer> topPlayers = CustomEvents.getInstance().getDatabaseManager().getTopPlayers();
		for (Map.Entry<UUID, Integer> data : topPlayers.entrySet()) {
			if (act < 36 * page) {
				act++;
				continue;
			}
			OfflinePlayer player = Bukkit.getOfflinePlayer(data.getKey());
			String playerName = player != null ? player.getName() : "???";
			inventory.setItem(act % 36, getFormattedItem(inventoryManager, act + 1, playerName, data.getValue()));
			act++;
			if (act % 36 == 0) return;
		}
		for (int i = act % 36; i < 36; i++) {
			inventory.setItem(i, null);
		}
	}

	private int getMaxValue() {
		LinkedHashMap<UUID, Integer> topPlayers = CustomEvents.getInstance().getDatabaseManager().getTopPlayers();
		return getMaxValue(topPlayers);
	}

	private int getMaxValue(LinkedHashMap<UUID, Integer> topPlayers) {
		return topPlayers.size();
	}

	private int maxToPages(int am) {
		return max(0, (am - 1)) / 36;
	}

}
