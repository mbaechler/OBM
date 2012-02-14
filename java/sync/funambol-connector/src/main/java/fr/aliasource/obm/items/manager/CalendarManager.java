package fr.aliasource.obm.items.manager;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
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
import org.obm.sync.client.login.LoginService;
import org.obm.sync.items.EventChanges;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import fr.aliasource.funambol.ConvertionException;
import fr.aliasource.funambol.OBMException;
import fr.aliasource.obm.items.converter.ObmEventConverter;


public class CalendarManager extends ObmManager {

	private final CalendarClient calendarClient;
	private final ObmEventConverter obmEventConverter;
	
	private String calendar;
	private String userEmail;
	private Map<EventExtId, Event> updatedRest = null;
	private List<EventExtId> deletedRest = null;
	private String rangeMin;
	private String rangeMax;

	public CalendarManager(final LoginService loginService, final CalendarClient calendarClient, final ObmEventConverter obmEventConverter) {
		super(loginService);
		this.calendarClient = calendarClient;
		this.obmEventConverter = obmEventConverter;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

	public void initUserEmail() throws OBMException {
		try {
			userEmail = calendarClient.getUserEmail(token);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}
	}
	
	public void initSyncRange(String sourceQuery) {
		//format:   /dr(-30,90)
		try {
			if (sourceQuery != null && !sourceQuery.equals("") && sourceQuery.contains("dr(")) {
				String min = sourceQuery.split(",")[0];
				String max = sourceQuery.split(",")[1];
				rangeMin = min.replace("/dr(-", "");
				rangeMax = max.replace(")", "");
				logger.info("Sync initialized with range : " + rangeMin + " <> " + rangeMax);
			}
		} catch (Exception e) {
			logger.error("error initializing sync range");
		}
	}

	public List<String> getAllItemKeys() throws OBMException {
		if (!syncReceived) {
			getSync(null);
		}
		return transformSetEventExtIdToListString(updatedRest.keySet());
	}

	public List<String> getDeletedItemKeys(Timestamp since) throws OBMException {
		if (!syncReceived) {
			getSync(since);
		}
		return transformListEventExtIdToListString(deletedRest);
	}

	public List<String> getUpdatedItemKeys(Timestamp since) throws OBMException {
		if (!syncReceived) {
			getSync(since);
		}
		return transformSetEventExtIdToListString(updatedRest.keySet());
	}

	public com.funambol.common.pim.calendar.Calendar getItemFromId(String key) throws OBMException {
		try {
			EventExtId eventExtId = new EventExtId(key); 
			Event event = updatedRest.get(eventExtId);
			if (event == null) {
				logger.info(" item " + key + " not found in updated -> get from sever");
				EventExtId id = new EventExtId(key);
				event = calendarClient.getEventFromExtId(token, calendar, id);
			}
			return obmEventConverter.obmEventToFoundationCalendar(event);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(),e);
		} catch (EventNotFoundException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ConvertionException e) {
			throw new OBMException(e.getMessage(),e);
		}

	}

	public void removeItem(String key) throws OBMException {
		try {
			EventExtId eventExtId = new EventExtId(key);
			Event event = calendarClient.getEventFromExtId(token, calendar, eventExtId);
			if (event == null) {
				logger.info("event removed on pda not in db: " + calendar
						+ " / " + key);
				return;
			}

			if (event.getAttendees() == null
					|| event.getAttendees().size() == 1) {
				// no attendee (only the owner)
				logger.info("not a meeting, removing event");
				calendarClient.removeEventByExtId(token, calendar, eventExtId, event.getSequence(), false);
			} else {
				refuseEvent(event);
			}

		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		} catch (EventNotFoundException e) {
			throw new OBMException(e.getMessage());
		}
	}

	private void refuseEvent(Event event) throws ServerFault {
		logger.info("meeting removed, refusing for " + userEmail);
		for (Attendee at : event.getAttendees()) {
			if (at.getEmail().equals(userEmail)) {
				at.setState(ParticipationState.DECLINED);
				logger.info("DECLINED for email " + userEmail);
				return;
			}
		}
		calendarClient.modifyEvent(token, calendar, event, true, false);
	}
	
	public com.funambol.common.pim.calendar.Calendar updateItem(
			com.funambol.common.pim.calendar.Calendar event)
			throws OBMException {
		try {
			Event c = calendarClient.modifyEvent(token, calendar,
					obmEventConverter.foundationCalendarToObmEvent(event, userEmail), false, false);
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

	public com.funambol.common.pim.calendar.Calendar addItem(
			com.funambol.common.pim.calendar.Calendar event)
			throws OBMException {
		try {
			Event forCreate = obmEventConverter.foundationCalendarToObmEvent(event, userEmail);
			EventExtId ext = new EventExtId(UUID.randomUUID());
			forCreate.setExtId(ext);
			EventObmId uid = calendarClient.createEvent(token, calendar, forCreate, false);
			Event evt = calendarClient.getEventFromId(token, calendar, uid);
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

	public List<String> getEventTwinKeys(
			com.funambol.common.pim.calendar.Calendar event)
			throws OBMException {

		try {
			Event evt = obmEventConverter.foundationCalendarToObmEvent(event, userEmail);
			if (evt == null) {
				return new LinkedList<String>();
			}
			evt.setUid(null);
			return calendarClient.getEventTwinKeys(token, calendar, evt).getKeys();
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(), e);
		} catch (ConvertionException e) {
			throw new OBMException(e.getMessage());
		}
	}

	private void getSync(Timestamp since) throws OBMException {
		try {
			Date lastSync = null;
			if (since != null) {
//				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//				cal.setTime(since);
				lastSync = since;
			}

			SyncRange syncRange = getSyncRanges(rangeMin, rangeMax);
			EventChanges sync = calendarClient.getSyncInRange(token, calendar, lastSync, syncRange);
			
			logger.info("getSync(" + calendar + ", " + lastSync + " (since == " + since
					+ ")) => upd: " + sync.getUpdated().length + " del: "
					+ sync.getRemoved().length);
			
			List<Event> updated = getUpdatedEvent(sync);
			Set<EventExtId> deleted = getRemovedEvent(sync);
			
			updatedRest = transformAsFunambolUpdated(updated);
			deletedRest = transformAsFunambolRemoved(deleted);

			syncReceived = true;
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

	private SyncRange getSyncRanges(String min, String max) {
		if(min == null || "".equals(min) || max == null || "".equals(max)){
			return null;
		}
		int minDays = Integer.parseInt(min);
		int maxDays = Integer.parseInt(max);
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		now.add(Calendar.DAY_OF_MONTH, 0 - minDays);
		Timestamp before = new Timestamp(now.getTimeInMillis());
		now.add(Calendar.DAY_OF_MONTH, minDays + maxDays);
		Timestamp after = new Timestamp(now.getTimeInMillis());
		return new SyncRange(before, after);
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
