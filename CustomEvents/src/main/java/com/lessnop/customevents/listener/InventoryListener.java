package com.lessnop.customevents.listener;

import com.lessnop.customevents.CustomEvents;
import com.lessnop.customevents.inventory.EventsInventoryHolder;
import com.lessnop.customevents.inventory.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof EventsInventoryHolder) {
			e.setCancelled(true);
			EventsInventoryHolder holder = (EventsInventoryHolder) e.getClickedInventory().getHolder();
			InventoryManager inventoryManager = CustomEvents.getInstance().getInventoryManager();
			if (e.getSlot() == inventoryManager.getPrevSlot()) {
				holder.prevPage();
			}
			else if (e.getSlot() == inventoryManager.getNextSlot()) {
				holder.nextPage();
			}
		}
	}

}
