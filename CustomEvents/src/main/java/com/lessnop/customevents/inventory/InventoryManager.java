package com.lessnop.customevents.inventory;

import org.bukkit.inventory.ItemStack;

public class InventoryManager {

	private int slots = 54, prevSlot = 45, nextSlot = 53, infoSlot = 49;

	private String title;
	private ItemStack headTemplate, previousPage, nextPage, info, empty;

	public int getSlots() {
		return slots;
	}

	public int getPrevSlot() {
		return prevSlot;
	}

	public int getNextSlot() {
		return nextSlot;
	}

	public int getInfoSlot() {
		return infoSlot;
	}

	public String getTitle() {
		return title;
	}

	public ItemStack getHeadTemplate() {
		return headTemplate;
	}

	public ItemStack getPreviousPage() {
		return previousPage;
	}

	public ItemStack getNextPage() {
		return nextPage;
	}

	public ItemStack getInfo() {
		return info;
	}

	public ItemStack getEmpty() {
		return empty;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setItems(ItemStack headTemplate, ItemStack prev, ItemStack next, ItemStack info, ItemStack empty) {
		this.headTemplate = headTemplate;
		this.previousPage = prev;
		this.nextPage = next;
		this.info = info;
		this.empty = empty;
	}

}
