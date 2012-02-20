package org.obm.funambol.model;

import java.util.Map;
import java.util.Set;

import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventObmId;

public class CalendarChanges {
	
	private Map<EventObmId, Event> updated;
	private Set<EventObmId> deleted;
	
	public CalendarChanges(Map<EventObmId, Event> updated,
			Set<EventObmId> deleted) {
		this.updated = updated;
		this.deleted = deleted;
	}

	public Map<EventObmId, Event> getUpdated() {
		return updated;
	}

	public Set<EventObmId> getDeleted() {
		return deleted;
	}
	
}
