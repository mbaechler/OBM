package org.obm.funambol.converter;

import java.util.Collection;
import java.util.List;

import org.obm.sync.book.ContactKey;
import org.obm.sync.calendar.EventObmId;

import com.funambol.framework.engine.SyncItemKey;

public interface ISyncItemKeyConverter {

	EventObmId getEventObmIdFromSyncItemKey(SyncItemKey syncItemKey);

	ContactKey getContactKeyFromSyncItemKey(SyncItemKey syncItemKey);

	List<SyncItemKey> getSyncItemKeysFromContactKeys(Collection<ContactKey> keys);

	List<SyncItemKey> getSyncItemKeysFromEventObmIds(Collection<EventObmId> keys);

}
