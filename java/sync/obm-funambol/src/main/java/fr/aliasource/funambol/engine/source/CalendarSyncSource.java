package fr.aliasource.funambol.engine.source;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.List;

import org.obm.sync.client.calendar.CalendarClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.converter.ConverterException;
import com.funambol.common.pim.converter.VCalendarConverter;
import com.funambol.common.pim.converter.VComponentWriter;
import com.funambol.common.pim.icalendar.ICalendarParser;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.xvcalendar.XVCalendarParser;
import com.funambol.framework.engine.InMemorySyncItem;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.tools.Base64;
import com.google.inject.Injector;

import fr.aliasource.funambol.OBMException;
import fr.aliasource.funambol.ObmFunambolGuiceInjector;
import fr.aliasource.funambol.utils.FunisHelper;
import fr.aliasource.funambol.utils.Helper;
import fr.aliasource.obm.items.converter.ObmEventConverter;
import fr.aliasource.obm.items.manager.CalendarManager;

public class CalendarSyncSource extends ObmSyncSource {

	private static final long serialVersionUID = 8820543271150832304L;

	private static final Logger logger = LoggerFactory.getLogger(CalendarSyncSource.class);
	
	private CalendarClient binding;
	private ObmEventConverter obmEventConverter;
	private CalendarManager manager;
	
	public CalendarSyncSource(){
		super();
		Injector injector = ObmFunambolGuiceInjector.getInjector();
		binding = injector.getProvider(CalendarClient.class).get();
		obmEventConverter = injector.getProvider(ObmEventConverter.class).get();

	}

	public void beginSync(SyncContext context) throws SyncSourceException {
		logger.info("Begin an OBM-Funambol Calendar sync");
		logger.info("context.getSourceQuery():" + context);
		
		manager = new CalendarManager(binding, obmEventConverter);
		manager.initSyncRange(context.getSourceQuery());
		
		try {
			manager.logIn(context.getPrincipal().getUser().getUsername(),
					context.getPrincipal().getUser().getPassword());
			String calendar = context.getPrincipal().getUser().getUsername()
					.split("@")[0];
			manager.setCalendar(calendar);
			manager.initUserEmail();

			super.beginSync(context);
		} catch (Throwable e) {
			logger.error("pb in begin sync", e);
			throw new SyncSourceException(e);
		}
		logger.info("beginSync end.");
	}

	/**
	 * @see SyncSource
	 */
	public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {

		logger.info("addSyncItem(" + principal + " , "
				+ syncItem.getKey().getKeyAsString() + ")");
		com.funambol.common.pim.calendar.Calendar created = null;
		try {
			com.funambol.common.pim.calendar.Calendar calendar = getFoundationFromSyncItem(syncItem);

			if (calendar != null) {
				created = manager.addItem(calendar);
			}
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}

		if (created == null) {
			logger.warn("Sending faked syncitem to PDA, we skipped this event");
			syncItem.setState(SyncItemState.SYNCHRONIZED);
			return syncItem;
		} else {
			logger.info(" created with id : "
					+ created.getCalendarContent().getUid()
							.getPropertyValueAsString());
			return getSyncItemFromFoundation(created,
					SyncItemState.SYNCHRONIZED);
		}
	}

	/*
	 * @see SyncSource
	 */
	public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {

		logger.info("getAllSyncItemKeys(" + principal + ")");

		List<String> keys = null;
		try {
			keys = manager.getAllItemKeys();
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
		SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);

		logger.info(" returning " + ret.length + " key(s)");
		return ret;
	}

	/*
	 * @see SyncSource
	 */
	public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {

		logger.info("getDeletedSyncItemKeys(" + principal + " , " + since
				+ " , " + until + ")");
		List<String> keys = null;

		try {
			keys = manager.getDeletedItemKeys(since);

		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
		SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);

		logger.info(" returning " + ret.length + " key(s)");

		return ret;
	}

	/*
	 * @see SyncSource
	 */
	public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {

		logger.info("getNewSyncItemKeys(" + principal + " , " + since + " , "
				+ until + ") => null");

		return new SyncItemKey[0];
	}

	/**
	 * @see SyncSource
	 */
	public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
			throws SyncSourceException {

		logger.info("getSyncItemKeysFromTwin(" + principal + ")");
		List<String> keys = null;
		try {
			syncItem.getKey().setKeyValue("");
			Calendar event = getFoundationFromSyncItem(syncItem);

			if (event != null) {
				keys = manager.getEventTwinKeys(event);
			}
		} catch (OBMException e) {
			logger.error(e.getMessage(), e);
			throw new SyncSourceException(e);
		}
		SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);

