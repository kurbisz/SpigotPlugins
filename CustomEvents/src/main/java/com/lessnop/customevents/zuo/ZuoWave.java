package com.lessnop.customevents.zuo;

import java.util.List;

public class ZuoWave {

	private List<ZuoWaveMob> mobs;

	public ZuoWave(List<ZuoWaveMob> mobs) {
		this.mobs = mobs;
	}

	public List<ZuoWaveMob> getMobs() {
		return mobs;
	}
}
