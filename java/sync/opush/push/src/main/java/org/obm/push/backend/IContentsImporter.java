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
package org.obm.push.backend;

import org.obm.push.bean.AttendeeStatus;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.IApplicationData;
import org.obm.push.bean.MSEmail;
import org.obm.push.bean.PIMDataType;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.SendEmailException;
import org.obm.push.exception.SmtpInvalidRcptException;
import org.obm.push.exception.UnknownObmSyncServerException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.exception.activesync.NotAllowedException;
import org.obm.push.exception.activesync.ProcessingEmailException;
import org.obm.push.exception.activesync.ServerItemNotFoundException;

/**
 * Content management interface, ie. CRUD API.
 */
public interface IContentsImporter {

	String importMessageChange(BackendSession bs, Integer collectionId, String serverId, String clientId, IApplicationData data)
			throws CollectionNotFoundException, DaoException, UnknownObmSyncServerException, ProcessingEmailException, ServerItemNotFoundException;

	void importMessageDeletion(BackendSession bs, PIMDataType type, Integer collectionId, String serverId, Boolean moveToTrash) 
			throws CollectionNotFoundException, DaoException, UnknownObmSyncServerException, ProcessingEmailException, ServerItemNotFoundException;

	String importMoveItem(BackendSession bs, PIMDataType type, String srcFolder, String dstFolder, String messageId)
			throws CollectionNotFoundException, DaoException, ProcessingEmailException;

	String importCalendarUserStatus(BackendSession bs, Integer invitationCollectionId, MSEmail invitation,
			AttendeeStatus userResponse) throws DaoException, CollectionNotFoundException, UnknownObmSyncServerException, ServerItemNotFoundException;

	void sendEmail(BackendSession bs, byte[] mailContent, Boolean saveInSent)
			throws SendEmailException, ProcessingEmailException, SmtpInvalidRcptException;

	void replyEmail(BackendSession bs, byte[] mailContent, Boolean saveInSent,	Integer collectionId, String serverId) 
			throws SendEmailException, ProcessingEmailException, SmtpInvalidRcptException, CollectionNotFoundException, 
			DaoException, UnknownObmSyncServerException;

	void forwardEmail(BackendSession bs, byte[] mailContent, Boolean saveInSent, String collectionId, String serverId)
			throws SendEmailException, ProcessingEmailException, SmtpInvalidRcptException, CollectionNotFoundException, 
			UnknownObmSyncServerException, DaoException;

	void emptyFolderContent(BackendSession bs, String collectionPath, boolean deleteSubFolder) 
			throws CollectionNotFoundException, NotAllowedException, DaoException, ProcessingEmailException;
	
}
