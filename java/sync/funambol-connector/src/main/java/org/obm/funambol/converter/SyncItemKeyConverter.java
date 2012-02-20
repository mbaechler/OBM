package org.obm.funambol.converter;

import java.util.Collection;
import java.util.List;

import org.obm.sync.book.ContactKey;
import org.obm.sync.calendar.EventObmId;

import com.funambol.framework.engine.SyncItemKey;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SyncItemKeyConverter implements ISyncItemKeyConverter {

	@Override
	public List<SyncItemKey> getSyncItemKeysFromContactKeys(
			Collection<ContactKey> keys) {
		final List<ContactKey> listKey = ImmutableList.<ContactKey>copyOf(keys);
		return Lists.transform(listKey, new Function<ContactKey, SyncItemKey>() {
			@Override
			public SyncItemKey apply(ContactKey input) {
				final String key = input.serialiseAsFunambolKey();
				return new SyncItemKey(key);
			}
		});
	}
	
	@Override
	public List<SyncItemKey> getSyncItemKeysFromEventObmIds(Collection<EventObmId> keys) {
		final List<EventObmId> listKey = ImmutableList.<EventObmId>copyOf(keys);
		return Lists.transform(listKey, new Function<EventObmId, SyncItemKey>() {
			@Override
			public SyncItemKey apply(EventObmId input) {
				final String key = input.serializeToString();
				return new SyncItemKey(key);
			}
		});
	}
	
	@Override
	public EventObmId getEventObmIdFromSyncItemKey(SyncItemKey syncItemKey) {
		final String itemKey = getCheckedSyncItemKeyAsString(syncItemKey);
		return new EventObmId(itemKey);
	}
	
	@Override
	public ContactKey getContactKeyFromSyncItemKey(SyncItemKey syncItemKey) {
		final String itemKey = getCheckedSyncItemKeyAsString(syncItemKey);
		return new ContactKey(itemKey);
	}
	
	private String getCheckedSyncItemKeyAsString(SyncItemKey syncItemKey) {
		Preconditions.checkNotNull(syncItemKey);
		Preconditions.checkNotNull(syncItemKey.getKeyValue());
		return syncItemKey.getKeyAsString();
	}

}
