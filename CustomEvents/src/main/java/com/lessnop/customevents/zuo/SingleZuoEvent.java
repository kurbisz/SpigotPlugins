package com.lessnop.customevents.zuo;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class SingleZuoEvent {

	private String name;
	private String regionName;
	private int minY, maxY;
	private BarColor barColor;
	private BarStyle barStyle;
	private int maxWave;
	private ZuoWave[] waves;

	private ProtectedRegion region;

	public SingleZuoEvent(String name, String regionName, int minY, int maxY, BarColor barColor, BarStyle barStyle, ZuoWave[] waves) {
		this.name = name;
		this.regionName = regionName;
		this.minY = minY;
		this.maxY = maxY;
		this.barColor = barColor;
		this.barStyle = barStyle;
		this.waves = waves;
		this.maxWave = waves.length;

	}

	public String getName() {
		return name;
	}

	public String getRegionName() {
		return regionName;
	}

	public ProtectedRegion getRegion() {
		return region;
	}

	public BarColor getBarColor() {
		return barColor;
	}

	public BarStyle getBarStyle() {
		return barStyle;
	}

	public void setRegion(ProtectedRegion region) {
		this.region = region;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMinY() {
		return minY;
	}

	public ZuoWave getWave(int i) {
		return waves[i];
	}

	public ZuoWave[] getWaves() {
		return waves;
	}

	public int getMaxWave() {
		return maxWave;
	}
}
