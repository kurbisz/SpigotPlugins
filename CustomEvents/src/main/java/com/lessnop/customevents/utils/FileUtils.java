package com.lessnop.customevents.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUtils {

	public static ItemStack getItemFromFileConfiguration(ConfigurationSection fc, String path) {
		int amount = 1;
		if(fc.contains(path + ".am")) amount = fc.getInt(path + ".am");
		ItemStack is = new ItemStack(Material.valueOf(fc.getString(path + ".type")), amount);
		ItemMeta im = is.getItemMeta();
		if(fc.contains(path + ".name")) im.setDisplayName(StringUtils.replaceAllColors(fc.getString(path + ".name")));
		if(fc.contains(path + ".lore")) {
			List<String> l = new ArrayList<String>();
			for(String str : fc.getStringList(path + ".lore")) {
				l.add(StringUtils.replaceAllColors(str));
			}
			im.setLore(l);
		}
		if(im instanceof SkullMeta && fc.contains(path + ".head")) {
			String id = fc.getString(path + ".head");
			SkullMeta headMeta = (SkullMeta) im;
			GameProfile profile = new GameProfile(UUID.randomUUID(), null);

			profile.getProperties().put("textures", new Property("textures", id));

			try {
				Field profileField = headMeta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(headMeta, profile);
			} catch (IllegalArgumentException|NoSuchFieldException|SecurityException|IllegalAccessException error) {
				error.printStackTrace();
			}
			is.setItemMeta(headMeta);

		}
		if(im instanceof LeatherArmorMeta) {
			LeatherArmorMeta lam = (LeatherArmorMeta) im;
			int r = 0, g = 0, b = 0;
			if(fc.contains(path + ".red")) r = fc.getInt(path + ".red");
			if(fc.contains(path + ".green")) g = fc.getInt(path + ".green");
			if(fc.contains(path + ".blue")) b = fc.getInt(path + ".blue");
			lam.setColor(Color.fromRGB(r, g, b));
			is.setItemMeta(lam);
		}
		else is.setItemMeta(im);
		return is;
	}

}
