/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 2.0
 *
 * The contents of this file are subject to the GNU General Public
 * License Version 2 or later (the "GPL").
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is
 *   MiniG.org project members
 *
 * ***** END LICENSE BLOCK ***** */

package org.obm.push.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.MimeEntityConfig;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.minig.imap.Address;
import org.minig.imap.Envelope;
import org.minig.imap.Flag;
import org.minig.imap.FlagsList;
import org.minig.imap.StoreClient;
import org.minig.imap.command.ImapReturn;
import org.minig.imap.mime.IMimePart;
import org.minig.imap.mime.MimeMessage;
import org.minig.mime.QuotedPrintableDecoderInputStream;
import org.obm.mail.conversation.MailBody;
import org.obm.mail.conversation.MailMessage;
import org.obm.mail.conversation.MessageId;
import org.obm.mail.imap.StoreException;
import org.obm.mail.message.MailMessageAttachment;
import org.obm.mail.message.MailMessageInvitation;
import org.obm.mail.message.MessageLoader;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.MSAddress;
import org.obm.push.bean.MSAttachement;
import org.obm.push.bean.MSEmail;
import org.obm.push.bean.MSEmailBody;
import org.obm.push.bean.MSEmailBodyType;
import org.obm.push.bean.MSEvent;
import org.obm.push.bean.MessageClass;
import org.obm.push.bean.MethodAttachment;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.UnfetchableMailException;
import org.obm.push.impl.ObmSyncBackend;
import org.obm.push.service.EventService;
import org.obm.push.utils.FileUtils;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.calendar.Event;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.services.ICalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

/**
 * Creates a {@link MailMessage} from a {@link MessageId}.
 */
public class MailMessageLoader {

	private static final Logger logger = LoggerFactory
			.getLogger(MailMessageLoader.class);
	
	private final ICalendar calendarClient;
	private final List<String> htmlMimeSubtypePriority;
	private final StoreClient storeClient;
	private final EventService eventService;
	private final LoginService login;
	
	public MailMessageLoader(final StoreClient store, final ICalendar calendarClient, 
			EventService eventService, LoginService login) {
		this.storeClient = store;
		this.calendarClient = calendarClient;
		this.eventService = eventService;
		this.htmlMimeSubtypePriority = Arrays.asList("html", "plain");
		this.login = login;
	}

	public MSEmail fetch(final Integer collectionId, final long messageId, final BackendSession bs) throws UnfetchableMailException {
		try {
			final List<Long> messageIdAsList = Arrays.asList(messageId);
			final Collection<ImapReturn<Envelope>> envelopes = storeClient.uidFetchEnvelope(messageIdAsList);
			Envelope toFetchEnvelope = getImapReturnValue(envelopes);

			final MimeMessage mimeMessage = getFirstMimeMessage(messageIdAsList);
			final MessageFetcherImpl messageFetcherImpl = new MessageFetcherImpl(storeClient);
			final MessageLoader helper = new MessageLoader(messageFetcherImpl, htmlMimeSubtypePriority, false, mimeMessage);
			MailMessage message = helper.fetch();
			MSEmail msEmail = convertMailMessageToMSEmail(message, bs, mimeMessage.getUid(), collectionId, messageId);
			setMsEmailFlags(msEmail, messageIdAsList);
			fetchMimeData(msEmail, messageId);
			msEmail.setSmtpId(toFetchEnvelope.getMessageId());
			return msEmail;
		} catch (IOException e) {
			throw new UnfetchableMailException(e);
		} catch (StoreException e) {
			throw new UnfetchableMailException(e);
		} catch (DaoException e) {
			throw new UnfetchableMailException(e);
		} catch (MimeException e) {
			throw new UnfetchableMailException(e);
		}
	}
	
	private <T> T getImapReturnValue(Collection<ImapReturn<T>> elements) throws UnfetchableMailException {
		try {
			ImapReturn<T> element = Iterables.getOnlyElement(elements);
			if (element.isError()) {
				throw element.getError().getCause();
			}
			return element.getValue();
		} catch (IllegalArgumentException e) {
			throw new UnfetchableMailException(e);
		} catch (NoSuchElementException e) {
			throw new UnfetchableMailException(e);
		} catch (Exception e) {
			throw new UnfetchableMailException(e);
		}
	}
	
	private MimeMessage getFirstMimeMessage(final List<Long> messageIdAsList) throws UnfetchableMailException {
		final Collection<ImapReturn<MimeMessage>> mts = storeClient.uidFetchBodyStructure(messageIdAsList);
		final MimeMessage tree = getImapReturnValue(mts);
		return tree;
	}
	
