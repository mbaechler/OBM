package fr.aliasource.obm.items.converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.obm.sync.calendar.Attendee;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventExtId;
import org.obm.sync.calendar.EventObmId;
import org.obm.sync.calendar.EventOpacity;
import org.obm.sync.calendar.EventRecurrence;
import org.obm.sync.calendar.ParticipationRole;
import org.obm.sync.calendar.ParticipationState;
import org.obm.sync.calendar.RecurrenceKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.common.pim.calendar.ExceptionToRecurrenceRule;
import com.funambol.common.pim.calendar.RecurrencePattern;
import com.google.inject.Singleton;

import fr.aliasource.funambol.utils.CalendarHelper;
import fr.aliasource.funambol.utils.Helper;

@Singleton
public class ObmEventConverter {

	private static final Logger logger = LoggerFactory.getLogger(ObmEventConverter.class);
	
	/**
	 * Convert an OBM event in a calendar of type
	 * com.funambol.common.pim.calendar.Calendar
	 * 
	 * @param obmevent
	 * @param type
	 * @return
	 */
	public com.funambol.common.pim.calendar.Calendar obmEventToFoundationCalendar(
			Event obmevent) {

		com.funambol.common.pim.calendar.Calendar calendar = new com.funambol.common.pim.calendar.Calendar();
		com.funambol.common.pim.calendar.Event event = new com.funambol.common.pim.calendar.Event();
		calendar.setEvent(event);

		event.getUid().setPropertyValue(obmevent.getObmId().serializeToString());
		logger
				.info("bd -> pda - obmToFound: " + obmevent.getTitle()
						+ " date: " + obmevent.getDate() + " "
						+ obmevent.getDuration());
		Date dstart = obmevent.getDate();

		Date dend = null;
		if (!obmevent.isAllday()) {
			event.getDtStart().setPropertyValue(
					CalendarHelper.getUTCFormat(dstart));

			java.util.Calendar temp = java.util.Calendar.getInstance();
			temp.setTime(dstart);
			temp.add(java.util.Calendar.SECOND, obmevent.getDuration());
			dend = temp.getTime();

			event.getDtEnd()
					.setPropertyValue(CalendarHelper.getUTCFormat(dend));
		} else {
			java.util.Calendar temp = java.util.Calendar.getInstance();
			temp.setTime(dstart);

			event.getDtStart().setPropertyValue(
					CalendarHelper.getUTCFormat(temp.getTime()));

			temp.add(java.util.Calendar.SECOND, (int) (86400 * Math
					.ceil(((float) obmevent.getDuration()) / 86400)));
			dend = temp.getTime();

			event.getDtEnd()
					.setPropertyValue(CalendarHelper.getUTCFormat(dend));
		}
		logger.info("computed dt end: " + dend);

		if (obmevent.getAlert() != null && obmevent.getAlert() > 0) {
			com.funambol.common.pim.calendar.Reminder remind = new com.funambol.common.pim.calendar.Reminder();

			remind.setMinutes(obmevent.getAlert() / 60);
			remind.setActive(true);
			event.setReminder(remind);

		} else {
			com.funambol.common.pim.calendar.Reminder remind = new com.funambol.common.pim.calendar.Reminder();
			remind.setActive(false);
			event.setReminder(remind);
		}
		/*
		 * logger.info("alert import:"+event.getReminder()); logger.info("alert
		 * import:"+obmevent.getAlert());
		 */
		event.setAllDay(new Boolean(obmevent.isAllday()));

		String s = obmevent.getTitle();
		if (s != null) {
			s = s.trim().replace("\r\n", "").replace("\n", "");
		}
		event.getSummary().setPropertyValue(s);
		event.getDescription().setPropertyValue(obmevent.getDescription());
		event.getCategories().setPropertyValue(obmevent.getCategory());

		s = obmevent.getLocation();
		if (s != null) {
			s = s.trim().replace("\r\n", "").replace("\n", "");
		}
		event.getLocation().setPropertyValue(s);

		if (obmevent.getPrivacy() == 1) {
			event.getAccessClass().setPropertyValue(new Short((short) 2)); // olPrivate
		} else {
			event.getAccessClass().setPropertyValue(new Short((short) 0)); // olNormal
		}
		if (obmevent.getOpacity() == EventOpacity.TRANSPARENT) {
			event.setBusyStatus(new Short((short) 0)); // olFree
			event.getTransp().setPropertyValue("1");
		} else {
			event.setBusyStatus(new Short((short) 2)); // olBusy
			event.getTransp().setPropertyValue("0");
		}
		
		

		event.getPriority().setPropertyValue("1");
		event.getStatus().setPropertyValue("0");

		/*
		 * XTag classification = new XTag();
		 * classification.setXTagValue("Classification");
		 * classification.getXTag().setPropertyValue("2");
		 * event.addXTag(classification);
		 */

		EventRecurrence obmrec = obmevent.getRecurrence();
		if (obmrec.getKind() != RecurrenceKind.none) {
			RecurrencePattern rp = CalendarHelper.getRecurrence(dstart, dend,
					obmrec);

			if (rp != null) {
				Date[] exceptions = obmrec.getExceptions();
				List<Event> evtExceptions = obmrec.getEventExceptions();
				Date[] eventExceptions = new Date[evtExceptions.size()];
				int i = 0;
				for (Event evEx : obmrec.getEventExceptions()) {
					//add original occurrence as exception
					eventExceptions[i++] = evEx.getRecurrenceId();
				}
				Date[] allExceptions = new Date[exceptions.length
						+ eventExceptions.length];
				System.arraycopy(exceptions, 0, allExceptions, 0,
						exceptions.length);
				System.arraycopy(eventExceptions, 0, allExceptions,
						exceptions.length, eventExceptions.length);
				
				List<ExceptionToRecurrenceRule> exceps = new ArrayList<ExceptionToRecurrenceRule>(
						allExceptions.length);
				for (Date d : allExceptions) {
					ExceptionToRecurrenceRule ex;
					try {
						ex = new ExceptionToRecurrenceRule(
								false, CalendarHelper.getUTCFormat(d));
						exceps.add(ex);
					} catch (ParseException e) {
						logger.error(e.getMessage(), e);
					}


				}
				rp.setExceptions(exceps);
			} else {
				logger.warn("null rec pattern with repeatkind=none");
			}

			event.setRecurrencePattern(rp);

		}
		event.setMileage(new Integer(0));

		return calendar;
	}
	
	
	/**
	 * Convert a calendar of type com.funambol.common.pim.calendar.Calendar in
	 * an OBM event
	 * 
	 * @param calendar
	 * @param type
	 * @param ignoreUid
	 * @param allDay
	 * @return
	 */
	public Event foundationCalendarToObmEvent(
			com.funambol.common.pim.calendar.Calendar calendar,
			boolean ignoreUid, String userEmail) {

		com.funambol.common.pim.calendar.Event foundation = calendar.getEvent();

		if (foundation != null) {
			Event event = fillObmEventWithVEvent(calendar, foundation,
					ignoreUid, userEmail);
			return event;
		} else {
			logger
					.warn("Received ICalendar does not contain a VEVENT, VTODO ?");
			return null;
		}

	}

