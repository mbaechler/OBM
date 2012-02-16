package fr.aliasource.obm.items.converter;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.contact.Contact;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.source.SyncSource;

import fr.aliasource.funambol.ConvertionException;
import fr.aliasource.obm.items.manager.SyncSession;

public interface ISyncItemConverter {
	
	Calendar getFunambolCalendarFromSyncItem(SyncSession syncSession, SyncItem item, String sourceType) throws ConvertionException;
	
	SyncItem getSyncItemFromFunambolCalendar(SyncSession syncSession, SyncSource syncSource, Calendar calendar, char status,  String sourceType) throws ConvertionException;
	
	Contact getFunambolContactFromSyncItem(SyncSession syncSession, SyncItem item, String sourceType) throws ConvertionException;
	
	SyncItem getSyncItemFromFunambolContact(SyncSession syncSession, SyncSource syncSource, Contact contact, char state, String sourceType) throws ConvertionException;
	
}
