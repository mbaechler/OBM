package org.obm.funambol.converter;

import org.obm.funambol.exception.ConvertionException;
import org.obm.funambol.model.SyncSession;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.contact.Contact;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.source.SyncSource;


public interface ISyncItemConverter {
	
	Calendar getFunambolCalendarFromSyncItem(SyncSession syncSession, SyncItem item, String sourceType) throws ConvertionException;
	
	SyncItem getSyncItemFromFunambolCalendar(SyncSession syncSession, SyncSource syncSource, Calendar calendar, char status,  String sourceType) throws ConvertionException;
	
	Contact getFunambolContactFromSyncItem(SyncSession syncSession, SyncItem item, String sourceType) throws ConvertionException;
	
	SyncItem getSyncItemFromFunambolContact(SyncSession syncSession, SyncSource syncSource, Contact contact, char state, String sourceType) throws ConvertionException;
	
}