	private Event fillObmEventWithVEvent(
			com.funambol.common.pim.calendar.Calendar calendar,
			com.funambol.common.pim.calendar.Event foundation, boolean ignoreUid,
			String userEmail) {
		Event event = new Event();
		if (!ignoreUid && foundation.getUid() != null
				&& !foundation.getUid().getPropertyValueAsString().equals("")) {
			EventObmId id = new EventObmId(foundation.getUid().getPropertyValueAsString());
			event.setUid(id);
		}
		EventExtId extId = new EventExtId(UUID.randomUUID().toString());
		event.setExtId(extId);
		
		event.setAllday(foundation.isAllDay());

		if (foundation.getDuration() != null) {
			logger.info("duration: "
					+ foundation.getDuration().getPropertyValue());
		}

		String prodId = "";
		if (calendar.getProdId() != null) {
			prodId = calendar.getProdId().getPropertyValueAsString();
			logger.info("prodId: " + prodId);
		}

		Date dstart = parseStart(foundation, event);
		Date dend = parseEnd(prodId, foundation);

		if (dend.getTime() != dstart.getTime()) {
			event
					.setDuration((int) ((dend.getTime() - dstart.getTime()) / 1000));
		} else {
			event.setDuration(3600);
		}

		if (foundation.getReminder() != null
				&& foundation.getReminder().getMinutes() != 0) {
			event.setAlert(foundation.getReminder().getMinutes() * 60);
		} else {
			event.setAlert(0);
		}

		logger.info("alert export : " + event.getAlert());

		if (foundation.getSummary() != null) {
			event.setTitle(foundation.getSummary().getPropertyValueAsString()
					.trim().replace("\r\n", "").replace("\n", ""));
		} else {
			event.setTitle("[Sans titre]");
		}

		if (foundation.getDescription() != null) {
			event.setDescription(foundation.getDescription()
					.getPropertyValueAsString());
		}

		if (foundation.getCategories() != null) {
			event.setCategory(CalendarHelper.getOneCategory(foundation
					.getCategories().getPropertyValueAsString()));
		}

		if (foundation.getLocation() != null) {
			event.setLocation(foundation.getLocation()
					.getPropertyValueAsString().trim().replace("\r\n", "")
					.replace("\n", ""));
		}

		if (foundation.getPriority() != null) {
			event.setPriority(Helper.getPriorityFromFoundation(foundation
					.getPriority().getPropertyValueAsString()));
		} else {
			event.setPriority(new Integer(1));
		}

		if (foundation.getAccessClass() != null
				&& Helper.nullToEmptyString(
						foundation.getAccessClass().getPropertyValueAsString())
						.equals("0")) { // olNormal
			event.setPrivacy(0); // public
		} else {
			event.setPrivacy(1); // private
		}

		if (foundation.getTransp() != null
				&& Helper.nullToEmptyString(
						foundation.getTransp().getPropertyValueAsString())
						.equals("1")) {
			event.setOpacity(EventOpacity.TRANSPARENT);
		} else {
			event.setOpacity(EventOpacity.OPAQUE);
		}
		
		EventRecurrence recurrence = null;
		if (foundation.isRecurrent()) {
			recurrence = CalendarHelper.getRecurrenceFromFoundation(foundation
					.getRecurrencePattern(), foundation.isAllDay());
		} else {
			recurrence = new EventRecurrence();
			recurrence.setKind(RecurrenceKind.none);
			recurrence.setDays("");
			recurrence.setFrequence(1);
		}
		event.setRecurrence(recurrence);

		// add syncing user as attendee
		Attendee syncingUser = new Attendee();
		syncingUser.setRequired(ParticipationRole.CHAIR);
		syncingUser.setState(ParticipationState.ACCEPTED);
		syncingUser.setEmail(userEmail);

		event.addAttendee(syncingUser);

		return event;
	}
	
