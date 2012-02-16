package org.obm.funambol.converter;

import java.io.ByteArrayInputStream;

import org.apache.commons.lang.StringUtils;
import org.obm.funambol.exception.ConvertionException;
import org.obm.funambol.model.SyncSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.converter.ContactToVcard;
import com.funambol.common.pim.converter.ConverterException;
import com.funambol.common.pim.converter.VCalendarConverter;
import com.funambol.common.pim.converter.VComponentWriter;
import com.funambol.common.pim.icalendar.ICalendarParser;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.vcard.VcardParser;
import com.funambol.common.pim.xvcalendar.ParseException;
import com.funambol.common.pim.xvcalendar.XVCalendarParser;
import com.funambol.framework.engine.InMemorySyncItem;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.tools.Base64;
import com.google.inject.Singleton;


@Singleton
public class SyncItemConverterImpl implements ISyncItemConverter {

	public static final String MSG_TYPE_VCARD = "text/x-vcard";
	public static final String MSG_TYPE_ICAL = "text/x-vcalendar";
	
	private static final Logger logger = LoggerFactory.getLogger(SyncItemConverterImpl.class);
	
	@Override
	public Calendar getFunambolCalendarFromSyncItem(SyncSession syncSession, SyncItem item, String sourceType)
			throws ConvertionException {
		String content = getCheckContentFromSyncItem(item, MSG_TYPE_ICAL, sourceType);
		Calendar foundationCalendar = getFoundationCalendarFromICal(content, syncSession);
		foundationCalendar.getCalendarContent().setUid(new Property(item.getKey().getKeyAsString()));
		return foundationCalendar;
	}
	
	@Override
	public Contact getFunambolContactFromSyncItem(SyncSession syncSession, SyncItem item, String sourceType)
			throws ConvertionException {
		String content = getCheckContentFromSyncItem(item, MSG_TYPE_VCARD, sourceType);
		Contact	contact = getContactFromVCard(content, syncSession);
		if(StringUtils.trimToNull(contact.getUid()) == null){
			contact.setUid(item.getKey().getKeyAsString());
		}
		return contact;
	}
	
	@Override
	public SyncItem getSyncItemFromFunambolCalendar(SyncSession syncSession, SyncSource syncSource, Calendar calendar, char status,  String sourceType)
			throws ConvertionException {
		
		checkSourceType(MSG_TYPE_ICAL, sourceType);

		String	content = getICalFromFoundationCalendar(calendar, syncSession);
		logger.info("sending syncitem to pda:\n" + content);
		
		return createSyncItem(syncSource, calendar.getCalendarContent()
				.getUid().getPropertyValueAsString(), status, content, sourceType, syncSession.isEncode());
	}

	@Override
	public SyncItem getSyncItemFromFunambolContact(SyncSession syncSession, SyncSource syncSource, Contact contact, char status, String sourceType)
			throws ConvertionException {
		checkSourceType(MSG_TYPE_VCARD, sourceType);
		
		String content = getVCardFromContact(contact, syncSession);
		logger.info("vcardFromFoundation:\n"+content);
		
		return createSyncItem(syncSource, contact.getUid(), status, content, sourceType, syncSession.isEncode());
	}
	
	private Calendar getFoundationCalendarFromICal(String content, SyncSession session) throws ConvertionException {
		logger.info("pda sent:\n" + content);

		String toParse = content;

		try {
			ByteArrayInputStream buffer = new ByteArrayInputStream(toParse
					.getBytes());
			VCalendar vcal = null;
			if (toParse.contains("VERSION:1.0")) {
				logger.info("Parsing version 1.0 as xvcalendar");
				XVCalendarParser parser = new XVCalendarParser(buffer);
				vcal = parser.XVCalendar();
			} else {
				logger.info("Parsing version 2.0 as icalendar");
				ICalendarParser parser = new ICalendarParser(buffer);
				vcal = parser.ICalendar();
			}
			VCalendarConverter vconvert = new VCalendarConverter(session.getDeviceTimeZone(),
					session.getDeviceCharset(), false);

			Calendar ret = vconvert.vcalendar2calendar(vcal);
			return ret;
		} catch (ParseException e) {
			throw new ConvertionException("Error converting from ical ", e);
		} catch (com.funambol.common.pim.icalendar.ParseException e) {
			throw new ConvertionException("Error converting from ical ", e);
		} catch (ConverterException e) {
			throw new ConvertionException("Error converting from ical ", e);
		}
	}
	
	private String getICalFromFoundationCalendar(Calendar calendar, SyncSession syncSession)
			throws ConvertionException {

		try {
			VCalendarConverter c2vcal = new VCalendarConverter(syncSession.getDeviceTimeZone(),
					syncSession.getDeviceCharset(), false);
			VCalendar cal = c2vcal.calendar2vcalendar(calendar, true);
			VComponentWriter writer = new VComponentWriter(
					VComponentWriter.NO_FOLDING);
			String ical = writer.toString(cal);
			return ical;
		} catch (ConverterException ex) {
			throw new ConvertionException("Error converting calendar in iCal",
					ex);
		}
	
	}

	private String getVCardFromContact(Contact contact, SyncSession syncSession) throws ConvertionException {
		try {
			ContactToVcard c2vcard = new ContactToVcard(syncSession.getDeviceTimeZone(),
					syncSession.getDeviceCharset());
			String vcard = c2vcard.convert(contact);
			return vcard;
		} catch (ConverterException ex) {
			throw new ConvertionException(ex.getMessage(), ex);
		}

	}

	private Contact getContactFromVCard(String content, SyncSession syncSession) throws ConvertionException {

		try {
			Contact contact = new Contact();
			ByteArrayInputStream buffer = new ByteArrayInputStream(content.getBytes());
			if ((content.getBytes()).length > 0) {
				VcardParser parser = new VcardParser(buffer, syncSession.getDeviceTimeZone().getDisplayName(), syncSession.getDeviceCharset());
				contact = parser.vCard();
			}
			return contact;
		} catch (Throwable e) {
			logger.error("Error converting following vcard:\n " + content + "\n", e);
			throw new ConvertionException(
					"Error converting from Vcard (card dump follows):\n"
							+ content + "\n", e);
		}
	}
	
	private String getCheckContentFromSyncItem(SyncItem item,
			String msgTypeIcal, String sourceType) throws ConvertionException {
		String content = getContentOfSyncItem(item);
		
		if (StringUtils.trimToNull(content) == null) {
			throw new ConvertionException("Sync item is empty");
		}
		
		checkSourceType(msgTypeIcal, sourceType);
		
		return content;
	}

	private void checkSourceType(String expectedSourceType, String sourceType) throws ConvertionException {
		if (!expectedSourceType.equals(sourceType)) {
			throw new ConvertionException("Only "+expectedSourceType+" type is supported, no ["+sourceType+"]");
		}
		
	}

	private String getContentOfSyncItem(SyncItem item) {
		
		byte[] itemContent = item.getContent();
		String result = new String(itemContent == null ? new byte[0] : itemContent);
		
		return result.trim();
	}
	
	private SyncItem createSyncItem(SyncSource syncSource, String uid,
			char state, String content, String sourceType, boolean isEncoded) {
		SyncItem syncItem = new InMemorySyncItem(syncSource, uid, state);
		
		syncItem.setType(sourceType);
		if (isEncoded) {
			syncItem.setContent(Base64.encode(content.getBytes()));
			syncItem.setFormat("b64");
		} else {
			syncItem.setContent(content.getBytes());
		}
		return syncItem;
	}

	
}
