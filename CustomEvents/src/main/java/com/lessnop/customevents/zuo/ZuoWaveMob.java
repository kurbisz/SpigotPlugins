package com.lessnop.customevents.zuo;

public class ZuoWaveMob {

	private String name;
	private int minAm, maxAm, minLvl, maxLvl;

	public ZuoWaveMob(String name, int minAm, int maxAm, int minLvl, int maxLvl) {
		this.name = name;
		this.minAm = minAm;
		this.maxAm = maxAm;
		this.minLvl = minLvl;
		this.maxLvl = maxLvl;
	}

	public String getName() {
		return name;
	}

	public int getMaxAm() {
		return maxAm;
	}

	public int getMinAm() {
		return minAm;
	}

	public int getMinLvl() {
		return minLvl;
	}

	public int getMaxLvl() {
		return maxLvl;
	}
}