	private void setMsEmailFlags(final MSEmail msEmail, final List<Long> messageIdAsList) throws UnfetchableMailException {
		final Collection<ImapReturn<FlagsList>> fl = storeClient.uidFetchFlags(messageIdAsList);
		FlagsList flags = getImapReturnValue(fl);
		msEmail.setRead(flags.contains(Flag.SEEN));
		msEmail.setStarred(flags.contains(Flag.FLAGGED));
		msEmail.setAnswered(flags.contains(Flag.ANSWERED));
	}
	
	private void fetchMimeData(final MSEmail mm, final long messageId) throws MimeException, IOException {
		final InputStream mimeData = storeClient.uidFetchMessage(messageId);

		final SendEmailHandler handler = new SendEmailHandler("");
		final MimeEntityConfig config = new MimeEntityConfig();
		config.setMaxContentLen(Integer.MAX_VALUE);
		config.setMaxLineLen(Integer.MAX_VALUE);
		final MimeStreamParser parser = new MimeStreamParser(config);
		parser.setContentHandler(handler);
		parser.parse(mimeData);
		mm.setMimeData(handler.getMessage());
	}

	private MSEmail convertMailMessageToMSEmail(final MailMessage mailMessage, final BackendSession bs, 
			final long uid, final Integer collectionId, long messageId) throws IOException, DaoException, UnfetchableMailException {
		
		final MSEmail msEmail = new MSEmail();
		msEmail.setSubject(mailMessage.getSubject());
		msEmail.setBody(convertMailBodyToMSEmailBody(mailMessage.getBody()));
		msEmail.setFrom(convertAdressToMSAddress(mailMessage.getSender()));
		msEmail.setDate(mailMessage.getDate());
		msEmail.setHeaders(mailMessage.getHeaders());
		msEmail.setForwardMessage(convertAllMailMessageToMSEmail(mailMessage.getForwardMessage(), bs, uid, collectionId, messageId));
		msEmail.setAttachements(convertMailMessageAttachmentToMSAttachment(mailMessage, uid, collectionId, messageId));	
		msEmail.setUid(mailMessage.getUid());
		
		msEmail.setTo(convertAllAdressToMSAddress(mailMessage.getTo()));
		msEmail.setBcc(convertAllAdressToMSAddress(mailMessage.getBcc()));
		msEmail.setCc(convertAllAdressToMSAddress(mailMessage.getCc()));
		
		if (this.calendarClient != null && mailMessage.getInvitation() != null) {
			setInvitation(msEmail, bs, mailMessage.getInvitation(), uid, messageId);
		}
		
		return msEmail;
	}

	private void setInvitation(final MSEmail msEmail, final BackendSession bs, final MailMessageInvitation mailMessageInvitation, 
			final long uid, final long messageId) throws IOException, DaoException, UnfetchableMailException {			
		final IMimePart mimePart = mailMessageInvitation.getPart();
		final InputStream inputStreamInvitation = extractInputStreamInvitation(mimePart, uid, messageId);
		final MSEvent event = getInvitation(bs, inputStreamInvitation);
		if (mimePart.isInvitation()) {
			msEmail.setInvitation(event, MessageClass.ScheduleMeetingRequest);
		} else if (mimePart.isCancelInvitation()) {
			msEmail.setInvitation(event, MessageClass.ScheduleMeetingCanceled);
		}
	}
	
	private InputStream extractInputStreamInvitation(final IMimePart mp, final long uid, final long messageId) throws IOException {
		byte[] data = null;
		final InputStream part = storeClient.uidFetchPart(uid, mp.getAddress().toString());
		data = extractPartData(mp, part, messageId);
		if (data != null) {
			return new ByteArrayInputStream(data);
		}
		return null;
	}
	
	private MSEvent getInvitation(BackendSession bs, InputStream invitation) throws IOException, DaoException, UnfetchableMailException {
		final String ics = FileUtils.streamString(invitation, true);
		if (ics != null && !"".equals(ics) && ics.startsWith("BEGIN")) {
			List<Event> obmEvents = null; 

			final AccessToken at = login.login(bs.getUser().getLoginAtDomain(),
					bs.getPassword(), ObmSyncBackend.OBM_SYNC_ORIGIN);
			try {
				obmEvents = calendarClient.parseICS(at, ics);
			} catch (Exception e) {
				throw new UnfetchableMailException(e);
			} finally {
				login.logout(at);
			}
			if (obmEvents.size() > 0) {
				final Event icsEvent = obmEvents.get(0);
				return eventService.convertEventToMSEvent(bs, icsEvent);
			}
		}
		return null;
	}

