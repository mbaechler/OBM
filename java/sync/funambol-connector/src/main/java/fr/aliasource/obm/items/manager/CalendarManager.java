package fr.aliasource.obm.items.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

import fr.aliasource.funambol.ConvertionException;
import fr.aliasource.funambol.OBMException;
import fr.aliasource.obm.items.converter.ObmEventConverter;


public class CalendarManager extends ObmManager {

	private final CalendarClient calendarClient;
	private final ObmEventConverter obmEventConverter;
	
	private String calendar;
	private String userEmail;
	private Map<String, Event> updatedRest = null;
	private List<String> deletedRest = null;
	private String rangeMin;
	private String rangeMax;

	public CalendarManager(final LoginService loginService, final CalendarClient calendarClient, final ObmEventConverter obmEventConverter) {
		super(loginService);
		this.calendarClient = calendarClient;
		this.obmEventConverter = obmEventConverter;
	}

	public String getCalendar() {
		return calendar;
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

	public List<String> getAllItemKeys() throws OBMException {

		if (!syncReceived) {
			getSync(null);
		}

		List<String> keys = new LinkedList<String>();
		keys.addAll(updatedRest.keySet());
		return keys;
	}

	public List<String> getDeletedItemKeys(Timestamp since) throws OBMException {

		if (!syncReceived) {
			getSync(since);
		}

		ArrayList<String> ret = new ArrayList<String>(deletedRest.size());
		ret.addAll(deletedRest);
		return ret;
	}

	public List<String> getUpdatedItemKeys(Timestamp since) throws OBMException {
		if (!syncReceived) {
			getSync(since);
		}
		List<String> keys = new LinkedList<String>();
		keys.addAll(updatedRest.keySet());
		return keys;
	}

	public com.funambol.common.pim.calendar.Calendar getItemFromId(String key) throws OBMException {
		try {
			Event event = updatedRest.get(key);
			if (event == null) {
				logger.info(" item " + key + " not found in updated -> get from sever");
				EventExtId id = new EventExtId(key);
				event = calendarClient.getEventFromExtId(token, calendar, id);
			}
			com.funambol.common.pim.calendar.Calendar ret = obmEventConverter.obmEventToFoundationCalendar(event);
			return ret;
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage(),e);
		} catch (EventNotFoundException e) {
			throw new OBMException(e.getMessage(),e);
		} catch (ConvertionException e) {
			throw new OBMException(e.getMessage(),e);
		}

	}

	public void removeItem(String key) throws OBMException {

		Event event = null;
		try {
			EventExtId id = new EventExtId(key);
			event = calendarClient.getEventFromExtId(token, calendar, id);
			if (event == null) {
				logger.info("event removed on pda not in db: " + calendar
						+ " / " + key);
				return;
			}

			if (event.getAttendees() == null
					|| event.getAttendees().size() == 1) {
				// no attendee (only the owner)
				logger.info("not a meeting, removing event");
				calendarClient.removeEventByExtId(token, calendar, id, event.getSequence(), false);
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
		Event c = null;
		try {
			c = calendarClient.modifyEvent(token, calendar,
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
			EventExtId ext = new EventExtId(UUID.randomUUID().toString());
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

		Event evt = obmEventConverter.foundationCalendarToObmEvent(event, userEmail);

		if (evt == null) {
			return new LinkedList<String>();
		}

		try {
			evt.setUid(null);
			return calendarClient.getEventTwinKeys(token, calendar, evt).getKeys();
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}
	}

	private void getSync(Timestamp since) throws OBMException {
		try {
			Date d = null;
			if (since != null) {
				d = new Date(since.getTime());
			}
	
			// get modified items
			
			SyncRange syncRange = getSyncRanges(rangeMin, rangeMax);
			EventChanges sync = calendarClient.getSyncInRange(token, calendar, d, syncRange);
			
			logger.info("getSync(" + calendar + ", " + d + " (since == " + since
					+ ")) => upd: " + sync.getUpdated().length + " del: "
					+ sync.getRemoved().length);
			
			// remove refused events and private events
			updatedRest = new HashMap<String, Event>();
			deletedRest = new ArrayList<String>();
			String user = token.getUserWithDomain();
			if (sync.getUpdated() != null) {
				for (Event e : sync.getUpdated()) {
					logger.info("getSync: " + e.getTitle() + ", d: " + e.getDate());
					if ((e.getPrivacy() == 1 && !calendar.equals(user))
							|| isUserRefused(userEmail, e.getAttendees())) {
						if (d != null) {
							deletedRest.add((e.getExtId().serializeToString()));
						}
					} else {
						updatedRest.put(e.getExtId().serializeToString(), e);
					}
				}
			}
			if(sync.getRemovedExtIds() != null){
				for (EventExtId del : sync.getRemovedExtIds()) {
					deletedRest.add(del.getExtId());
				}
			}
	
			syncReceived = true;
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}
	}
	
	public boolean isUserRefused(String userEmail, List<Attendee> list) {
		for (Attendee at : list) {
			if (at.getEmail().equals(userEmail)
					&& at.getState() == ParticipationState.DECLINED) {
				return true;
			}
		}
		return false;
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

}