		logger.info(" returning " + ret.length + " key(s)");
		return ret;
	}

	/*
	 * @see SyncSource
	 */
	public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {

		logger.info("getUpdatedSyncItemKeys(" + principal + " , " + since
				+ " , " + until + ")");
		List<String> keys = null;

		try {
			keys = manager.getUpdatedItemKeys(since);

		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
		SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);

		logger.info(" returning " + ret.length + " key(s)");

		return ret;
	}

	public void removeSyncItem(SyncItemKey syncItemKey, Timestamp time,
			boolean softDelete) throws SyncSourceException {

		logger.info("removeSyncItem(" + principal + " , " + syncItemKey + " , "
				+ time + ")");

		try {
			manager.removeItem(syncItemKey.getKeyAsString());
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
	}

	/*
	 * @see SyncSource
	 */
	public SyncItem updateSyncItem(SyncItem syncItem)
			throws SyncSourceException {

		logger.info("updateSyncItem(" + principal + " , "
				+ syncItem.getKey().getKeyAsString() + ")");
		Calendar event = null;
		try {
			Calendar calendar = getFoundationFromSyncItem(syncItem);

			event = manager.updateItem(calendar);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}

		if (event == null) {
			logger.warn("Sending faked syncitem to PDA, we skipped this event");
			syncItem.setState(SyncItemState.SYNCHRONIZED);
			return syncItem;
		}
		return getSyncItemFromFoundation(event, SyncItemState.SYNCHRONIZED);
	}

	/*
	 * @see SyncSource
	 */
	public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
			throws SyncSourceException {

		logger
				.info("getSyncItemFromId(" + principal + ", " + syncItemKey
						+ ")");

		String key = syncItemKey.getKeyAsString();

		Calendar calendar = null;
		try {
			calendar = manager.getItemFromId(key);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
		SyncItem ret = getSyncItemFromFoundation(calendar,
				SyncItemState.UNKNOWN);

		return ret;
	}

	// -------------------- Private methods ----------------------

	/**
	 * Get Data from com.funambol.foundation.pdi.event.Calendar converting the
	 * Calendar object into a v-card item
	 * 
	 * @param calendar
	 * @return
	 * @throws SyncSourceException
	 */
	private String getICalFromFoundationCalendar(Calendar calendar)
			throws SyncSourceException {

		String ical = null;

		// dateAsUTC(calendar);

		try {
			VCalendarConverter c2vcal = new VCalendarConverter(deviceTimezone,
					deviceCharset, false);
			VCalendar cal = c2vcal.calendar2vcalendar(calendar, true);
			VComponentWriter writer = new VComponentWriter(
					VComponentWriter.NO_FOLDING);
			ical = writer.toString(cal);
		} catch (ConverterException ex) {
			throw new SyncSourceException("Error converting calendar in iCal",
					ex);
		}
		return ical;
	}

	/**
	 * Get Data from ICal message converting the ical item into a Calendar
	 * object
	 * 
	 * the calendar object is a com.funambol.foundation.pim.calendar.Calendar
	 * 
	 * @param content
	 *            String
	 * @return Calendar
	 * @throws OBMException
	 */
	private Calendar getFoundationCalendarFromICal(String content)
			throws OBMException {
		logger.info("pda sent:\n" + content);

		String toParse = content;
		toParse = toParse.replace("encoding", "ENCODING");
		toParse = toParse.replace("PRINTABLE:", "PRINTABLE;CHARSET=UTF-8:");
		toParse = FunisHelper.removeQuotedPrintableFromVCalString(toParse);
		ByteArrayInputStream buffer = new ByteArrayInputStream(toParse
				.getBytes());

		try {
			VCalendar vcal = null;
			if (toParse.contains("VERSION:1.0")) {
				logger.info("Parsing version 1.0 as xvcalendar");
//				XVCalendarParser parser = new XVCalendarParser(buffer,
//						deviceCharset);
				XVCalendarParser parser = new XVCalendarParser(buffer);
				vcal = parser.XVCalendar();
			} else {
				logger.info("Parsing version 2.0 as icalendar");
				ICalendarParser parser = new ICalendarParser(buffer);
				vcal = parser.ICalendar();
			}
			VCalendarConverter vconvert = new VCalendarConverter(deviceTimezone,
					deviceCharset, false);

			Calendar ret = vconvert.vcalendar2calendar(vcal);
			return ret;
		} catch (Exception e) {
			throw new OBMException("Error converting from ical ", e);
		}

	}

	private Calendar getFoundationFromSyncItem(SyncItem item)
			throws OBMException {
		Calendar foundationCalendar = null;

		String content = Helper.getContentOfSyncItem(item);
		logger.info("foundFromSync:\n" + content);
		logger.info(" ===> syncItemKey: " + item.getKey());

		if (content == null || content.trim().length() == 0) {
			return null;
		}

		if (MSG_TYPE_ICAL.equals(getSourceType())) {
			foundationCalendar = getFoundationCalendarFromICal(content);
			logger.info("calContent.uid: "
					+ foundationCalendar.getCalendarContent().getUid());

			foundationCalendar.getCalendarContent().setUid(
					new Property(item.getKey().getKeyAsString()));
		} else {
			logger.error("Only Ical is supported");
		}

		return foundationCalendar;
	}

	private SyncItem getSyncItemFromFoundation(Calendar calendar, char status)
			throws SyncSourceException {
		SyncItem syncItem = null;

		String content = null;

		if (MSG_TYPE_ICAL.equals(getSourceType())) {
			content = getICalFromFoundationCalendar(calendar);
			syncItem = new InMemorySyncItem(this, calendar.getCalendarContent()
					.getUid().getPropertyValueAsString(), status);

			logger.info("sending syncitem to pda:\n" + content);
			if (isEncode()) {
				syncItem.setContent(Base64.encode(content.getBytes()));
				syncItem.setType(getSourceType());
				syncItem.setFormat("b64");
			} else {
				syncItem.setContent(content.getBytes());
				syncItem.setType(getSourceType());
			}
		} else {
			logger.error("Only Ical is supported");
		}

		return syncItem;
	}

	@Override
	public void endSync() throws SyncSourceException {
		manager.logout();
		super.endSync();
	}
}