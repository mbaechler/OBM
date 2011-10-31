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

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.obm.push.bean.Email;
import org.obm.push.bean.SyncState;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.EmailNotFoundException;

public interface EmailDao {

	void deleteSyncEmails(Integer devId, Integer collectionId, Collection<Long> mailUids) throws DaoException;

	void deleteSyncEmails(Integer devId, Integer collectionId, Date lastSync, Collection<Long> uids) throws DaoException;
	
	Set<Email> listSyncedEmails(Integer devId, Integer collectionId, SyncState state) throws DaoException;

	Set<Long> getDeletedMail(Integer devId, Integer collectionId, Date lastSync) throws DaoException;

	Email getSyncedEmail(Integer devId, Integer collectionId, long uid) throws DaoException, EmailNotFoundException;

	Set<Email> alreadySyncedEmails(int collectionId, int device, Collection<Email> emails) throws DaoException;
	
	void update(Integer devId, Integer collectionId, Email email) throws DaoException;

	void insert(Integer devId, Integer collectionId, Date lastSync, Email email) throws DaoException;

	Set<Email> listDeletedEmails(Integer devId, Integer collectionId) throws DaoException;

	void createSyncEntries(Integer devId, Integer collectionId,
			Set<Email> emailsToMarkAsSynced, Date lastSync) throws DaoException;

	void updateSyncEntriesStatus(Integer devId, Integer collectionId,
			Set<Email> alreadySyncedEmails) throws DaoException;
	
}
