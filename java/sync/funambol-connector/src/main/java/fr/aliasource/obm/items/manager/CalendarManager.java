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

import org.obm.sync.NotAllowedException;
import org.obm.sync.auth.EventAlreadyExistException;
import org.obm.sync.auth.EventNotFoundException;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventExtId;
import org.obm.sync.calendar.EventObmId;
import org.obm.sync.calendar.SyncRange;
import org.obm.sync.client.ISyncClient;
import org.obm.sync.client.calendar.CalendarClient;
import org.obm.sync.items.EventChanges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.aliasource.funambol.OBMException;
import fr.aliasource.funambol.utils.CalendarHelper;
import fr.aliasource.obm.items.converter.ObmEventConverter;

/**
 * Maintains a connection to obm-sync through a {@link CalendarClient}.
 * 
 * Stores the result of the getSync call to provide updates and deletions
 * informations "on demand".
 * 
 * @author tom
 * 
 */
public class CalendarManager extends ObmManager {

	private CalendarClient binding;
	private ObmEventConverter obmEventConverter;
	
	private String calendar;
	private String userEmail;
	private Map<String, Event> updatedRest = null;
	private List<String> deletedRest = null;
	private String rangeMin;
	private String rangeMax;

	private static final Logger logger = LoggerFactory.getLogger(CalendarManager.class);

	public CalendarManager(CalendarClient binding, ObmEventConverter obmEventConverter) {
		this.binding = binding;
		this.obmEventConverter = obmEventConverter;
	}

	public String getCalendar() {
		return calendar;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

	public CalendarClient getBinding() {
		return binding;
	}

	public void initUserEmail() throws OBMException {
		try {
			userEmail = binding.getUserEmail(token);
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

		Event event = null;

		event = updatedRest.get(key);

		if (event == null) {
			logger.info(" item " + key
					+ " not found in updated -> get from sever");
			try {
				EventObmId id = new EventObmId(key);
				event = binding.getEventFromId(token, calendar, id);
			} catch (ServerFault e) {
				throw new OBMException(e.getMessage());
			} catch (EventNotFoundException e) {
				throw new OBMException(e.getMessage());
			}
		}

		com.funambol.common.pim.calendar.Calendar ret = obmEventConverter.obmEventToFoundationCalendar(
				event);

		return ret;
	}

	public void removeItem(String key) throws OBMException {

		Event event = null;
		try {
			EventObmId id = new EventObmId(key);
			event = binding.getEventFromId(token, calendar, id);
			// log.info(" attendees size : "+event.getAttendees().length );
			// log.info(" owner : "+event.getOwner()+" calendar : "+calendar);
			if (event == null) {
				logger.info("event removed on pda not in db: " + calendar
						+ " / " + key);
				return;
			}

			if (event.getAttendees() == null
					|| event.getAttendees().size() == 1) {
				// no attendee (only the owner)
				logger.info("not a meeting, removing event");
				try {
					binding.removeEventById(token, calendar, id, event.getSequence(), false);
				} catch (NotAllowedException e) {
					refuseEvent(event);
				}
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
		CalendarHelper.refuseEvent(event, userEmail);
		// event = binding.refuseEvent(token, calendar, event);
		binding.modifyEvent(token, calendar, event, true, false);
	}

	public com.funambol.common.pim.calendar.Calendar updateItem(
			com.funambol.common.pim.calendar.Calendar event)
			throws OBMException {

		Event c = null;
		try {
			c = binding.modifyEvent(token, calendar,
					obmEventConverter.foundationCalendarToObmEvent(event, false, userEmail), false, false);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}

		if (c == null) {
			return null;
		} else {
			return obmEventConverter.obmEventToFoundationCalendar(c);
		}
	}

	public com.funambol.common.pim.calendar.Calendar addItem(
			com.funambol.common.pim.calendar.Calendar event)
			throws OBMException {

		Event evt = null;

		try {
			Event forCreate = obmEventConverter.foundationCalendarToObmEvent(event, true, userEmail);
			EventExtId ext = new EventExtId(UUID.randomUUID().toString());
			forCreate.setExtId(ext);
			EventObmId uid = binding.createEvent(token, calendar, forCreate, false);
			evt = binding.getEventFromId(token, calendar, uid);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		} catch (EventAlreadyExistException e) {
			throw new OBMException(e.getMessage());
		} catch (EventNotFoundException e) {
			throw new OBMException(e.getMessage());
		}

		if (evt == null) {
			return null;
		} else {
			return obmEventConverter.obmEventToFoundationCalendar(evt);
		}
	}

	public List<String> getEventTwinKeys(
			com.funambol.common.pim.calendar.Calendar event)
			throws OBMException {

		Event evt = obmEventConverter.foundationCalendarToObmEvent(event, true, userEmail);

		if (evt == null) {
			return new LinkedList<String>();
		}

		try {
			evt.setUid(null);
			return binding.getEventTwinKeys(token, calendar, evt).getKeys();
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}
	}

	// ---------------- Private methods ----------------------------------

	private void getSync(Timestamp since) throws OBMException {
		Date d = null;
		if (since != null) {
			d = new Date(since.getTime());
		}

		EventChanges sync = null;
		// get modified items
		try {
			SyncRange syncRange = getSyncRanges(rangeMin, rangeMax);
			sync = binding.getSyncInRange(token, calendar, d, syncRange);
		} catch (ServerFault e) {
			throw new OBMException(e.getMessage());
		}
		logger.info("getSync(" + calendar + ", " + d + " (since == " + since
				+ ")) => upd: " + sync.getUpdated().length + " del: "
				+ sync.getRemoved().length);
		Event[] updated = new Event[0];
		if (sync.getUpdated() != null) {
			updated = sync.getUpdated();
		}
		EventObmId[] deleted = new EventObmId[0];
		if (sync.getRemoved() != null) {
			deleted = sync.getRemoved();
		}

		// remove refused events and private events
		updatedRest = new HashMap<String, Event>();
		deletedRest = new ArrayList<String>();
		String user = token.getUser();

		for (Event e : updated) {
			logger.info("getSync: " + e.getTitle() + ", d: " + e.getDate());
			if ((e.getPrivacy() == 1 && !calendar.equals(user))
					|| CalendarHelper
							.isUserRefused(userEmail, e.getAttendees())) {
				if (d != null) {
					deletedRest.add((e.getObmId().serializeToString()));
				}
			} else {
				updatedRest.put(e.getObmId().serializeToString(), e);
			}
		}

		for (EventObmId del : deleted) {
			deletedRest.add("" + del);
		}

		syncReceived = true;
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
	
	@Override
	protected ISyncClient getSyncClient() {
		return binding;
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

	public void setRangeMin(String rangeMin) {
		this.rangeMin = rangeMin;
	}

	public String getRangeMin() {
		return rangeMin;
	}

	public void setRangeMax(String rangeMax) {
		this.rangeMax = rangeMax;
	}

	public String getRangeMax() {
		return rangeMax;
	}
}
