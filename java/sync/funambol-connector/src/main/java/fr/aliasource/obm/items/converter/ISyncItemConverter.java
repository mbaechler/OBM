package fr.aliasource.obm.items.converter;

import java.util.TimeZone;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.contact.Contact;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.source.SyncSource;

import fr.aliasource.funambol.ConvertionException;

public interface ISyncItemConverter {
	
	Calendar getFunambolCalendarFromSyncItem(SyncItem item, String sourceType, TimeZone deviceTimezone, String deviceCharset) throws ConvertionException;
	
	SyncItem getSyncItemFromFunambolCalendar(SyncSource syncSource, Calendar calendar, char status,  String sourceType, TimeZone deviceTimezone, String deviceCharset, boolean isEncoded) throws ConvertionException;
	
	Contact getFunambolContactFromSyncItem(SyncItem item, String sourceType, TimeZone deviceTimezone, String deviceCharset, boolean isEncoded) throws ConvertionException;
	
	SyncItem getSyncItemFromFunambolContact(SyncSource syncSource, Contact contact, char state, String sourceType, TimeZone deviceTimezone, String deviceCharset, boolean isEncoded) throws ConvertionException;
	
}
