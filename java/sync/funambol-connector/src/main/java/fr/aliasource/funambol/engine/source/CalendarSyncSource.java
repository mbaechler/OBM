package fr.aliasource.funambol.engine.source;

import java.sql.Timestamp;
import java.util.List;

import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.AuthFault;
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
import fr.aliasource.obm.items.manager.SyncSession;
import fr.aliasource.obm.items.manager.ICalendarService;

public class CalendarSyncSource extends ObmSyncSource {

	private static final Logger logger = LoggerFactory.getLogger(CalendarSyncSource.class);
	
	private SyncSession syncBean;
	private ICalendarService calendarService;
	
	public CalendarSyncSource(){
		super();
		Injector injector = ObmFunambolGuiceInjector.getInjector();
		calendarService = injector.getInstance(ICalendarService.class);
	}

	@Override
	public void beginSync(SyncContext context) throws SyncSourceException {
		logger.info("Begin an OBM-Funambol Calendar sync");
		logger.info("context.getSourceQuery():" + context);
		
		try {
			this.syncBean = new SyncSession(context);
			AccessToken token = loginService.login(this.syncBean.getUserLogin(), this.syncBean.getUserPassword());
			this.syncBean.setObmAccessToken(token);
			super.beginSync(context);
		} catch (AuthFault e) {
			logger.error("pb in begin sync", e);
			throw new SyncSourceException(e);
		}
		logger.info("beginSync end.");
	}

	@Override
	public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {

		logger.info("addSyncItem(" + syncBean.getUserLogin() + " , "
				+ syncItem.getKey().getKeyAsString() + ")");
		com.funambol.common.pim.calendar.Calendar created = null;
		try {
			com.funambol.common.pim.calendar.Calendar calendar = syncItemConverter.getFunambolCalendarFromSyncItem(syncItem, getSourceType(), deviceTimezone, deviceCharset);

			if (calendar != null) {
				created = calendarService.addItem(syncBean, calendar);
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
		try {
			logger.info("getAllSyncItemKeys(" + syncBean.getUserLogin() + ")");
			List<String> keys = calendarService.getAllItemKeys(syncBean);
			SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);

			logger.info(" returning " + ret.length + " key(s)");
			return ret;
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
	}

	@Override
	public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {
		logger.info("getDeletedSyncItemKeys(" + syncBean.getUserLogin() + " , " + since
				+ " , " + until + ")");
		try {
			List<String> keys = calendarService.getDeletedItemKeys(syncBean, since);
			SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);

			logger.info(" returning " + ret.length + " key(s)");

			return ret;
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
	}
	
	@Override
	public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {

		logger.info("getNewSyncItemKeys(" + syncBean.getUserLogin() + " , " + since + " , "
				+ until + ") => null");

		return new SyncItemKey[0];
	}

	@Override
	public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
			throws SyncSourceException {

		logger.info("getSyncItemKeysFromTwin(" + syncBean.getUserLogin() + ")");
		try {
			syncItem.getKey().setKeyValue("");
			Calendar event = syncItemConverter.getFunambolCalendarFromSyncItem(syncItem, getSourceType(), deviceTimezone, deviceCharset);
			if (event == null) {
				return new SyncItemKey[0];
			}
			List<String> keys = calendarService.getEventTwinKeys(syncBean, event);
			SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);
			logger.info(" returning " + ret.length + " key(s)");
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
	public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp until)
			throws SyncSourceException {

		try {
			logger.info("getUpdatedSyncItemKeys(" + syncBean.getUserLogin() + " , " + since
					+ " , " + until + ")");
			
			List<String> keys = calendarService.getUpdatedItemKeys(syncBean, since);
			SyncItemKey[] ret = getSyncItemKeysFromKeys(keys);

			logger.info(" returning " + ret.length + " key(s)");

			return ret;
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
	}

	@Override
	public void removeSyncItem(SyncItemKey syncItemKey, Timestamp time,
			boolean softDelete) throws SyncSourceException {
		try {
			logger.info("removeSyncItem(" + syncBean.getUserLogin() + " , " + syncItemKey + " , "
					+ time + ")");
			
			calendarService.removeItem(syncBean, syncItemKey);
		} catch (OBMException e) {
			throw new SyncSourceException(e);
		}
	}

	@Override
	public SyncItem updateSyncItem(SyncItem syncItem)
			throws SyncSourceException {

		logger.info("updateSyncItem(" + syncBean.getUserLogin() + " , "
				+ syncItem.getKey().getKeyAsString() + ")");
		Calendar event = null;
		try {
			Calendar calendar = syncItemConverter.getFunambolCalendarFromSyncItem(syncItem, getSourceType(), deviceTimezone, deviceCharset);

			event = calendarService.updateItem(syncBean, calendar);
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
		try {
			logger
			.info("getSyncItemFromId(" + syncBean.getUserLogin() + ", " + syncItemKey
					+ ")");
			
			Calendar calendar = calendarService.getItemFromId(syncBean, syncItemKey);
			SyncItem ret = syncItemConverter.getSyncItemFromFunambolCalendar(this, calendar, SyncItemState.UNKNOWN, getSourceType(), deviceTimezone, deviceCharset, isEncode());
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
		calendarService.logout(syncBean.getObmAccessToken());
		this.syncBean = null;
		super.endSync();
	}
}