package org.obm.funambol.converter;

import org.obm.funambol.exception.ConvertionException;
import org.obm.sync.calendar.Event;


public interface IEventConverter {

	public com.funambol.common.pim.calendar.Calendar obmEventToFoundationCalendar(
			Event obmEvent) throws ConvertionException;
	
	Event foundationCalendarToObmEvent(
			com.funambol.common.pim.calendar.Calendar calendar, String userEmail) throws ConvertionException;
}
