package org.obm.push;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.obm.push.backend.DataDelta;
import org.obm.push.backend.IContentsExporter;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.FilterType;
import org.obm.push.bean.ItemChange;
import org.obm.push.bean.MSAttachementData;
import org.obm.push.bean.PIMDataType;
import org.obm.push.bean.SyncState;
import org.obm.push.calendar.CalendarBackend;
import org.obm.push.contacts.ContactsBackend;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.UnknownObmSyncServerException;
import org.obm.push.exception.activesync.AttachementNotFoundException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.exception.activesync.ProcessingEmailException;
import org.obm.push.mail.MailBackend;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ContentsExporter implements IContentsExporter {

	private final MailBackend mailBackend;
	private final CalendarBackend calBackend;
	private final ContactsBackend contactsBackend;

	@Inject
	private ContentsExporter(MailBackend mailBackend,
			CalendarBackend calendarExporter, ContactsBackend contactsBackend) {
		
		this.mailBackend = mailBackend;
		this.calBackend = calendarExporter;
		this.contactsBackend = contactsBackend;
	}

	private DataDelta getContactsChanges(BackendSession bs, SyncState state, Integer collectionId) throws UnknownObmSyncServerException {
		return contactsBackend.getContentChanges(bs, state, collectionId);
	}

	private DataDelta getTasksChanges(BackendSession bs, SyncState state, Integer collectionId, FilterType filterType) 
			throws CollectionNotFoundException, DaoException, UnknownObmSyncServerException  {
		return this.calBackend.getContentChanges(bs, state, collectionId, filterType);
	}

	private DataDelta getCalendarChanges(BackendSession bs, SyncState state, Integer collectionId, FilterType filterType) 
			throws CollectionNotFoundException, DaoException, UnknownObmSyncServerException {
		return calBackend.getContentChanges(bs, state, collectionId, filterType);
	}

	private int getItemEstimateSize(BackendSession bs, SyncState syncState, FilterType filterType, Integer collectionId)
			throws DaoException, CollectionNotFoundException, UnknownObmSyncServerException, ProcessingEmailException {
		
		DataDelta dataDelta = getChanged(bs, syncState, filterType, collectionId);
		return dataDelta.getItemEstimateSize();
	}
	
	private int getItemEmailEstimateSize(BackendSession bs, SyncState syncState, FilterType filterType, Integer collectionId) 
			throws CollectionNotFoundException, ProcessingEmailException {
		DataDelta changes = mailBackend.getMailChanges(bs, syncState, collectionId, filterType);
		return changes.getItemEstimateSize();
	}

	@Override
	public DataDelta getChanged(BackendSession bs, SyncState state, FilterType filterType, Integer collectionId) 
			throws DaoException, CollectionNotFoundException, UnknownObmSyncServerException, ProcessingEmailException {
		
		switch (state.getDataType()) {
		case CALENDAR:
			return getCalendarChanges(bs, state, collectionId, filterType);
		case CONTACTS:
			return getContactsChanges(bs, state, collectionId);
		case EMAIL:
			return mailBackend.getAndUpdateEmailChanges(bs, state, collectionId, filterType);
		case TASKS:
			return getTasksChanges(bs, state, collectionId, filterType);
		case FOLDER:
			return null;
		}
		return null;
	}
	
	@Override
	public List<ItemChange> fetch(BackendSession bs, PIMDataType getDataType, List<String> fetchServerIds) 
			throws CollectionNotFoundException, DaoException, ProcessingEmailException {
		
		LinkedList<ItemChange> changes = new LinkedList<ItemChange>();
		switch (getDataType) {
		case CONTACTS:
			changes.addAll(contactsBackend.fetchItems(bs, fetchServerIds));
			break;
		case EMAIL:
			changes.addAll(mailBackend.fetchItems(bs, fetchServerIds));
			break;
		case CALENDAR:
		case TASKS:
			changes.addAll(calBackend.fetchItems(bs, fetchServerIds));
			break;
		case FOLDER:
			break;
		}
		return changes;
	}

	@Override
	public MSAttachementData getEmailAttachement(BackendSession bs, String attachmentId) 
			throws AttachementNotFoundException, CollectionNotFoundException, DaoException, ProcessingEmailException {
		return mailBackend.getAttachment(bs, attachmentId);
	}

	@Override
	public List<ItemChange> fetchEmails(BackendSession bs, Integer collectionId, Collection<Long> uids) 
			throws DaoException, CollectionNotFoundException, ProcessingEmailException {
		return mailBackend.fetchItems(bs, collectionId, uids);
	}

	@Override
	public int getItemEstimateSize(BackendSession bs, FilterType filterType, Integer collectionId, SyncState state) throws CollectionNotFoundException, ProcessingEmailException, DaoException, UnknownObmSyncServerException {
		if (state.getDataType() != null) {
			switch (state.getDataType()) {
			case CALENDAR:
				return getItemEstimateSize(bs, state, filterType, collectionId);
			case CONTACTS:
				return getItemEstimateSize(bs, state, filterType, collectionId);
			case EMAIL:
				return getItemEmailEstimateSize(bs, state, filterType, collectionId);
			case FOLDER:
				return getItemEstimateSize(bs, state, filterType, collectionId);
			case TASKS:
				return getItemEstimateSize(bs, state, filterType, collectionId);
			}
		}
		return 0;
	}
	
}
