package org.obm.funambol.model;

import java.util.Map;
import java.util.Set;

import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventExtId;

public class CalendarChanges {
	
	private Map<EventExtId, Event> updated;
	private Set<EventExtId> deleted;
	
	public CalendarChanges(Map<EventExtId, Event> updated,
			Set<EventExtId> deleted) {
		this.updated = updated;
		this.deleted = deleted;
	}

	public Map<EventExtId, Event> getUpdated() {
		return updated;
	}

	public Set<EventExtId> getDeleted() {
		return deleted;
	}
	
}
