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
package org.obm.push;

import org.obm.push.backend.DataDelta;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.MSEmail;
import org.obm.push.bean.PIMDataType;
import org.obm.push.bean.SyncState;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.exception.activesync.ProcessingEmailException;
import org.obm.sync.calendar.EventObmId;

public class DummyInvitationFilterManager implements IInvitationFilterManager {

	@Override
	public DataDelta filterEvent(BackendSession bs, SyncState state,
			Integer eventCollectionId, DataDelta delta) throws DaoException {
		return delta;
	}

	@Override
	public void createOrUpdateInvitation(BackendSession bs, SyncState state,
			Integer emailCollectionId, DataDelta delta) throws DaoException,
			ProcessingEmailException {
		return;
	}

	@Override
	public void handleMeetingResponse(BackendSession bs,
			Integer invitationCollectionId, MSEmail invitation)
			throws DaoException {
		return;
	}

	@Override
	public int getCountFilterChanges(BackendSession bs, String syncKey,
			PIMDataType dataType, Integer collectionId) throws DaoException {
		return 0;
	}

	@Override
	public void deleteFilteredEvent(Integer collectionId, EventObmId eventUid)
			throws DaoException {
		return;
	}

	@Override
	public void deleteFilteredEmail(Integer collectionId, Long mailUid)
			throws DaoException {
		return;
	}

	@Override
	public DataDelta filterInvitation(BackendSession bs, SyncState state,
			Integer emailCollectionId, DataDelta delta) throws DaoException,
			ProcessingEmailException, CollectionNotFoundException {
		return delta;
	}

	@Override
	public void removeInvitationStatus(Integer eventCollectionId,
			Integer emailCollectionId, Long mailUid)
			throws CollectionNotFoundException, ProcessingEmailException {
		return;
	}

}
