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

import org.obm.push.backend.IContentsImporter;
import org.obm.push.bean.AttendeeStatus;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.IApplicationData;
import org.obm.push.bean.MSContact;
import org.obm.push.bean.MSEmail;
import org.obm.push.bean.PIMDataType;
import org.obm.push.calendar.CalendarBackend;
import org.obm.push.contacts.ContactsBackend;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.SendEmailException;
import org.obm.push.exception.SmtpInvalidRcptException;
import org.obm.push.exception.UnknownObmSyncServerException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.exception.activesync.NotAllowedException;
import org.obm.push.exception.activesync.ProcessingEmailException;
import org.obm.push.exception.activesync.ServerItemNotFoundException;
import org.obm.push.mail.MailBackend;
import org.obm.sync.calendar.Event;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ContentsImporter implements IContentsImporter {

	private final MailBackend mailBackend;
	private final CalendarBackend calBackend;
	private final ContactsBackend contactBackend;
	private final IInvitationFilterManager invitationFilterManager;

	@Inject
	private ContentsImporter(MailBackend mailBackend,
			CalendarBackend calBackend, ContactsBackend contactBackend,
			IInvitationFilterManager invitationFilterManager) {

		this.mailBackend = mailBackend;
		this.calBackend = calBackend;
		this.contactBackend = contactBackend;
		this.invitationFilterManager = invitationFilterManager;
	}

	@Override
	public String importMessageChange(BackendSession bs, Integer collectionId, String serverId, String clientId, IApplicationData data) 
			throws CollectionNotFoundException, DaoException, UnknownObmSyncServerException, ProcessingEmailException, ServerItemNotFoundException {
		
		String id = null;
		switch (data.getType()) {
		case CONTACTS:
			id = contactBackend.createOrUpdate(bs, collectionId, serverId, (MSContact) data);
			break;
		case EMAIL:
			id = mailBackend.createOrUpdate(bs, collectionId, serverId,
					clientId, (MSEmail) data);
			break;
		case TASKS:
		case CALENDAR:
			id = calBackend.createOrUpdate(bs, collectionId, serverId, data);
			break;
		case FOLDER:
			break;
		}
		return id;
	}

	@Override
	public void importMessageDeletion(BackendSession bs, PIMDataType type, Integer collectionId, String serverId, Boolean moveToTrash) 
					throws CollectionNotFoundException, DaoException, UnknownObmSyncServerException, ProcessingEmailException, ServerItemNotFoundException {
		
		switch (type) {
		case CALENDAR:
			Event event = calBackend.getEventFromServerId(bs, serverId);
			calBackend.delete(bs, collectionId, serverId);
			if (event.getUid() != null) {
				invitationFilterManager.deleteFilteredEvent(collectionId, event.getUid());
			}
			break;
		case CONTACTS:
			contactBackend.delete(bs, serverId);
			break;
		case EMAIL:
			Long emailUid = mailBackend.getEmailUidFromServerId(serverId);
			mailBackend.delete(bs, serverId,moveToTrash);
			if(emailUid != null){
				invitationFilterManager.deleteFilteredEmail(collectionId, emailUid);
			}
			break;
		case TASKS:
			calBackend.delete(bs, collectionId, serverId);
			break;
		case FOLDER:
			break;
		}
	}

	public String importMoveItem(BackendSession bs, PIMDataType type,
			String srcFolder, String dstFolder, String messageId) throws CollectionNotFoundException, DaoException, ProcessingEmailException {
		switch (type) {
		case EMAIL:
			return mailBackend.move(bs, srcFolder, dstFolder, messageId);
		case CALENDAR:
		case CONTACTS:
		case TASKS:
		case FOLDER:
			break;
		}
		return null;
	}

	@Override
	public void sendEmail(BackendSession bs, byte[] mailContent,
			Boolean saveInSent)  throws SendEmailException, ProcessingEmailException, SmtpInvalidRcptException {
		mailBackend.sendEmail(bs, mailContent, saveInSent);
	}

	@Override
	public void replyEmail(BackendSession bs, byte[] mailContent, Boolean saveInSent, Integer collectionId, String serverId)  
					throws SendEmailException, ProcessingEmailException, SmtpInvalidRcptException, CollectionNotFoundException, 
					DaoException, UnknownObmSyncServerException {
		mailBackend.replyEmail(bs, mailContent, saveInSent, collectionId, serverId);
	}

	@Override
	public String importCalendarUserStatus(BackendSession bs,  Integer invitationCollexctionId, MSEmail invitation,
			AttendeeStatus userResponse) throws DaoException, CollectionNotFoundException, UnknownObmSyncServerException, ServerItemNotFoundException {
		
		String serverId = calBackend.handleMeetingResponse(bs, invitation, userResponse);
		invitationFilterManager.handleMeetingResponse(bs, invitationCollexctionId, invitation);
		return serverId;
	}

	@Override
	public void forwardEmail(BackendSession bs, byte[] mailContent, Boolean saveInSent, String collectionId, String serverId)  
			throws SendEmailException, ProcessingEmailException, SmtpInvalidRcptException, CollectionNotFoundException, 
			UnknownObmSyncServerException, DaoException {
		mailBackend.forwardEmail(bs, mailContent, saveInSent, collectionId, serverId);
	}

	@Override
	public void emptyFolderContent(BackendSession bs, String collectionPath, boolean deleteSubFolder) 
			throws CollectionNotFoundException, NotAllowedException, DaoException, ProcessingEmailException {
		
		if (collectionPath != null && collectionPath.contains("email\\")) {
			mailBackend.purgeFolder(bs, collectionPath, deleteSubFolder);
		} else {
			throw new NotAllowedException(
					"emptyFolderContent is only supported for emails, collection was "
							+ collectionPath);
		}
	}

}
