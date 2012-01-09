package org.obm.push.mail;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.obm.push.bean.Address;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.Email;
import org.obm.push.bean.MSEmail;
import org.obm.push.bean.SyncState;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.SendEmailException;
import org.obm.push.exception.SmtpInvalidRcptException;
import org.obm.push.exception.activesync.ProcessingEmailException;
import org.obm.push.exception.activesync.StoreEmailException;
import org.obm.sync.services.ICalendar;

public interface MailboxService {

	MailChanges getSync(BackendSession bs, SyncState state, Integer devId, Integer collectionId, String collectionName)
			throws MailException, DaoException;

	List<MSEmail> fetchMails(BackendSession bs, ICalendar calendarClient, Integer collectionId, String collectionName, 
			Collection<Long> uids) throws MailException;

	void updateReadFlag(BackendSession bs, String collectionName, Long uid, boolean read) throws MailException;

	String parseMailBoxName(BackendSession bs, String collectionName) throws MailException;

	void delete(BackendSession bs, Integer devId, String collectionPath, Integer collectionId, Long uid) throws MailException, DaoException;

	Long moveItem(BackendSession bs, Integer devId, String srcFolder, Integer srcFolderId, String dstFolder, Integer dstFolderId, 
			Long uid) throws MailException, DaoException;

	List<InputStream> fetchMIMEMails(BackendSession bs, ICalendar calendarClient, String collectionName, 
			Set<Long> uids) throws MailException;

	void setAnsweredFlag(BackendSession bs, String collectionName, Long uid) throws MailException;

	void sendEmail(BackendSession bs, Address from, Set<Address> setTo, Set<Address> setCc, Set<Address> setCci, InputStream mimeMail,
			Boolean saveInSent) throws SendEmailException, ProcessingEmailException, SmtpInvalidRcptException, StoreEmailException;

	InputStream findAttachment(BackendSession bs, String collectionName, Long mailUid, String mimePartAddress) throws MailException;

	void purgeFolder(BackendSession bs, Integer devId, String collectionPath, Integer collectionId) throws MailException, DaoException;

	Long storeInInbox(BackendSession bs, InputStream mailContent, boolean isRead) throws StoreEmailException;

	boolean getLoginWithDomain();

	boolean getActivateTLS();
	
	void updateData(Integer devId, Integer collectionId, Date lastSync, Collection<Long> removedEmailsIds, Collection<Email> updated)
			throws DaoException;

}
