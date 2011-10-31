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
package org.obm.push.mail;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.minig.imap.FastFetch;
import org.minig.imap.SearchQuery;
import org.minig.imap.StoreClient;
import org.obm.push.bean.Email;
import org.obm.push.bean.SyncState;
import org.obm.push.exception.DaoException;
import org.obm.push.store.EmailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class EmailSync implements IEmailSync {

	private final static Logger logger = LoggerFactory.getLogger(EmailSync.class);
	private final EmailDao emailDao;

	@Inject
	public EmailSync(EmailDao emailDao) {
		this.emailDao = emailDao;
	}

	@Override
	public MailChanges getSync(StoreClient imapStore, Integer devId, SyncState state, Integer collectionId) throws DaoException {
		Set<Email> emailsFromIMAP = getImapEmails(imapStore, state.getLastSync());
		Set<Email> alreadySyncedEmails = emailDao.alreadySyncedEmails(collectionId, devId, emailsFromIMAP);
		Set<Email> emailsToSync = Sets.difference(emailsFromIMAP, alreadySyncedEmails);
		MailChanges mailChanges = new MailChanges(new HashSet<Email>(), emailsToSync, emailsFromIMAP);
		loggerInfo(state.getLastSync(), emailsFromIMAP, mailChanges);
		return mailChanges;
	}

	private void loggerInfo(Date syncStartDate, Set<Email> emailsFromIMAP, MailChanges mailChanges) {
		logger.info("Synchronization date {}", syncStartDate);
		logger.info("{} email(s) from imap", emailsFromIMAP.size());
		logger.info("{} email(s) will be updated to pda", mailChanges.getUpdatedEmailFromImap().size());
		logger.info("{} email(s) will be updated to db", mailChanges.getUpdatedEmailToDB().size());
	}

	private Set<Email> getImapEmails(StoreClient imapStore, Date windows) {
		Collection<Long> uids = imapStore.uidSearch(new SearchQuery(null, windows));
		Collection<FastFetch> mails = imapStore.uidFetchFast(uids);
		return EmailFactory.listEmailFromFastFetch(mails);
	}

}
