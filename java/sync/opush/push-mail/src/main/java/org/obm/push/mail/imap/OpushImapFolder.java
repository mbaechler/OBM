/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.push.mail.imap;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchException;
import javax.mail.search.SearchTerm;

import org.minig.imap.SearchQuery;
import org.obm.push.mail.ImapMessageNotFoundException;
import org.obm.push.mail.imap.command.IMAPCommand;
import org.obm.push.mail.imap.command.UIDCopyMessage;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPFolder.ProtocolCommand;
import com.sun.mail.imap.IMAPInputStream;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.protocol.IMAPProtocol;

public class OpushImapFolder {

	private final IMAPFolder folder;

	public OpushImapFolder(IMAPFolder folder) {
		this.folder = folder;
	}

	public void appendMessageStream(final StreamedLiteral literal, final Flags flags, final Date messageDate) throws MessagingException {
		folder.doCommand(new ProtocolCommand() {
			
			@Override
			public Object doCommand(IMAPProtocol p)
				throws ProtocolException {
				p.append(folder.getFullName(), flags, messageDate, literal);
				return null;
			}
		});
	}
	
	public void appendMessage(Message message) throws MessagingException {
		folder.appendMessages(new Message[]{message});
	}

	public void deleteMessage(Message messageToDelete) throws MessagingException {
		folder.setFlags(new Message[]{messageToDelete}, new Flags(Flags.Flag.DELETED), true);
		expunge();
	}

	public void expunge() throws MessagingException {
		folder.expunge();
	}

	public String getFullName() {
		return folder.getFullName();
	}

	public boolean create(int type) throws MessagingException {
		return folder.create(type);
	}

	public Folder[] list(String pattern) throws MessagingException {
		return folder.list(pattern);
	}

	public void open(int mode) throws MessagingException {
		folder.open(mode);
	}

	public Message getMessageByUID(long messageUid) throws MessagingException, ImapMessageNotFoundException {
		Message messageFound = folder.getMessageByUID(messageUid);

		if (messageFound != null) {
			return messageFound;
		}
		throw new ImapMessageNotFoundException("No message correspond to given UID, UID:" + String.valueOf(messageUid));
	}

	public <T> T doCommand(IMAPCommand<T> command) throws MessagingException {
		return (T) folder.doCommand(command);
	}
	
	public long copyMessageThenGetNewUID(String folderDst, long messageUid) throws MessagingException {
		return doCommand(new UIDCopyMessage(folderDst, messageUid));
	}
	
	public Message fetch(long messageUid, FetchProfile fetchProfile) throws MessagingException, ImapMessageNotFoundException {
		Message message = getMessageByUID(messageUid);
		folder.fetch(new Message[]{message}, fetchProfile);
		return message;
	}
	
	public Message fetchEnvelope(long messageUid) throws MessagingException, ImapMessageNotFoundException {
		FetchProfile fetchProfile = new FetchProfile();
		fetchProfile.add(FetchProfile.Item.ENVELOPE);
		return fetch(messageUid, fetchProfile);
	}

	public InputStream uidFetchMessage(long messageUid) throws MessagingException, ImapMessageNotFoundException {
		IMAPMessage messageToFetch = (IMAPMessage) getMessageByUID(messageUid);
		return peekMessageStream(messageToFetch);
	}

	private InputStream peekMessageStream(IMAPMessage messageToFetch) {
		return peekMessageStream(messageToFetch, null);
	}

	private InputStream peekMessageStream(IMAPMessage messageToFetch, String mimePartAddress) {
		int noMaxByteCount = -1;
		boolean usePeek = true;
		return new IMAPInputStream(messageToFetch, mimePartAddress, noMaxByteCount, usePeek);
	}
	
	public Collection<Long> uidSearch(SearchQuery query) throws MessagingException {
		final SearchTerm searchTerm = toSearchTerm(query);
		return (Collection<Long>) folder.doCommand(new ProtocolCommand() {
			@Override
			public Object doCommand(IMAPProtocol protocol)
					throws ProtocolException {
				try {
					int[] array = protocol.search(searchTerm);
					List<Long> result = Lists.newArrayList();
					for (int i: array) {
						result.add(Long.valueOf(i));
					}
					return result;
				} catch (SearchException e) {
					throw new ProtocolException(e.getMessage(), e);
				}
			}
		});
	}
	
	@VisibleForTesting SearchTerm toSearchTerm(SearchQuery query) {
		SearchTerm dateSearchTerm = toDateSearchTerm(query);
		if (dateSearchTerm != null) {
			return new AndTerm(buildNotDeleted(), dateSearchTerm);
		} else {
			return buildNotDeleted();
		}
	}

	private SearchTerm toDateSearchTerm(SearchQuery query) {
		Date before = query.getBefore();
		Date after = query.getAfter();
		if (after != null) {
			if (before != null) {
				return new AndTerm(buildBeforeTerm(before), buildAfterTerm(after));
			} else {
				return buildAfterTerm(after);
			}
		} else {
			if (before != null) {
				return buildBeforeTerm(before);
			} else {
				return null;
			}
		}
	}

	private NotTerm buildAfterTerm(Date after) {
		return new NotTerm(buildBeforeTerm(after));
	}

	private ReceivedDateTerm buildBeforeTerm(Date before) {
		return new ReceivedDateTerm(ComparisonTerm.LT, before);
	}
	
	private SearchTerm buildNotDeleted() {
		return new NotTerm(new FlagTerm(new Flags(Flags.Flag.DELETED), true));
	}
}
