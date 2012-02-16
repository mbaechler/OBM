package org.obm.funambol.model;

import java.util.List;
import java.util.Map;

import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventExtId;

public class CalendarChanges {
	
	private Map<EventExtId, Event> updated;
	private List<EventExtId> deleted;
	
	public CalendarChanges(Map<EventExtId, Event> updated,
			List<EventExtId> deleted) {
		this.updated = updated;
		this.deleted = deleted;
	}

	public Map<EventExtId, Event> getUpdated() {
		return updated;
	}

	public List<EventExtId> getDeleted() {
		return deleted;
	}
	
}