	/**
	 * bb hack : on ajoute 1j pour les blackberry. "le 19 à minuit GMT" est
	 * converti en "le 18" par les classes funambol.
	 * 
	 * @param prodId
	 * @param foundation
	 * @param event
	 * @return
	 */
	private Date parseStart(com.funambol.common.pim.calendar.Event foundation, Event event) {
		String dtStart = foundation.getDtStart().getPropertyValueAsString();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Date utcDate = CalendarHelper.getDateFromUTCString(dtStart);
		cal.setTime(utcDate);
		event.setDate(utcDate);
		return cal.getTime();
	}

	private Date parseEnd(String prodId,
			com.funambol.common.pim.calendar.Event foundation) {
		String dtEnd = foundation.getDtEnd().getPropertyValueAsString();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Date utcDate = CalendarHelper.getDateFromUTCString(dtEnd);
		cal.setTime(utcDate);

		if (foundation.isAllDay() && "Blackberry".equals(prodId)) {
			//
			// logger.info("bb detected, adding 1 day to dtend");
			// cal.add(Calendar.DAY_OF_MONTH, 1);
			// logger.info("utcDate: " + utcDate + " prev dtend: " + dtEnd
			// + " new dtend: " + cal.getTime());
		}
		return cal.getTime();
	}

}
