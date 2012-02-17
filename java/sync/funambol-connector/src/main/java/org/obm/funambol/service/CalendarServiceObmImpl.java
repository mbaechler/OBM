package org.obm.funambol.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.obm.funambol.converter.ISyncItemKeyConverter;
import org.obm.funambol.converter.ObmEventConverter;
import org.obm.funambol.exception.ConvertionException;
import org.obm.funambol.exception.OBMException;
import org.obm.funambol.model.CalendarChanges;
import org.obm.funambol.model.SyncSession;
import org.obm.sync.auth.EventAlreadyExistException;
import org.obm.sync.auth.EventNotFoundException;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.calendar.Attendee;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventExtId;
import org.obm.sync.calendar.EventKey;
import org.obm.sync.calendar.EventObmId;
import org.obm.sync.calendar.ParticipationState;
import org.obm.sync.calendar.SyncRange;
import org.obm.sync.client.calendar.CalendarClient;
import org.obm.sync.items.EventChanges;

import com.funambol.framework.engine.SyncItemKey;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class CalendarServiceObmImpl extends ObmService implements ICalendarService {

	private final CalendarClient calendarClient;
	private final ObmEventConverter obmEventConverter;
	private final ISyncItemKeyConverter syncItemKeyConverter;

	@Inject
	private CalendarServiceObmImpl(final CalendarClient calendarClient,	final ObmEventConverter obmEventConverter, 
			final ISyncItemKeyConverter syncItemKeyConverter) {
		this.calendarClient = calendarClient;
		this.obmEventConverter = obmEventConverter;
		this.syncItemKeyConverter = syncItemKeyConverter;
	}

	@Override
	public List<SyncItemKey> getAllItemKeys(SyncSession syncBean) throws OBMException {
		CalendarChanges changes = getSync(syncBean, null);
		return syncItemKeyConverter.getSyncItemKeysFromEventExtIds(changes.getUpdated().keySet());
	}

	@Override
	public List<SyncItemKey> getDeletedItemKeys(SyncSession syncBean, Timestamp since) throws OBMException {
		CalendarChanges changes = getSync(syncBean, since);
		return syncItemKeyConverter.getSyncItemKeysFromEventExtIds(changes.getDeleted());
	}

	@Override
	public List<SyncItemKey> getUpdatedItemKeys(SyncSession syncBean, Timestamp since) throws OBMException {
		CalendarChanges changes = getSync(syncBean, since);
		return syncItemKeyConverter.getSyncItemKeysFromEventExtIds(changes.getUpdated().keySet());
	}

	@Override
	public com.funambol.common.pim.calendar.Calendar getItemFromId(SyncSession syncBean, SyncItemKey syncItemKey) throws OBMException {
		try {
			final EventExtId eventExtId = syncItemKeyConverter.getEventExtIdFromSyncItemKey(syncItemKey);
			final Event event = calendarClient.getEventFromExtId(syncBean.getObmAccessToken(), syncBean.getUserLogin(), eventExtId);
			return obmEventConverter.obmEventToFoundationCalendar(event);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(),e);
		} catch (EventNotFoundException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ConvertionException e) {
			throw new OBMException(e.getMessage(),e);
		}

	}

	@Override
	public void removeItem(SyncSession syncBean, SyncItemKey syncItemKey) throws OBMException {
		try {
			final EventExtId eventExtId = syncItemKeyConverter.getEventExtIdFromSyncItemKey(syncItemKey);
			Event event = calendarClient.getEventFromExtId(syncBean.getObmAccessToken(), syncBean.getUserLogin(), eventExtId);
			if (event == null) {
				logger.info("event removed on pda not in db: " + syncBean.getUserLogin()
						+ " / " + eventExtId.serializeToString());
				return;
			}

			if (event.getAttendees() == null
					|| event.getAttendees().size() == 1) {
				// no attendee (only the owner)
				logger.info("not a meeting, removing event");
				calendarClient.removeEventByExtId(syncBean.getObmAccessToken(), syncBean.getUserLogin(), eventExtId, event.getSequence(), false);
			} else {
				refuseEvent(syncBean, event, syncBean.getUserLogin());
			}
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		} catch (EventNotFoundException e) {
			throw new OBMException(e.getMessage());
		}
	}

	private void refuseEvent(SyncSession syncBean, Event event, String userEmail) throws ServerFault {
		logger.info("meeting removed, refusing for " + userEmail);
		for (Attendee at : event.getAttendees()) {
			if (at.getEmail().equals(userEmail)) {
				at.setState(ParticipationState.DECLINED);
				logger.info("DECLINED for email " + userEmail);
				return;
			}
		}
		calendarClient.modifyEvent(syncBean.getObmAccessToken(), userEmail, event, true, false);
	}
	
	@Override
	public com.funambol.common.pim.calendar.Calendar updateItem(SyncSession syncBean,
			com.funambol.common.pim.calendar.Calendar event)
			throws OBMException {
		try {
			Event c = calendarClient.modifyEvent(syncBean.getObmAccessToken(), syncBean.getUserLogin(),
					obmEventConverter.foundationCalendarToObmEvent(event, syncBean.getUserLogin()), false, false);
			if (c == null) {
				return null;
			} else {
				return obmEventConverter.obmEventToFoundationCalendar(c);
			}
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(), e);
		} catch (ConvertionException e) {
			throw new OBMException(e.getMessage(), e);
		}

		
	}

	@Override
	public com.funambol.common.pim.calendar.Calendar addItem(SyncSession syncBean, 
			com.funambol.common.pim.calendar.Calendar event)
			throws OBMException {
		try {
			Event forCreate = obmEventConverter.foundationCalendarToObmEvent(event, syncBean.getUserLogin());
			EventExtId ext = new EventExtId(UUID.randomUUID());
			forCreate.setExtId(ext);
			EventObmId uid = calendarClient.createEvent(syncBean.getObmAccessToken(), syncBean.getUserLogin(), forCreate, false);
			Event evt = calendarClient.getEventFromId(syncBean.getObmAccessToken(), syncBean.getUserLogin(), uid);
			return obmEventConverter.obmEventToFoundationCalendar(evt);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(), e);
		} catch (EventAlreadyExistException e) {
			throw new OBMException(e.getMessage(), e);
		} catch (EventNotFoundException e) {
			throw new OBMException(e.getMessage(), e);
		} catch (ConvertionException e) {
			throw new OBMException(e.getMessage(), e);
		}
	}
	
	@Override
	public List<SyncItemKey> getEventTwinKeys(SyncSession syncBean, com.funambol.common.pim.calendar.Calendar event)
			throws OBMException {

		try {
			Event evt = obmEventConverter.foundationCalendarToObmEvent(event, syncBean.getUserLogin());
			if (evt == null) {
				return ImmutableList.<SyncItemKey>of();
			}
			evt.setUid(null);
			List<EventKey> keyList = calendarClient.getEventTwinKeys(syncBean.getObmAccessToken(), syncBean.getUserLogin(), evt);
			List<EventExtId> extIds = transformAsEventExtIdSet(keyList);
			return syncItemKeyConverter.getSyncItemKeysFromEventExtIds(extIds);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(), e);
		} catch (ConvertionException e) {
			throw new OBMException(e.getMessage());
		}
	}

	private CalendarChanges getSync(SyncSession syncBean, Timestamp since) throws OBMException {
		try {
			Date lastSync = null;
			if (since != null) {
//				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//				cal.setTime(since);
				lastSync = since;
			}

			final SyncRange syncRange = syncBean.getSyncRange();
			final String userLogin = syncBean.getUserLogin();
			final EventChanges sync = calendarClient.getSyncInRange(syncBean.getObmAccessToken(), userLogin, lastSync, syncRange);
			
			logger.info("getSync(" + userLogin + ", " + lastSync + " (since == " + since
					+ ")) => upd: " + sync.getUpdated().length + " del: "
					+ sync.getRemoved().length);
			
			final List<Event> updated = getUpdatedEvent(sync);
			final Map<EventExtId, Event> updatedRest = transformAsFunambolUpdated(updated);
			
			final Set<EventExtId> deleted = getRemovedEvent(sync);
			
			return new CalendarChanges(updatedRest, deleted);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}
	}
	

	private List<EventExtId> transformAsEventExtIdSet(List<EventKey> keyList) {
		return Lists.transform(keyList, new Function<EventKey, EventExtId>() {
			@Override
			public EventExtId apply(EventKey input) {
				return input.getEventExtId();
			}
		});
	}

	private Map<EventExtId, Event> transformAsFunambolUpdated(List<Event> updated) {
		ImmutableMap.Builder<EventExtId, Event> mapBuilder = ImmutableMap.builder();
		for (Event e : updated) {
			mapBuilder.put(e.getExtId(), e);
		}
		return mapBuilder.build();
	}

	private Set<EventExtId> getRemovedEvent(EventChanges sync) {
		return sync.getRemovedExtIds() != null ? ImmutableSet.<EventExtId>copyOf(sync.getRemovedExtIds()) : ImmutableSet.<EventExtId>of();
	}

	private List<Event> getUpdatedEvent(EventChanges sync) {
		return sync.getUpdated() != null ? ImmutableList.<Event>copyOf(sync.getUpdated()) : ImmutableList.<Event>of();
	}

}
