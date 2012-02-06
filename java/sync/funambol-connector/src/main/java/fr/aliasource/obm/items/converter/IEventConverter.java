package fr.aliasource.obm.items.converter;

import org.obm.sync.calendar.Event;

import fr.aliasource.funambol.ConvertionException;

public interface IEventConverter {

	public com.funambol.common.pim.calendar.Calendar obmEventToFoundationCalendar(
			Event obmEvent) throws ConvertionException;
	
	Event foundationCalendarToObmEvent(
			com.funambol.common.pim.calendar.Calendar calendar, String userEmail);
}
