package fr.aliasource.obm.items.manager;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.obm.sync.auth.EventAlreadyExistException;
import org.obm.sync.auth.EventNotFoundException;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.calendar.Attendee;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventExtId;
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

import fr.aliasource.funambol.ConvertionException;
import fr.aliasource.funambol.OBMException;
import fr.aliasource.obm.items.converter.ObmEventConverter;

@Singleton
public class CalendarServiceObmImpl extends ObmManager implements ICalendarService {

	private final CalendarClient calendarClient;
	private final ObmEventConverter obmEventConverter;

	@Inject
	private CalendarServiceObmImpl(final CalendarClient calendarClient,	final ObmEventConverter obmEventConverter) {
		this.calendarClient = calendarClient;
		this.obmEventConverter = obmEventConverter;
	}

	@Override
	public List<String> getAllItemKeys(SyncSession syncBean) throws OBMException {
		CalendarChanges changes = getSync(syncBean, null);
		return transformSetEventExtIdToListString(changes.getUpdated().keySet());
	}

	@Override
	public List<String> getDeletedItemKeys(SyncSession syncBean, Timestamp since) throws OBMException {
		CalendarChanges changes = getSync(syncBean, since);
		return transformListEventExtIdToListString(changes.getDeleted());
	}

	@Override
	public List<String> getUpdatedItemKeys(SyncSession syncBean, Timestamp since) throws OBMException {
		CalendarChanges changes = getSync(syncBean, since);
		return transformSetEventExtIdToListString(changes.getUpdated().keySet());
	}

	@Override
	public com.funambol.common.pim.calendar.Calendar getItemFromId(SyncSession syncBean, SyncItemKey syncItemKey) throws OBMException {
		try {
			final EventExtId eventExtId = getEventExtIdFromSyncItemKey(syncItemKey);
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

	private EventExtId getEventExtIdFromSyncItemKey(SyncItemKey syncItemKey) {
		final String itemKey = getCheckedSyncItemKeyAsString(syncItemKey);
		return new EventExtId(itemKey);
	}

	@Override
	public void removeItem(SyncSession syncBean, SyncItemKey syncItemKey) throws OBMException {
		try {
			final EventExtId eventExtId = getEventExtIdFromSyncItemKey(syncItemKey);
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
	public List<String> getEventTwinKeys(SyncSession syncBean, com.funambol.common.pim.calendar.Calendar event)
			throws OBMException {

		try {
			Event evt = obmEventConverter.foundationCalendarToObmEvent(event, syncBean.getUserLogin());
			if (evt == null) {
				return new LinkedList<String>();
			}
			evt.setUid(null);
			return calendarClient.getEventTwinKeys(syncBean.getObmAccessToken(), syncBean.getUserLogin(), evt).getKeys();
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
			EventChanges sync = calendarClient.getSyncInRange(syncBean.getObmAccessToken(), userLogin, lastSync, syncRange);
			
			logger.info("getSync(" + userLogin + ", " + lastSync + " (since == " + since
					+ ")) => upd: " + sync.getUpdated().length + " del: "
					+ sync.getRemoved().length);
			
			List<Event> updated = getUpdatedEvent(sync);
			Set<EventExtId> deleted = getRemovedEvent(sync);
			
			final Map<EventExtId, Event> updatedRest = transformAsFunambolUpdated(updated);
			final List<EventExtId> deletedRest = transformAsFunambolRemoved(deleted);
			return new CalendarChanges(updatedRest, deletedRest);

		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}
	}

	private List<EventExtId> transformAsFunambolRemoved(Set<EventExtId> deleted) {
		ImmutableList.Builder<EventExtId> mapBuilder = ImmutableList.builder();
		for (EventExtId e : deleted) {
			mapBuilder.add(e);
		}
		return mapBuilder.build();
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

	private List<String> transformSetEventExtIdToListString(Set<EventExtId> keySet) {
		List<EventExtId> listKey = ImmutableList.<EventExtId>copyOf(keySet);
		return transformListEventExtIdToListString(listKey);
	}
	
	private List<String> transformListEventExtIdToListString(
			List<EventExtId> deleted) {
		return Lists.transform(deleted, new Function<EventExtId, String>() {
			@Override
			public String apply(EventExtId input) {
				return input.serializeToString();
			}
		});
	}

}
