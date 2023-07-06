package com.lessnop.customevents.database;

import java.time.LocalDateTime;

public class TimeData {

	private int time, maxTime;
	private LocalDateTime refreshTime;

	public TimeData(int time, int maxTime) {
		this.time = time;
		this.maxTime = maxTime;
		refreshTime = LocalDateTime.now();
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public LocalDateTime getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(LocalDateTime refreshTime) {
		this.refreshTime = refreshTime;
	}
}
