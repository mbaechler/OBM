/* ***** BEGIN LICENSE BLOCK *****
 *
 * %%
 * Copyright (C) 2000 - 2011 Linagora
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK ***** */
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
import org.obm.sync.client.ISyncClient;
import org.obm.sync.client.book.BookClient;
import org.obm.sync.client.calendar.AbstractEventSyncClient;
import org.obm.sync.client.calendar.CalendarClient;
import org.obm.sync.client.calendar.TodoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class ObmSyncBackend {

	public static final String OBM_SYNC_ORIGIN = "o-push";
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected String obmSyncHost;

	private final CollectionDao collectionDao;
	private final BookClient bookClient;
	private final CalendarClient calendarClient;
	private final TodoClient todoClient;

	protected ObmSyncBackend(CollectionDao collectionDao, BookClient bookClient, CalendarClient calendarClient, TodoClient todoClient) {
		this.bookClient = bookClient;
		this.calendarClient = calendarClient;
		this.todoClient = todoClient;
		this.collectionDao = collectionDao;
	}

	protected AccessToken login(ISyncClient client, BackendSession session) {
		return client.login(session.getLoginAtDomain(), session.getPassword(), OBM_SYNC_ORIGIN);
	}

	public boolean validatePassword(String loginAtDomain, String password) {
		AccessToken token = calendarClient.login(loginAtDomain, password, OBM_SYNC_ORIGIN);
		try {
			if (token == null || token.getSessionId() == null) {
				logger.info(loginAtDomain + " can't log on obm-sync. The username or password isn't valid");
				return false;
			}
		} finally {
			calendarClient.logout(token);
		}
		return true;
	}
	
	protected String getDefaultCalendarName(BackendSession bs) {
		return "obm:\\\\" + bs.getLoginAtDomain() + "\\calendar\\"
				+ bs.getLoginAtDomain();
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

	protected Integer getItemIdFor(String serverId) {
		int idx = serverId.lastIndexOf(":");
		return Integer.parseInt(serverId.substring(idx + 1));
	}

	protected String createCollectionMapping(Device device, String col) throws DaoException {
		return collectionDao.addCollectionMapping(device, col).toString();
	}
	
	protected BookClient getBookClient() {
		return bookClient;
	}
	
	protected AbstractEventSyncClient getCalendarClient() {
		return getEventSyncClient(PIMDataType.CALENDAR);
	}

	protected AbstractEventSyncClient getEventSyncClient(PIMDataType type) {
		if (PIMDataType.TASKS.equals(type)) {
			return todoClient;
		} else {
			return calendarClient;
		}
	}
	
}
