package org.obm.push.backend;

import org.obm.push.bean.BackendSession;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.impl.ObmSyncBackend;
import org.obm.push.store.CollectionDao;
import org.obm.sync.client.CalendarType;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.services.IAddressBook;
import org.obm.sync.services.ICalendar;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class FolderBackend extends ObmSyncBackend {

	@Inject
	private FolderBackend(CollectionDao collectionDao, IAddressBook bookClient, 
			@Named(CalendarType.CALENDAR) ICalendar calendarClient, 
			@Named(CalendarType.TODO) ICalendar todoClient,
			LoginService login) {
		super(collectionDao, bookClient, calendarClient, todoClient, login);
	}

	public int getServerIdFor(BackendSession bs) throws DaoException, CollectionNotFoundException {
		return getCollectionIdFor(bs.getDevice(), getColName(bs));
	}
	
	public String getColName(BackendSession bs){
		return "obm:\\\\" + bs.getUser().getLoginAtDomain();
	}

}
