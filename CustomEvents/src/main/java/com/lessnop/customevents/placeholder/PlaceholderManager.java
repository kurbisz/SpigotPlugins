package com.lessnop.customevents.placeholder;

import com.lessnop.customevents.database.TimeData;
import com.lessnop.customevents.event.EventType;
import com.lessnop.customevents.utils.StringUtils;
import org.bukkit.Bukkit;

import java.util.Map;

public class PlaceholderManager {

	private String actualEvent = "";

	private String generalFormat, defaultGeneral, timeFormat, zeroTime;

	private DefaultPlaceholderHook defaultPlaceholderHook;

	public void setGeneralFormatString(String placeholder) {
		generalFormat = placeholder;
	}

	public void setTimeFormatString(String placeholder) {
		timeFormat = placeholder;
	}

	public void init() {
		defaultPlaceholderHook = new DefaultPlaceholderHook();
		defaultPlaceholderHook.register();
	}

	public void unload() {
		if (defaultPlaceholderHook != null) defaultPlaceholderHook.unregister();
	}

	public void setActualEvent(Map.Entry<EventType, TimeData> eventEntry) {
		if (eventEntry == null) actualEvent = defaultGeneral;
		else {
			actualEvent = StringUtils.replaceVars(generalFormat,
					"%event%", eventEntry.getKey().getEventName(),
					"%time%", getFormattedTime(eventEntry.getValue().getTime()));
		}
	}

	public String getActualEvent() {
		return actualEvent;
	}

	public String getFormattedTime(int minutes) {
		int h = minutes / 60;
		int m = minutes % 60;
		String hour = (h < 10 ? "0" : "") + h;
		String min = (m < 10 ? "0" : "") + m;
		return StringUtils.replaceVars(timeFormat, "%h%", hour, "%m%", min);
	}

	public String getZeroTime() {
		return zeroTime;
	}

	public void setZeroTimeString(String placeholderZeroTime) {
		zeroTime = placeholderZeroTime;
	}

	public void setDefaultGeneralFormatString(String placeholderGeneral) {
		defaultGeneral = placeholderGeneral;
	}
}