	private Set<MSAttachement> convertMailMessageAttachmentToMSAttachment(MailMessage mailMessage, long uid, 
			Integer collectionId, long messageId) throws IOException {
		
		Set<MSAttachement> msAttachements = new HashSet<MSAttachement>();
		for (MailMessageAttachment mailMessageAttachment: mailMessage.getAttachments()) {			
			
			IMimePart part = mailMessageAttachment.getPart();
			if (part != null && !part.isInvitation()) {
				MSAttachement extractAttachments = extractAttachmentData(part, uid, collectionId, messageId);
				if (isNotICSAttachments(extractAttachments)) {
					msAttachements.add(extractAttachments);
				}
			}
			
		}
		return msAttachements;
	}

	private boolean isNotICSAttachments(MSAttachement msAttachment) {
		if (msAttachment != null) {
			String displayName = msAttachment.getDisplayName();
			if (displayName != null && !displayName.endsWith(".ics")) {
				return true;
			}
		}
		return false;
	}
	
	private Set<MSEmail> convertAllMailMessageToMSEmail(final Set<MailMessage> set, final BackendSession bs, 
			final long uid, final Integer collectionId, final long messageId) 
					throws IOException, DaoException, UnfetchableMailException {
		final Set<MSEmail> msEmails = new HashSet<MSEmail>();
		for (final MailMessage mailMessage: set) {
			msEmails.add(convertMailMessageToMSEmail(mailMessage, bs, uid, collectionId, messageId));
		}
		return msEmails;
	}

	private MSAddress convertAdressToMSAddress(Address adress) {
		if (adress != null) {
			return new MSAddress(adress.getDisplayName(), adress.getMail());
		}
		return null;
	}
	
	private List<MSAddress> convertAllAdressToMSAddress(List<Address> adresses) {
		List<MSAddress> msAdresses = new ArrayList<MSAddress>();
		if (adresses != null) {
			for (Address adress: adresses) {
				msAdresses.add(convertAdressToMSAddress(adress));
			}
		}
		return msAdresses;
	}

	private MSEmailBody convertMailBodyToMSEmailBody(final MailBody body) {
		final MSEmailBody emailBody = new MSEmailBody();
		for (final String format: body.availableFormats()) {
			final String value = body.getValue(format);
			emailBody.addConverted(MSEmailBodyType.getValueOf(format), value);
		}		
		return emailBody;
	}

	private byte[] extractPartData(final IMimePart mp, final InputStream bodyText, final long messageId) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileUtils.transfer(bodyText, out, true);
		byte[] rawData = out.toByteArray();
		if (logger.isDebugEnabled()) {
			logger.debug("[" + messageId + "] transfer encoding for part: "
					+ mp.getContentTransfertEncoding() + " "
					+ mp.getFullMimeType());
		}
		if ("QUOTED-PRINTABLE".equals(mp.getContentTransfertEncoding())) {
			out = new ByteArrayOutputStream();
			InputStream in = new QuotedPrintableDecoderInputStream(
					new ByteArrayInputStream(rawData));
			FileUtils.transfer(in, out, true);
			rawData = out.toByteArray();
		} else if ("BASE64".equals(mp.getContentTransfertEncoding())) {
			rawData = new Base64().decode(rawData);
		}
		return rawData;
	}
	
	private MSAttachement extractAttachmentData(final IMimePart mp, final long uid, 
			final Integer collectionId, final long messageId) throws IOException {
			
		if (mp.getName() != null || mp.getContentId() != null) {
			byte[] data = null;
			final InputStream part = storeClient.uidFetchPart(uid, mp.getAddress().toString());
			data = extractPartData(mp, part, messageId);

			final String id = AttachmentHelper.getAttachmentId(collectionId.toString(), String.valueOf(messageId), 
					mp.getAddress().toString(), mp.getFullMimeType(), mp.getContentTransfertEncoding());

			String name = mp.getName();
			if (name == null) {
				name = mp.getContentId();
			}

			MSAttachement att = new MSAttachement();
			att.setFileReference(id);
			att.setMethod(MethodAttachment.NormalAttachment);
			att.setEstimatedDataSize(data.length);
			att.setDisplayName(name);
			return att;
		}			

		return null;
	}
	
}
