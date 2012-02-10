package fr.aliasource.funambol.engine.source;

import java.sql.Timestamp;
import java.util.List;

import org.obm.sync.client.calendar.CalendarClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSourceException;
import com.google.inject.Injector;

import fr.aliasource.funambol.ConvertionException;
import fr.aliasource.funambol.OBMException;
import fr.aliasource.funambol.ObmFunambolGuiceInjector;
import fr.aliasource.obm.items.converter.ObmEventConverter;
import fr.aliasource.obm.items.manager.CalendarManager;

public class CalendarSyncSource extends ObmSyncSource {

	private static final Logger logger = LoggerFactory.getLogger(CalendarSyncSource.class);
	
	private CalendarClient calendarClient;
	private ObmEventConverter obmEventConverter;
	private CalendarManager manager;
	
	public CalendarSyncSource(){
		super();
		Injector injector = ObmFunambolGuiceInjector.getInjector();
		calendarClient = injector.getProvider(CalendarClient.class).get();
		obmEventConverter = injector.getProvider(ObmEventConverter.class).get();
	}

	@Override
	public void beginSync(SyncContext context) throws SyncSourceException {
		logger.info("Begin an OBM-Funambol Calendar sync");
		logger.info("context.getSourceQuery():" + context);
		
		manager = new CalendarManager(loginService, calendarClient, obmEventConverter);
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

	@Override
	public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {

		logger.info("addSyncItem(" + principal + " , "
				+ syncItem.getKey().getKeyAsString() + ")");
		com.funambol.common.pim.calendar.Calendar created = null;
		try {
			com.funambol.common.pim.calendar.Calendar calendar = syncItemConverter.getFunambolCalendarFromSyncItem(syncItem, getSourceType(), deviceTimezone, deviceCharset);

			if (calendar != null) {
				created = manager.addItem(calendar);
			}
			
			if (created == null) {
				logger.warn("Sending faked syncitem to PDA, we skipped this event");
				syncItem.setState(SyncItemState.SYNCHRONIZED);
				return syncItem;
			} else {
				logger.info(" created with id : "
						+ created.getCalendarContent().getUid()
								.getPropertyValueAsString());
				return syncItemConverter.getSyncItemFromFunambolCalendar(this, created, SyncItemState.SYNCHRONIZED, getSourceType(), deviceTimezone, deviceCharset, isEncode());
			}
			
		} catch (OBMException e) {
			logger.error(e.getMessage(), e);
			throw new SyncSourceException(e);
		} catch (ConvertionException e) {
			logger.error(e.getMessage(), e);
			throw new SyncSourceException(e);
		}
	}

	@Override
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

	@Override
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
	
	@Override
	public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {

		logger.info("getNewSyncItemKeys(" + principal + " , " + since + " , "
				+ until + ") => null");

		return new SyncItemKey[0];
	}

	@Override
	public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
			throws SyncSourceException {

		logger.info("getSyncItemKeysFromTwin(" + principal + ")");
		List<String> keys = null;
		try {
			syncItem.getKey().setKeyValue("");
			Calendar event = syncItemConverter.getFunambolCalendarFromSyncItem(syncItem, getSourceType(), deviceTimezone, deviceCharset);

			if (event != null) {
				keys = manager.getEventTwinKeys(event);
			}
		} catch (OBMException e) {
			logger.error(e.getMessage(), e);
			throw new SyncSourceException(e);
		} catch (ConvertionException e) {
			logger.error(e.getMessage(), e);
			throw new SyncSourceException(e);
		}
		SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);

		logger.info(" returning " + ret.length + " key(s)");
		return ret;
	}

	@Override
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

	@Override
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

	@Override
	public SyncItem updateSyncItem(SyncItem syncItem)
			throws SyncSourceException {

		logger.info("updateSyncItem(" + principal + " , "
				+ syncItem.getKey().getKeyAsString() + ")");
		Calendar event = null;
		try {
			Calendar calendar = syncItemConverter.getFunambolCalendarFromSyncItem(syncItem, getSourceType(), deviceTimezone, deviceCharset);

			event = manager.updateItem(calendar);
			if (event == null) {
				logger.warn("Sending faked syncitem to PDA, we skipped this event");
				syncItem.setState(SyncItemState.SYNCHRONIZED);
				return syncItem;
			}
			return syncItemConverter.getSyncItemFromFunambolCalendar(this, event, SyncItemState.SYNCHRONIZED, getSourceType(), deviceTimezone, deviceCharset, isEncode());
		} catch (OBMException e) {
			logger.error(e.getMessage(), e);
			throw new SyncSourceException(e);
		} catch (ConvertionException e) {
			logger.error(e.getMessage(), e);
			throw new SyncSourceException(e);
		}
	}

	@Override
	public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
			throws SyncSourceException {

		logger
				.info("getSyncItemFromId(" + principal + ", " + syncItemKey
						+ ")");

		String key = syncItemKey.getKeyAsString();

		Calendar calendar = null;
		try {
			calendar = manager.getItemFromId(key);
			SyncItem ret = syncItemConverter.getSyncItemFromFunambolCalendar(this, calendar, SyncItemState.UNKNOWN, getSourceType(), deviceTimezone, key, isEncode());
			return ret;
		} catch (OBMException e) {
			logger.error(e.getMessage(), e);
			throw new SyncSourceException(e);
		} catch (ConvertionException e) {
			logger.error(e.getMessage(), e);
			throw new SyncSourceException(e);
		}
	}

	@Override
	public void endSync() throws SyncSourceException {
		manager.logout();
		super.endSync();
	}
}