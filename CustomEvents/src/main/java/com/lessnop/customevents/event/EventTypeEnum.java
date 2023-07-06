package com.lessnop.customevents.event;

import com.lessnop.customevents.CustomEvents;

public enum EventTypeEnum {

	ITEMS_EVENT_TYPE {
		public String getName() {
			return "ITEMS";
		}

		public SingleEventManager getEventManager() {
			return CustomEvents.getInstance().getEventManager().getItemsEventManager();
		}

		public String getEventTimeTableName() {
			return "itemsEventsTime";
		}

		public String getEventMaxTimeTableName() {
			return "itemsEventsMaxTime";
		}
	},

	MOBS_EVENT_TYPE {
		public String getName() {
			return "MOBS";
		}

		public SingleEventManager getEventManager() {
			return CustomEvents.getInstance().getEventManager().getMobsEventManager();
		}

		public String getEventTimeTableName() {
			return "mobsEventsTime";
		}

		public String getEventMaxTimeTableName() {
			return "mobsEventsMaxTime";
		}
	};

	public String getName() {
		return "name";
	}

	public String getEventTimeTableName() {
		return "";
	}

	public String getEventMaxTimeTableName() {
		return "";
	}

	public SingleEventManager getEventManager() {
		return null;
	}

	public static EventTypeEnum getByName(String name) {
		if (name.equalsIgnoreCase("items")) return ITEMS_EVENT_TYPE;
		else return MOBS_EVENT_TYPE;
	}

}
