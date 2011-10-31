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
package org.obm.push.store;

import java.util.Date;

import org.obm.push.bean.ChangedCollections;
import org.obm.push.bean.Device;
import org.obm.push.bean.SyncState;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.activesync.CollectionNotFoundException;

public interface CollectionDao {

	Integer addCollectionMapping(Device device, String collection) throws DaoException;

	int getCollectionMapping(Device device, String collectionId)
			throws CollectionNotFoundException, DaoException;

	String getCollectionPath(Integer collectionId)
			throws CollectionNotFoundException, DaoException;

	void resetCollection(Device device, Integer collectionId) throws DaoException;
	
	/**
	 * Create a new SyncState entry in database and returns its unique id
	 * @return SyncState database unique id
	 */
	int updateState(Device device, Integer collectionId, SyncState state) throws DaoException;

	SyncState findStateForKey(String syncKey) throws DaoException, CollectionNotFoundException;
	
	ChangedCollections getCalendarChangedCollections(Date lastSync) throws DaoException;

	ChangedCollections getContactChangedCollections(Date lastSync) throws DaoException;
	
}
