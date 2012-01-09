package org.obm.push.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.minig.mime.QuotedPrintableDecoderInputStream;
import org.obm.configuration.EmailConfiguration;
import org.obm.locator.LocatorClientException;
import org.obm.push.backend.DataDelta;
import org.obm.push.bean.Address;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.FilterType;
import org.obm.push.bean.FolderType;
import org.obm.push.bean.IApplicationData;
import org.obm.push.bean.ItemChange;
import org.obm.push.bean.MSAttachementData;
import org.obm.push.bean.MSEmail;
import org.obm.push.bean.SyncState;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.SendEmailException;
import org.obm.push.exception.SmtpInvalidRcptException;
import org.obm.push.exception.UnknownObmSyncServerException;
import org.obm.push.exception.activesync.AttachementNotFoundException;
import org.obm.push.exception.activesync.CollectionNotFoundException;
import org.obm.push.exception.activesync.FolderTypeNotFoundException;
import org.obm.push.exception.activesync.NotAllowedException;
import org.obm.push.exception.activesync.ProcessingEmailException;
import org.obm.push.exception.activesync.StoreEmailException;
import org.obm.push.service.impl.MappingService;
import org.obm.push.tnefconverter.TNEFConverterException;
import org.obm.push.tnefconverter.TNEFUtils;
import org.obm.push.utils.FileUtils;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.client.CalendarType;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.services.ICalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class MailBackendImpl implements MailBackend {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final MailboxService emailManager;
	private final ICalendar calendarClient;

	private final LoginService login;

	private final MappingService mappingService;

	@Inject
	/* package */ MailBackendImpl(MailboxService emailManager, 
			@Named(CalendarType.CALENDAR) ICalendar calendarClient, 
			LoginService login,
			MappingService mappingService)  {
		this.emailManager = emailManager;
		this.calendarClient = calendarClient;
		this.login = login;
		this.mappingService = mappingService;
	}

	@Override
	public List<ItemChange> getHierarchyChanges(BackendSession bs) throws DaoException {
		LinkedList<ItemChange> ret = new LinkedList<ItemChange>();
		ret.add(genItemChange(bs, EmailConfiguration.IMAP_INBOX_NAME, FolderType.DEFAULT_INBOX_FOLDER));
		ret.add(genItemChange(bs,  EmailConfiguration.IMAP_DRAFTS_NAME, FolderType.DEFAULT_DRAFTS_FOLDERS));
		ret.add(genItemChange(bs,  EmailConfiguration.IMAP_SENT_NAME, FolderType.DEFAULT_SENT_EMAIL_FOLDER));
		ret.add(genItemChange(bs,  EmailConfiguration.IMAP_TRASH_NAME,FolderType.DEFAULT_DELETED_ITEMS_FOLDERS));
		return ret;
	}

	private ItemChange genItemChange(BackendSession bs, String imapFolder,
			FolderType type) throws DaoException {
		ItemChange ic = new ItemChange();
		ic.setParentId("0");
		ic.setDisplayName(bs.getUser().getLoginAtDomain() + " " + imapFolder);
		ic.setItemType(type);

		String imapPath = buildPath(bs, imapFolder);
		String serverId;
		try {
			Integer collectionId = mappingService.getCollectionIdFor(bs.getDevice(), imapPath);
			serverId = mappingService.collectionIdToString(collectionId);
		} catch (CollectionNotFoundException e) {
			serverId = mappingService.createCollectionMapping(bs.getDevice(), imapPath);
			ic.setIsNew(true);
		}

		ic.setServerId(serverId);
		return ic;
	}
	
	private String buildPath(BackendSession bs, String imapFolder) {
		StringBuilder sb = new StringBuilder();
		sb.append("obm:\\\\");
		sb.append(bs.getUser().getLoginAtDomain());
		sb.append("\\email\\");
		sb.append(imapFolder);
		return sb.toString();
	}

	private String getWasteBasketPath(BackendSession bs) {
		return buildPath(bs, "Trash");
	}

	private MailChanges getSync(BackendSession bs, SyncState state, Integer collectionId, FilterType filterType) 
			throws ProcessingEmailException, CollectionNotFoundException {
		
		try {
			String collectionPath = mappingService.getCollectionPathFor(collectionId);
			Integer devDbId = bs.getDevice().getDatabaseId();

			state.updatingLastSync(filterType);
			return emailManager.getSync(bs, state, devDbId, collectionId, collectionPath);
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		} catch (MailException e) {
			throw new ProcessingEmailException(e);
		} catch (LocatorClientException e) {
			throw new ProcessingEmailException(e);
		}
	}
	
	@Override
	public DataDelta getMailChanges(BackendSession bs, SyncState state, Integer collectionId, FilterType filterType) 
			throws ProcessingEmailException, CollectionNotFoundException {
		
		MailChanges mailChanges = getSync(bs, state, collectionId, filterType);
		try {
			return getDataDelta(bs, collectionId, mailChanges);
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		}
	}
	
	@Override
	public DataDelta getAndUpdateEmailChanges(BackendSession bs, SyncState state, Integer collectionId, FilterType filter) 
			throws ProcessingEmailException, CollectionNotFoundException {
		
		MailChanges mailChanges = getSync(bs, state, collectionId, filter);
		try {
			emailManager.updateData(bs.getDevice().getDatabaseId(), collectionId, state.getLastSync(), 
					mailChanges.getRemovedEmailsUids(), mailChanges.getNewAndUpdatedEmails());
			return getDataDelta(bs, collectionId, mailChanges);
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		}
	}

	private DataDelta getDataDelta(BackendSession bs, Integer collectionId, MailChanges mailChanges) 
			throws ProcessingEmailException, CollectionNotFoundException, DaoException {
		
		List<ItemChange> itemChanges = fetchMails(bs, collectionId, 
				mappingService.getCollectionPathFor(collectionId), mailChanges.getNewEmailsUids());
		List<ItemChange> itemsToDelete = mappingService.buildItemsToDeleteFromUids(collectionId, mailChanges.getRemovedEmailsUids());
		return new DataDelta(itemChanges, itemsToDelete, mailChanges.getLastSync());
	}
	
	private List<ItemChange> fetchMails(
			BackendSession bs, Integer collectionId, String collection, 
			Collection<Long> emailsUids) throws ProcessingEmailException {
		
		ImmutableList.Builder<ItemChange> itch = ImmutableList.builder();
		try {
			List<MSEmail> msMails = 
					emailManager.fetchMails(bs, calendarClient, collectionId, collection, emailsUids);
			for (MSEmail mail: msMails) {
				itch.add(getItemChange(collectionId, mail.getUid(), mail));
			}
			return itch.build();
		} catch (MailException e) {
			throw new ProcessingEmailException(e);
		} catch (LocatorClientException e) {
			throw new ProcessingEmailException(e);
		}
	}
	
	private ItemChange getItemChange(Integer collectionId, Long uid, IApplicationData data) {
		ItemChange ic = new ItemChange();
		ic.setServerId(mappingService.getServerIdFor(collectionId, "" + uid));
		ic.setData(data);
		return ic;
	}
	
	@Override
	public List<ItemChange> fetchItems(BackendSession bs, List<String> fetchIds) throws ProcessingEmailException {
		LinkedList<ItemChange> ret = new LinkedList<ItemChange>();
		Map<Integer, Collection<Long>> emailUids = getEmailUidByCollectionId(fetchIds);
		for (Entry<Integer, Collection<Long>> entry : emailUids.entrySet()) {
			Integer collectionId = entry.getKey();
			Collection<Long> uids = entry.getValue();
			try {
				ret.addAll(fetchItems(bs, collectionId, uids));
			} catch (CollectionNotFoundException e) {
				logger.error("fetchItems : collection {} not found !", collectionId);
			}
		}
		return ret;
	}
	
	private Map<Integer, Collection<Long>> getEmailUidByCollectionId(List<String> fetchIds) {
		Map<Integer, Collection<Long>> ret = Maps.newHashMap();
		for (String serverId : fetchIds) {
			Integer collectionId = mappingService.getCollectionIdFor(serverId);
			Collection<Long> set = ret.get(collectionId);
			if (set == null) {
				set = Sets.newHashSet();
				ret.put(collectionId, set);
			}
			set.add(getEmailUidFromServerId(serverId));
		}
		return ret;
	}

	@Override
	public List<ItemChange> fetchItems(BackendSession bs, Integer collectionId, Collection<Long> uids) 
			throws CollectionNotFoundException, ProcessingEmailException {
		
		try {
			final Builder<ItemChange> ret = ImmutableList.builder();
			final String collectionPath = mappingService.getCollectionPathFor(collectionId);
			final List<MSEmail> emails = emailManager.fetchMails(bs, calendarClient, collectionId, collectionPath, uids);
			for (final MSEmail email: emails) {
				ItemChange ic = new ItemChange();
				ic.setServerId(mappingService.getServerIdFor(collectionId, String.valueOf(email.getUid())));
				ic.setData(email);
				ret.add(ic);
			}
			return ret.build();	
		} catch (MailException e) {
			throw new ProcessingEmailException(e);
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		} catch (LocatorClientException e) {
			throw new ProcessingEmailException(e);
		}
	}

	@Override
	public void delete(BackendSession bs, String serverId, Boolean moveToTrash) throws CollectionNotFoundException, ProcessingEmailException {
		try {
			if (moveToTrash) {
				logger.info("move to trash serverId {}", serverId);
			} else {
				logger.info("delete serverId {}", serverId);
			}
			if (serverId != null) {
				final Long uid = getEmailUidFromServerId(serverId);
				final Integer collectionId = mappingService.getCollectionIdFor(serverId);
				final String collectionName = mappingService.getCollectionPathFor(collectionId);
				final Integer devDbId = bs.getDevice().getDatabaseId();

				if (moveToTrash) {
					String wasteBasketPath = getWasteBasketPath(bs);
					Integer wasteBasketId = mappingService.getCollectionIdFor(bs.getDevice(), wasteBasketPath);
					emailManager.moveItem(bs, devDbId, collectionName, collectionId, wasteBasketPath, wasteBasketId, uid);
				} else {
					emailManager.delete(bs, devDbId, collectionName, collectionId, uid);
				}
			}	
		} catch (MailException e) {
			throw new ProcessingEmailException(e);
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		} catch (LocatorClientException e) {
			throw new ProcessingEmailException(e);
		}
	}


	protected String getDefaultCalendarName(BackendSession bs) {
		return "obm:\\\\" + bs.getUser().getLoginAtDomain() + "\\calendar\\"
				+ bs.getUser().getLoginAtDomain();
	}
	
	@Override
	public String createOrUpdate(BackendSession bs, Integer collectionId, String serverId, String clientId, MSEmail data) 
			throws CollectionNotFoundException, ProcessingEmailException {
		
		try {
			String collectionPath = mappingService.getCollectionPathFor(collectionId);
			logger.info("createOrUpdate( {}, {}, {} )", new Object[]{collectionPath, serverId, clientId});
			if (serverId != null) {
				Long mailUid = getEmailUidFromServerId(serverId);
				emailManager.updateReadFlag(bs, collectionPath, mailUid, data.isRead());
			}
			return serverId;
		} catch (MailException e) {
			throw new ProcessingEmailException(e);
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		} catch (LocatorClientException e) {
			throw new ProcessingEmailException(e);
		}
	}

	@Override
	public String move(BackendSession bs, String srcFolder, String dstFolder, String messageId) 
			throws CollectionNotFoundException, ProcessingEmailException {
		
		try {
			logger.info("move( messageId =  {}, from = {}, to = {} )", new Object[]{messageId, srcFolder, dstFolder});
			final Long currentMailUid = getEmailUidFromServerId(messageId);
			final Integer srcFolderId = mappingService.getCollectionIdFor(bs.getDevice(), srcFolder);
			final Integer dstFolderId = mappingService.getCollectionIdFor(bs.getDevice(), dstFolder);
			final Integer devDbId = bs.getDevice().getDatabaseId();
			Long newUidMail = emailManager.moveItem(bs, devDbId, srcFolder, srcFolderId, dstFolder, dstFolderId, currentMailUid);
			return dstFolderId + ":" + newUidMail;	
		} catch (MailException e) {
			throw new ProcessingEmailException(e);
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		} catch (LocatorClientException e) {
			throw new ProcessingEmailException(e);
		}
	}


	@Override
	public void sendEmail(BackendSession bs, byte[] mailContent, Boolean saveInSent) throws ProcessingEmailException {
		try {
			SendEmailHandler handler = new SendEmailHandler(getUserEmail(bs));
			send(bs, mailContent, handler, saveInSent);
		} catch (UnknownObmSyncServerException e) {
			throw new ProcessingEmailException(e);
		} 
	}

	@Override
	public void replyEmail(BackendSession bs, byte[] mailContent, Boolean saveInSent, Integer collectionId, String serverId)
			throws ProcessingEmailException, CollectionNotFoundException {
		
		try {
			String collectionPath = "";
			if (collectionId != null && collectionId > 0) {
				collectionPath = mappingService.getCollectionPathFor(collectionId);
			}
			
			if (serverId == null || !serverId.isEmpty()) {
				collectionId = mappingService.getCollectionIdFor(serverId);
				collectionPath = mappingService.getCollectionPathFor(collectionId);
			}
			
			Long uid = getEmailUidFromServerId(serverId);
			Set<Long> uids = new HashSet<Long>();
			uids.add(uid);
			List<MSEmail> mail = emailManager.fetchMails(bs, calendarClient, collectionId, collectionPath, uids);

			if (mail.size() > 0) {
				//TODO uses headers References and In-Reply-To
				ReplyEmailHandler reh = new ReplyEmailHandler(getUserEmail(bs), mail.get(0));
				send(bs, mailContent, reh, saveInSent);
				emailManager.setAnsweredFlag(bs, collectionPath, uid);
			} else {
				SendEmailHandler handler = new SendEmailHandler(getUserEmail(bs));
				send(bs, mailContent, handler, saveInSent);
			}
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		} catch (MailException e) {
			throw new ProcessingEmailException(e);
		} catch (UnknownObmSyncServerException e) {
			throw new ProcessingEmailException(e);
		} catch (LocatorClientException e) {
			throw new ProcessingEmailException(e);
		} 
	}

	@Override
	public void forwardEmail(BackendSession bs, byte[] mailContent, Boolean saveInSent, String collectionId, String serverId) 
			throws ProcessingEmailException, CollectionNotFoundException {
		
		try {
			String collectionName = mappingService.getCollectionPathFor(Integer
					.parseInt(collectionId));
			Long uid = getEmailUidFromServerId(serverId);
			Set<Long> uids = new HashSet<Long>();
			uids.add(uid);
			List<InputStream> mail = emailManager.fetchMIMEMails(bs,
					calendarClient, collectionName, uids);

			if (mail.size() > 0) {
				ForwardEmailHandler reh = new ForwardEmailHandler(
						getUserEmail(bs), mail.get(0));
				send(bs, mailContent, reh, saveInSent);
				try{
					emailManager.setAnsweredFlag(bs, collectionName, uid);
				} catch (Throwable e) {
					logger.info("Can't set Answered Flag to mail["+uid+"]");
				}
			} else {
				SendEmailHandler handler = new SendEmailHandler(
						getUserEmail(bs));
				send(bs, mailContent, handler, saveInSent);
			}
		} catch (NumberFormatException e) {
			throw new ProcessingEmailException(e);
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		} catch (MailException e) {
			throw new ProcessingEmailException(e);
		} catch (UnknownObmSyncServerException e) {
			throw new ProcessingEmailException(e);
		} catch (LocatorClientException e) {
			throw new ProcessingEmailException(e);
		} 
	}

	private AccessToken login(BackendSession session) {
		return login.login(session.getUser().getLoginAtDomain(), session.getPassword());
	}
	
	private String getUserEmail(BackendSession bs) throws UnknownObmSyncServerException {
		ICalendar cal = calendarClient;
		AccessToken at = login(bs);
		try {
			return cal.getUserEmail(at);
		} catch (ServerFault e) {
			throw new UnknownObmSyncServerException(e);
		} finally {
			login.logout(at);
		}
	}

	private void send(BackendSession bs, byte[] mailContent, SendEmailHandler handler, Boolean saveInSent) throws ProcessingEmailException {
		InputStream emailData = null;
		try {
			MimeStreamParser parser = new MimeStreamParser();
			parser.setContentHandler(handler);
			parser.parse(new ByteArrayInputStream(mailContent));
			emailData = new ByteArrayInputStream(FileUtils.streamBytes(handler.getMessage(), true));
			emailData.mark(emailData.available());
			
			Boolean isScheduleMeeting = !TNEFUtils.isScheduleMeetingRequest(emailData);
			emailData.reset();

			Address from = getAddress(handler.getFrom());
			if(!handler.isInvitation()  &&  isScheduleMeeting){
				emailManager.sendEmail(bs, from, handler.getTo(),
						handler.getCc(), handler.getCci(), emailData, saveInSent);	
			} else {
				logger.warn("OPUSH blocks email invitation sending by PDA. Now that obm-sync handle email sending on event creation/modification/deletion, we must filter mail from PDA for these actions.");
			}
		} catch (TNEFConverterException e) {
			throw new ProcessingEmailException(e);
		} catch (MimeException e) {
			throw new ProcessingEmailException(e);
		} catch (IOException e) {
			throw new ProcessingEmailException(e);
		} catch (StoreEmailException e) {
			throw new ProcessingEmailException(e);
		} catch (SendEmailException e) {
			throw new ProcessingEmailException(e);
		} catch (SmtpInvalidRcptException e) {
			throw new ProcessingEmailException(e);
		} finally {
			if(emailData != null){
				try{emailData.close();} catch (Throwable e) {}
			}
		}
	}

	private Address getAddress(String from) throws ProcessingEmailException {
		if(from == null || !from.contains("@")){
			throw new ProcessingEmailException(""+from+"is not a valid email");
		}
		return new Address(from);
	}

	@Override
	public MSEmail getEmail(BackendSession bs, Integer collectionId, String serverId) throws CollectionNotFoundException, ProcessingEmailException {
		try {
			String collectionName = mappingService.getCollectionPathFor(collectionId);
			Long uid = getEmailUidFromServerId(serverId);
			Set<Long> uids = new HashSet<Long>();
			uids.add(uid);
			List<MSEmail> emails = emailManager.fetchMails(bs, calendarClient, collectionId, collectionName, uids);
			if (emails.size() > 0) {
				return emails.get(0);
			}
			return null;	
		} catch (MailException e) {
			throw new ProcessingEmailException(e);
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		} catch (LocatorClientException e) {
			throw new ProcessingEmailException(e);
		}
	}

	@Override
	public MSAttachementData getAttachment(BackendSession bs, String attachmentId) 
			throws AttachementNotFoundException, CollectionNotFoundException, ProcessingEmailException {
		
		if (attachmentId != null && !attachmentId.isEmpty()) {
			Map<String, String> parsedAttId = AttachmentHelper.parseAttachmentId(attachmentId);
			try {
				String collectionId = parsedAttId
						.get(AttachmentHelper.COLLECTION_ID);
				String messageId = parsedAttId.get(AttachmentHelper.MESSAGE_ID);
				String mimePartAddress = parsedAttId
						.get(AttachmentHelper.MIME_PART_ADDRESS);
				String contentType = parsedAttId
						.get(AttachmentHelper.CONTENT_TYPE);
				String contentTransferEncoding = parsedAttId
						.get(AttachmentHelper.CONTENT_TRANSFERE_ENCODING);
				logger.info("attachmentId= [collectionId:" + collectionId
						+ "] [emailUid" + messageId + "] [mimePartAddress:"
						+ mimePartAddress + "] [contentType" + contentType
						+ "] [contentTransferEncoding"
						+ contentTransferEncoding + "]");

				String collectionName = mappingService.getCollectionPathFor(Integer
						.parseInt(collectionId));
				InputStream is = emailManager.findAttachment(bs,
						collectionName, Long.parseLong(messageId),
						mimePartAddress);

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				FileUtils.transfer(is, out, true);
				byte[] rawData = out.toByteArray();

				if ("QUOTED-PRINTABLE".equals(contentTransferEncoding)) {
					out = new ByteArrayOutputStream();
					InputStream in = new QuotedPrintableDecoderInputStream(
							new ByteArrayInputStream(rawData));
					FileUtils.transfer(in, out, true);
					rawData = out.toByteArray();
				} else if ("BASE64".equals(contentTransferEncoding)) {
					rawData = new Base64().decode(rawData);
				}

				return new MSAttachementData(contentType,
						new ByteArrayInputStream(rawData));
		
			} catch (NumberFormatException e) {
				throw new ProcessingEmailException(e);
			} catch (IOException e) {
				throw new ProcessingEmailException(e);
			} catch (MailException e) {
				throw new ProcessingEmailException(e);
			} catch (DaoException e) {
				throw new ProcessingEmailException(e);
			} catch (LocatorClientException e) {
				throw new ProcessingEmailException(e);
			}
		}
		
		throw new AttachementNotFoundException();
	}

	@Override
	public void purgeFolder(BackendSession bs, String collectionPath, boolean deleteSubFolder) 
			throws NotAllowedException, CollectionNotFoundException, ProcessingEmailException {

		try {
			String wasteBasketPath = getWasteBasketPath(bs);
			if (!wasteBasketPath.equals(collectionPath)) {
				throw new NotAllowedException(
						"Only the Trash folder can be purged.");
			}
			final Integer devDbId = bs.getDevice().getDatabaseId();
			int collectionId = mappingService.getCollectionIdFor(bs.getDevice(), collectionPath);
			emailManager.purgeFolder(bs, devDbId, collectionPath, collectionId);
			if (deleteSubFolder) {
				logger.warn("deleteSubFolder isn't implemented because opush doesn't yet manage folders");
			}	
		} catch (MailException e) {
			throw new ProcessingEmailException(e);
		} catch (DaoException e) {
			throw new ProcessingEmailException(e);
		} catch (LocatorClientException e) {
			throw new ProcessingEmailException(e);
		}
	}
	
	@Override
	public Long getEmailUidFromServerId(String serverId){
		return mappingService.getItemIdFor(serverId).longValue();
	}


	/**
	 *  obm:\\adrien@test.tlse.lng\email\INBOX
	 *	obm:\\adrien@test.tlse.lng\email\Drafts
	 *	obm:\\adrien@test.tlse.lng\email\Sent
	 *	obm:\\adrien@test.tlse.lng\email\Trash
	 * @param collectionPath
	 * @return
	 * @throws FolderTypeNotFoundException 
	 */
	@Override
	public FolderType getFolderType(String collectionPath) throws FolderTypeNotFoundException {
		if (collectionPath != null) {
			if(collectionPath.contains(EmailConfiguration.IMAP_INBOX_NAME)){
				return FolderType.DEFAULT_INBOX_FOLDER;
			} 
			if(collectionPath.contains(EmailConfiguration.IMAP_DRAFTS_NAME)){
				return FolderType.DEFAULT_DRAFTS_FOLDERS;
			}
			if(collectionPath.contains(EmailConfiguration.IMAP_SENT_NAME)){
				return FolderType.DEFAULT_SENT_EMAIL_FOLDER;
			}
			if(collectionPath.contains(EmailConfiguration.IMAP_TRASH_NAME)){
				return FolderType.DEFAULT_DELETED_ITEMS_FOLDERS;
			}
		}
		throw new FolderTypeNotFoundException("The collection's path["+collectionPath+"] is invalid");
	}
	
}
