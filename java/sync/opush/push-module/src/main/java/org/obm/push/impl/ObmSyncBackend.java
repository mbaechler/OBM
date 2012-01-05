package org.obm.push.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.obm.push.bean.BackendSession;
import org.obm.push.bean.Device;
import org.obm.push.bean.ItemChange;
import org.obm.push.bean.PIMDataType;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.store.CollectionDao;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.AuthFault;
import org.obm.sync.client.CalendarType;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.services.IAddressBook;
import org.obm.sync.services.ICalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.name.Named;

public class ObmSyncBackend {

	public static final String OBM_SYNC_ORIGIN = "o-push";
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected String obmSyncHost;

	private final CollectionDao collectionDao;
	private final IAddressBook bookClient;
	private final ICalendar calendarClient;
	private final ICalendar todoClient;
	private final LoginService login;

	protected ObmSyncBackend(CollectionDao collectionDao, IAddressBook bookClient, 
			@Named(CalendarType.CALENDAR) ICalendar calendarClient, 
			@Named(CalendarType.TODO) ICalendar todoClient,
			LoginService login) {
		this.bookClient = bookClient;
		this.calendarClient = calendarClient;
		this.todoClient = todoClient;
		this.collectionDao = collectionDao;
		this.login = login;
	}

	protected AccessToken login(BackendSession session) {
		return login.login(session.getUser().getLoginAtDomain(), session.getPassword(), OBM_SYNC_ORIGIN);
	}

	protected void logout(AccessToken at) {
		login.logout(at);
	}

	public AccessToken login(String loginAtDomain, String password) throws AuthFault {
		AccessToken token = login.login(loginAtDomain, password, OBM_SYNC_ORIGIN);
		try {
			if (token == null || token.getSessionId() == null) {
				throw new AuthFault(loginAtDomain + " can't log on obm-sync. The username or password isn't valid");
			}
		} finally {
			login.logout(token);
		}
		return token;
	}
	
	protected String getDefaultCalendarName(BackendSession bs) {
		return "obm:\\\\" + bs.getUser().getLoginAtDomain() + "\\calendar\\"
				+ bs.getUser().getLoginAtDomain();
	}

	public Integer getCollectionIdFor(Device device, String collection)
			throws CollectionNotFoundException, DaoException {
		return collectionDao.getCollectionMapping(device, collection);
	}

	public String getCollectionPathFor(Integer collectionId) throws CollectionNotFoundException, DaoException {
		return collectionDao.getCollectionPath(collectionId);
	}

	protected List<ItemChange> buildItemsToDeleteFromUids(Integer collectionId, Collection<Long> uids) {
		List<ItemChange> deletions = new LinkedList<ItemChange>();
		for (Long uid: uids) {
			deletions.add( getItemChange(collectionId, uid.toString()) );
		}
		return deletions;
	}
	
	protected ItemChange getItemChange(Integer collectionId, String clientId) {
		return new ItemChange( getServerIdFor(collectionId, clientId) );
	}
	
	protected String collectionIdToString(Integer collectionId) {
		return String.valueOf(collectionId);
	}

	protected String getServerIdFor(Integer collectionId, String clientId) {
		if (collectionId == null || Strings.isNullOrEmpty(clientId)) {
			return null;
		}
		StringBuilder sb = new StringBuilder(10);
		sb.append(collectionId);
		sb.append(':');
		sb.append(clientId);
		return sb.toString();
	}

	protected Integer getItemIdFromServerId(String serverId) {
		String[] idx = serverId.split(":");
		if (idx.length == 2) {
			return Integer.parseInt(idx[1]);
		}
		return null;
	}

	protected Integer getCollectionIdFromServerId(String serverId) {
		String[] idx = serverId.split(":");
		if (idx.length == 2) {
			return Integer.parseInt(idx[0]);
		}
		return null;
	}
	
	protected String createCollectionMapping(Device device, String col) throws DaoException {
		return collectionDao.addCollectionMapping(device, col).toString();
	}
	
	protected IAddressBook getBookClient() {
		return bookClient;
	}
	
	protected ICalendar getCalendarClient() {
		return getEventSyncClient(PIMDataType.CALENDAR);
	}

	protected ICalendar getEventSyncClient(PIMDataType type) {
		if (PIMDataType.TASKS.equals(type)) {
			return todoClient;
		} else {
			return calendarClient;
		}
	}

	public LoginService getSyncClient() {
		return login;
	}
}
