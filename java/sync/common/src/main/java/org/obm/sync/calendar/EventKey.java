package org.obm.sync.calendar;

import com.google.common.base.Objects;

public class EventKey {
	
	private EventObmId eventObmId;
	private EventExtId eventExtId;

	public EventKey(EventObmId eventObmId, EventExtId eventExtId) {
		this.eventObmId = eventObmId;
		this.eventExtId = eventExtId;
	}
	
	public EventObmId getEventObmId() {
		return eventObmId;
	}

	public EventExtId getEventExtId() {
		return eventExtId;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(eventObmId, eventExtId);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof EventKey) {
			EventKey that = (EventKey) object;
			
			return Objects.equal(this.eventObmId, that.eventObmId) && 
					Objects.equal(this.eventExtId, that.eventExtId) ;
		}
		return false;
	}

	@Override
	public String toString() {
		return "EventKey [eventObmId=" + eventObmId + ", eventExtId="
				+ eventExtId + "]";
	}
	
	
	
}
