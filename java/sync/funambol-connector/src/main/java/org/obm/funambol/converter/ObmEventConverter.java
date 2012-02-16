package org.obm.funambol.converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.obm.funambol.exception.ConvertionException;
import org.obm.sync.calendar.Attendee;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventExtId;
import org.obm.sync.calendar.EventOpacity;
import org.obm.sync.calendar.EventRecurrence;
import org.obm.sync.calendar.ParticipationRole;
import org.obm.sync.calendar.ParticipationState;
import org.obm.sync.calendar.RecurrenceKind;

import com.funambol.common.pim.calendar.ExceptionToRecurrenceRule;
import com.funambol.common.pim.calendar.RecurrencePattern;
import com.funambol.common.pim.calendar.RecurrencePatternException;
import com.funambol.common.pim.calendar.Reminder;
import com.funambol.common.pim.common.Property;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Singleton;


@Singleton
public class ObmEventConverter extends AbstractConverter implements IEventConverter{

	private static final byte[] foundationWeekDays = {
		RecurrencePattern.DAY_OF_WEEK_SUNDAY,
		RecurrencePattern.DAY_OF_WEEK_MONDAY,
		RecurrencePattern.DAY_OF_WEEK_TUESDAY,
		RecurrencePattern.DAY_OF_WEEK_WEDNESDAY,
		RecurrencePattern.DAY_OF_WEEK_THURSDAY,
		RecurrencePattern.DAY_OF_WEEK_FRIDAY,
		RecurrencePattern.DAY_OF_WEEK_SATURDAY, };
	
	@Override
	public com.funambol.common.pim.calendar.Calendar obmEventToFoundationCalendar(
			Event obmEvent) throws ConvertionException {

		com.funambol.common.pim.calendar.Event event = new com.funambol.common.pim.calendar.Event();
		appendExtId(event, obmEvent);
		appendStart(event, obmEvent);
		appendEnd(event, obmEvent);
		appendAllDay(event, obmEvent);
		appendAlert(event, obmEvent);
		appendTitle(event, obmEvent);
		appendDescription(event, obmEvent);
		appendCategory(event, obmEvent);
		appendLocation(event, obmEvent);
		appendPriority(event, obmEvent);
		appendPrivacy(event, obmEvent);
		appendOpacity(event, obmEvent);
		appendStatus(event);
		appendAttendees(event, obmEvent);
		appendReccurence(event, obmEvent);
		appendMileage(event);
		
		return new com.funambol.common.pim.calendar.Calendar(event);
	}
	
	@Override
	public Event foundationCalendarToObmEvent(
			com.funambol.common.pim.calendar.Calendar calendar, String userEmail) throws ConvertionException {

		com.funambol.common.pim.calendar.Event foundation = calendar.getEvent();
		if (foundation != null) {
			Event event = new Event();
			appendUid(event, foundation);
			appendAllday(event, foundation);
			appendDtStart(event, foundation);
			appendDuration(event, foundation);
			appendAlert(event, foundation);
			appendTitle(event, foundation);
			appendDescription(event, foundation);
			appendCategories(event, foundation);
			appendLocation(event, foundation);
			appendPriority(event, foundation);
			appendPrivacy(event, foundation);
			appendOpacity(event, foundation);
			appendAttendees(event, foundation, userEmail);
			appendRecurence(event, foundation);

			// add syncing user as attendee
			return event;
		} else {
			logger
					.warn("Received ICalendar does not contain a VEVENT, VTODO ?");
			return null;
		}
	}

	
	private void appendMileage(com.funambol.common.pim.calendar.Event event) {
		event.setMileage(new Integer(0));
	}

	private void appendReccurence(com.funambol.common.pim.calendar.Event event,
			Event obmEvent) throws ConvertionException {
		EventRecurrence obmrec = obmEvent.getRecurrence();
		if (isRecurrentEvent(obmrec)) {
			RecurrencePattern rp = getRecurrence(obmEvent);
			//FIXME This is probably bad
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
								false, getUTCFormat(d));
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
	}
	
	/**
	 * Convert an OBM reccurence in a foundation recurrence
	 * 
	 * @param obmrec
	 * @return
	 * @throws ConvertionException 
	 */
	public RecurrencePattern getRecurrence(Event event) throws ConvertionException {

		EventRecurrence obmrec = event.getRecurrence();
		
		RecurrencePattern result = null;

		int interval = obmrec.getFrequence();
		Date cend = obmrec.getEnd();
		boolean noEndDate = true;

		if (cend != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("GMT"));
			cal.setTime(cend);
			if (cal.get(Calendar.YEAR) > 2017) {
				cal.set(Calendar.YEAR, 2017);
			}
			noEndDate = false;
		}

		String sPatternStart = getUTCFormat(event.getDate());
		String sPatternEnd = getUTCFormat(event.getDate());
		short dayOfWeekMask = getDayOfWeekMask(obmrec.getDays());

		try {
			if (obmrec.getKind() == RecurrenceKind.daily) {

				result = RecurrencePattern.getDailyRecurrencePattern(interval,
						sPatternStart, sPatternEnd, noEndDate);

			} else if (obmrec.getKind() == RecurrenceKind.weekly) {

				result = RecurrencePattern.getWeeklyRecurrencePattern(interval,
						dayOfWeekMask, sPatternStart, sPatternEnd, noEndDate);

			} else if (obmrec.getKind() == RecurrenceKind.monthlybydate) {

				result = RecurrencePattern.getMonthlyRecurrencePattern(
						interval, getDayOfMonth(event.getDate()), sPatternStart,
						sPatternEnd, noEndDate);

			} else if (obmrec.getKind() == RecurrenceKind.monthlybyday) {

				result = RecurrencePattern.getMonthNthRecurrencePattern(
						interval, getDayOfWeek(event.getDate()), getNthDay(event.getDate()),
						sPatternStart, sPatternEnd);

			} else if (obmrec.getKind() == RecurrenceKind.yearly) {

				result = RecurrencePattern.getYearlyRecurrencePattern(interval,
						getDayOfMonth(event.getDate()), getMonthOfYear(event.getDate()),
						sPatternStart, sPatternEnd, noEndDate);

			}
		} catch (RecurrencePatternException e) {
			throw new ConvertionException(e.getMessage(), e);
		}
		return result;
	}
	
	private static short getMonthOfYear(Date date) {
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.setTime(date);

		return (short) (temp.get(java.util.Calendar.MONTH) + 1);
	}
	
	private static short getNthDay(Date date) {
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.setTime(date);

		return (short) temp.get(java.util.Calendar.DAY_OF_WEEK_IN_MONTH);
	}
	
	private static short getDayOfWeek(Date date) {
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.setTime(date);

		short result = 0;

		switch (temp.get(java.util.Calendar.DAY_OF_WEEK)) {
		case java.util.Calendar.FRIDAY:
			result += RecurrencePattern.DAY_OF_WEEK_FRIDAY;
			break;
		case java.util.Calendar.MONDAY:
			result += RecurrencePattern.DAY_OF_WEEK_MONDAY;
			break;
		case java.util.Calendar.SATURDAY:
			result += RecurrencePattern.DAY_OF_WEEK_SATURDAY;
			break;
		case java.util.Calendar.SUNDAY:
			result += RecurrencePattern.DAY_OF_WEEK_SUNDAY;
			break;
		case java.util.Calendar.THURSDAY:
			result += RecurrencePattern.DAY_OF_WEEK_THURSDAY;
			break;
		case java.util.Calendar.TUESDAY:
			result += RecurrencePattern.DAY_OF_WEEK_TUESDAY;
			break;
		case java.util.Calendar.WEDNESDAY:
			result += RecurrencePattern.DAY_OF_WEEK_WEDNESDAY;
			break;
		default:
			break;
		}

		return result;
	}
	
	/**
	 * Get the day number (1-31) form a date
	 * 
	 * @param dstart
	 * @return
	 */
	private static short getDayOfMonth(Date date) {
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.setTime(date);
		return (short) temp.get(java.util.Calendar.DAY_OF_MONTH);
	}
	
	private static short getDayOfWeekMask(String days) {
		short result = 0;

		if (days == null || days.equals("") || days.length() < 7) {
		} else {
			for (int i = 0; i < 7; i++) {
				if (days.charAt(i) == '1') {
					result += foundationWeekDays[i];
				}
			}
		}
		return result;
	}

	private boolean isRecurrentEvent(EventRecurrence obmrec) {
		return obmrec != null && RecurrenceKind.none != obmrec.getKind();
	}

	private void appendStatus(com.funambol.common.pim.calendar.Event event) {
		event.getStatus().setPropertyValue("0");
	}


	private void appendPriority(com.funambol.common.pim.calendar.Event event,
			Event obmEvent) {
		event.getPriority().setPropertyValue(obmEvent.getPriority());
	}


	private void appendOpacity(com.funambol.common.pim.calendar.Event event,
			Event obmEvent) {
		if (obmEvent.getOpacity() == EventOpacity.TRANSPARENT) {
			event.setBusyStatus(new Short((short) 0)); // olFree
			event.getTransp().setPropertyValue("1");
		} else {
			event.setBusyStatus(new Short((short) 2)); // olBusy
			event.getTransp().setPropertyValue("0");
		}
	}


	private void appendPrivacy(com.funambol.common.pim.calendar.Event event,
			Event obmEvent) {
		if (obmEvent.getPrivacy() == 1) {
			event.getAccessClass().setPropertyValue(new Short((short) 2)); // olPrivate
		} else {
			event.getAccessClass().setPropertyValue(new Short((short) 0)); // olNormal
		}
		
	}


	private void appendLocation(com.funambol.common.pim.calendar.Event event,
			Event obmEvent) {
		String location = sanitazeString(obmEvent.getLocation()); 
		event.getLocation().setPropertyValue(location);
		
	}


	private void appendCategory(com.funambol.common.pim.calendar.Event event,
			Event obmEvent) {
		String category = StringUtils.trimToNull(obmEvent.getCategory());
		event.getCategories().setPropertyValue(category);
	}

	private void appendDescription(
			com.funambol.common.pim.calendar.Event event, Event obmEvent) {
		String description = StringUtils.trimToNull(obmEvent.getDescription());
		event.getDescription().setPropertyValue(description);
	}


	private void appendTitle(com.funambol.common.pim.calendar.Event event,
			Event obmEvent) {
		String title = sanitazeString(obmEvent.getTitle()); 
		event.getSummary().setPropertyValue(title);
	}

	private String sanitazeString(String value) {
		String sanitaze = StringUtils.trimToNull(value);
		if(sanitaze != null){
			sanitaze = StringUtils.remove(sanitaze, "\r\n");
			sanitaze = StringUtils.remove(sanitaze, "\n");
		}
		return sanitaze;
	}

	private void appendAllDay(com.funambol.common.pim.calendar.Event funisEvent,
			Event obmEvent) {
		funisEvent.setAllDay(new Boolean(obmEvent.isAllday()));
	}

	private void appendAlert(com.funambol.common.pim.calendar.Event funisEvent,
			Event obmevent) {
		Reminder remind = new Reminder();
		if (obmevent.getAlert() != null && obmevent.getAlert() > 0) {
			remind.setMinutes(obmevent.getAlert() / 60);
			remind.setActive(true);
		} else {
			remind.setActive(false);
		}
		funisEvent.setReminder(remind);
	}


	private void appendStart(com.funambol.common.pim.calendar.Event funisEvent,
			Event obmEvent) throws ConvertionException {
		if(obmEvent.getDate() == null){
			throw new ConvertionException("The start day cannot be null");
		}

		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.setTime(obmEvent.getDate());
		if (!obmEvent.isAllday()) {
			temp.add(java.util.Calendar.SECOND, obmEvent.getDuration());
		} else {

			temp.add(java.util.Calendar.SECOND, (int) (86400 * Math
					.ceil(((float) obmEvent.getDuration()) / 86400)));
		}
		funisEvent.getDtEnd()
				.setPropertyValue(getUTCFormat(temp.getTime()));
	}
	
	private void appendEnd(com.funambol.common.pim.calendar.Event funisEvent, Event obmEvent) throws ConvertionException {
		if(!obmEvent.isAllday() && obmEvent.getDuration() <=0){
			throw new ConvertionException("The duration cannot be null for no all days events");
		}
		java.util.Calendar temp = java.util.Calendar.getInstance();
		temp.setTime(obmEvent.getDate());
		funisEvent.getDtStart().setPropertyValue(
				getUTCFormat(temp.getTime()));
	}

	private void appendExtId(com.funambol.common.pim.calendar.Event event,
			Event obmEvent) throws ConvertionException {
		if(obmEvent.getExtId() == null){
			throw new ConvertionException("EventExtId cannot be null");
		}
		event.getUid().setPropertyValue(obmEvent.getExtId().serializeToString());
	}

	private void appendAttendees(com.funambol.common.pim.calendar.Event event,
			Event obmEvent) {
		List<com.funambol.common.pim.calendar.Attendee> funambolAttendees = getFunambolAttendees(obmEvent.getAttendees());
		event.getAttendees().addAll(funambolAttendees);
	}

	private List<com.funambol.common.pim.calendar.Attendee> getFunambolAttendees(
			List<Attendee> attendees) {
		return Lists.transform(attendees, new Function<Attendee, com.funambol.common.pim.calendar.Attendee>() {

			@Override
			public com.funambol.common.pim.calendar.Attendee apply(
					Attendee input) {
				com.funambol.common.pim.calendar.Attendee att = new com.funambol.common.pim.calendar.Attendee();
				att.setEmail(input.getEmail());
				att.setName(input.getDisplayName());
				att.setKind(com.funambol.common.pim.calendar.Attendee.INDIVIDUAL);
				if(input.isOrganizer()){
					att.setRole(com.funambol.common.pim.calendar.Attendee.ORGANIZER);
				} else {
					att.setRole(com.funambol.common.pim.calendar.Attendee.ATTENDEE);
				}
				switch (input.getRequired()) {
				case CHAIR:
					att.setExpected(com.funambol.common.pim.calendar.Attendee.CHAIRMAN);
					break;
				case NON:
					att.setExpected(com.funambol.common.pim.calendar.Attendee.NON_PARTICIPANT);
					break;
				case OPT:
					att.setExpected(com.funambol.common.pim.calendar.Attendee.OPTIONAL);
					break;
				case REQ:
					att.setExpected(com.funambol.common.pim.calendar.Attendee.REQUIRED);
					break;
				default:
					att.setExpected(com.funambol.common.pim.calendar.Attendee.UNKNOWN);
					break;
				}
				switch (input.getState()) {
				case ACCEPTED:
					att.setStatus(com.funambol.common.pim.calendar.Attendee.ACCEPTED);
					break;
				case COMPLETED:
					att.setStatus(com.funambol.common.pim.calendar.Attendee.COMPLETED);
					break;
				case DECLINED:
					att.setStatus(com.funambol.common.pim.calendar.Attendee.DECLINED);
					break;
				case DELEGATED:
					att.setStatus(com.funambol.common.pim.calendar.Attendee.DELEGATED);
					break;
				case INPROGRESS:
					att.setStatus(com.funambol.common.pim.calendar.Attendee.IN_PROCESS);
					break;
				case NEEDSACTION:
					att.setStatus(com.funambol.common.pim.calendar.Attendee.NEEDS_ACTION);
					break;
				case TENTATIVE:
					att.setStatus(com.funambol.common.pim.calendar.Attendee.TENTATIVE);
					break;
				default:
					att.setStatus(com.funambol.common.pim.calendar.Attendee.UNKNOWN);
					break;
				}
				return att;
			}
		});
	}

	private void appendRecurence(Event event,
			com.funambol.common.pim.calendar.Event foundation) throws ConvertionException {
		EventRecurrence recurrence = null;
		if (foundation.isRecurrent()) {
			recurrence = getRecurrenceFromFoundation(foundation
					.getRecurrencePattern(), foundation.isAllDay());
		} else {
			recurrence = new EventRecurrence();
			recurrence.setKind(RecurrenceKind.none);
			recurrence.setDays("");
			recurrence.setFrequence(1);
		}
		event.setRecurrence(recurrence);
	}
	
	// Foundation to obm

	/**
	 * Construct an OBM event recurrence from a foundation recurrence
	 * 
	 * @param rec
	 * @param dend
	 * @param allDay
	 * @return
	 * @throws ConvertionException 
	 */
	public EventRecurrence getRecurrenceFromFoundation(
			RecurrencePattern rec, boolean allDay) throws ConvertionException {
		EventRecurrence recurrence = new EventRecurrence();

		recurrence.setFrequence(rec.getInterval());
		recurrence.setDays("");

		List<ExceptionToRecurrenceRule> recexs = rec.getExceptions();
		if (recexs != null) {
			Set<Date> exs = Sets.newHashSet();
			for (ExceptionToRecurrenceRule exceptionToRecurrenceRule : recexs) {
				Date date = getDateFromUTCString(exceptionToRecurrenceRule
						.getDate());
				exs.add(date);
			}
			recurrence.setExceptions(exs.toArray(new Date[exs.size()]));
		} else {
			recurrence.setExceptions(new Date[0]);
		}

		java.util.Calendar cEndRec = java.util.Calendar.getInstance();
		logger.info("recurrence: " + rec);
		if (rec.getOccurrences() > 0) {
			Date begin = getDateFromUTCString(rec.getStartDatePattern());
			short type = rec.getTypeId();
			Calendar endTime = Calendar
					.getInstance(TimeZone.getTimeZone("GMT"));
			endTime.setTime(begin);
			switch (type) {
			case RecurrencePattern.TYPE_DAYLY:
				endTime.add(Calendar.DAY_OF_MONTH, (rec.getOccurrences() - 1)
						* rec.getInterval());
				break;
			case RecurrencePattern.TYPE_MONTHLY:
			case RecurrencePattern.TYPE_MONTH_NTH:
				endTime.add(Calendar.MONTH, (rec.getOccurrences() - 1)
						* rec.getInterval());
				break;
			case RecurrencePattern.TYPE_WEEKLY:
				endTime.add(Calendar.WEEK_OF_YEAR, (rec.getOccurrences() - 1)
						* rec.getInterval());
				break;
			case RecurrencePattern.TYPE_YEARLY:
			case RecurrencePattern.TYPE_YEAR_NTH:
				endTime.add(Calendar.YEAR, (rec.getOccurrences() - 1)
						* rec.getInterval());
				break;
			}
			// funambol perd la tz en calculant la startDatePattern : le 19 à
			// 23h utc (20 à 0h sur paris), devient le 19
			if (allDay) {
				endTime.add(Calendar.DAY_OF_YEAR, 1);
			}
			logger.info("Computed end date : " + endTime.getTime());
			cEndRec.setTime(endTime.getTime());
		} else if (!rec.isNoEndDate()) {
			Date dEndRec = getDateFromUTCString(rec.getEndDatePattern());
			cEndRec.setTime(dEndRec);
		} else {
			/* infinite */
			cEndRec.set(Calendar.YEAR, 2017);
			// cEndRec = null;
		}
		recurrence.setEnd(cEndRec.getTime());

		switch (rec.getTypeId()) {
		case RecurrencePattern.TYPE_DAYLY:
			recurrence.setKind(RecurrenceKind.daily);
			break;
		case RecurrencePattern.TYPE_WEEKLY:
			recurrence.setKind(RecurrenceKind.weekly);
			recurrence.setDays(getOBMDayOfWeekMask(rec.getDayOfWeekMask()));
			break;
		case RecurrencePattern.TYPE_MONTHLY:
			recurrence.setKind(RecurrenceKind.monthlybydate);
			break;
		case RecurrencePattern.TYPE_MONTH_NTH:
			// only one nth day supported by OBM
			recurrence.setKind(RecurrenceKind.monthlybyday);
			break;
		case RecurrencePattern.TYPE_YEARLY:
			recurrence.setKind(RecurrenceKind.yearly);
			break;
		case RecurrencePattern.TYPE_YEAR_NTH:
			// not supported by OBM
			recurrence.setKind(RecurrenceKind.yearly);
			break;
		}

		return recurrence;
	}
	
	private static String getOBMDayOfWeekMask(short dayOfWeekMask) {
		String result = "";

		for (int i = 0; i < 7; i++) {
			if ((dayOfWeekMask & foundationWeekDays[i]) == foundationWeekDays[i]) {
				result += "1";
			} else {
				result += "0";
			}
		}
		return result;
	}

	private void appendAttendees(Event event,
			com.funambol.common.pim.calendar.Event foundation, String userEmail) {
		List<Attendee> obmAttendees = getObmAttendees(foundation.getAttendees());
		event.getAttendees().addAll(obmAttendees);
		addOwnerAsAttendeeIfNotExist(userEmail, event);
	}

	private void appendOpacity(Event event,
			com.funambol.common.pim.calendar.Event foundation) {
		if (isNotEmptyProperties(foundation.getTransp()) && "1".equals(foundation.getTransp().getPropertyValueAsString())) {
			event.setOpacity(EventOpacity.TRANSPARENT);
		} else {
			event.setOpacity(EventOpacity.OPAQUE);
		}
	}

	private void appendPrivacy(Event event,
			com.funambol.common.pim.calendar.Event foundation) {
		if (isNotEmptyProperties(foundation.getAccessClass()) && "0".equals(foundation.getAccessClass().getPropertyValueAsString())) {
			event.setPrivacy(0); // public
		} else {
			event.setPrivacy(1); // private
		}
	}

	private void appendPriority(Event event,
			com.funambol.common.pim.calendar.Event foundation) {
		if (foundation.getPriority() != null) {
			event.setPriority(getPriorityFromFoundation(foundation
					.getPriority().getPropertyValueAsString()));
		} else {
			event.setPriority(new Integer(1));
		}
	}
	
	public Integer getPriorityFromFoundation(String priority) {
		Integer value = 1;
		
		if ( !"".equals(StringUtils.trimToEmpty(priority)) ) {
			try {
				value = Integer.parseInt(priority)+1;
			} catch (NumberFormatException nfe) {
			}
		}
		return value;
	}

	private void appendLocation(Event event,
			com.funambol.common.pim.calendar.Event foundation) {
		if (isNotEmptyProperties(foundation.getLocation())) {
			String location = sanitazeString(foundation.getLocation().getPropertyValueAsString());
			event.setLocation(location);
		}
	}

	private void appendCategories(Event event,
			com.funambol.common.pim.calendar.Event foundation) {
		if (isNotEmptyProperties(foundation.getCategories())) {
			event.setCategory(getOneCategory(foundation
					.getCategories().getPropertyValueAsString()));
		}
	}
	
	/**
	 * Return the first category
	 * 
	 * @param propertyValueAsString
	 * @return
	 */
	public static String getOneCategory(String categories) {
		String ret = "";
		if (categories != null) {
			String[] result = categories.split(";|,");
			if (result.length > 0) {
				ret = result[0];
			}
		}
		return ret;
	}

	private void appendDescription(Event event,
			com.funambol.common.pim.calendar.Event foundation) {
		if (isNotEmptyProperties(foundation.getDescription())) {
			event.setDescription(foundation.getDescription()
					.getPropertyValueAsString());
		}
		
	}

	private void appendTitle(Event event,
			com.funambol.common.pim.calendar.Event foundation) {
		if (isNotEmptyProperties(foundation.getSummary())) {
			String title = sanitazeString(foundation.getSummary().getPropertyValueAsString());
			event.setTitle(title);
		} else {
			event.setTitle("[Sans titre]");
		}
	}

	private void appendAlert(Event event,
			com.funambol.common.pim.calendar.Event foundation) {
		if (foundation.getReminder() != null
				&& foundation.getReminder().getMinutes() != 0) {
			event.setAlert(foundation.getReminder().getMinutes() * 60);
		} else {
			event.setAlert(0);
		}
	}

	private void appendDuration(Event event,
			com.funambol.common.pim.calendar.Event foundation) throws ConvertionException {
		Date dstart = getDateFromProperty(foundation.getDtStart());
		Date dend = null;
		if(!Property.isEmptyProperty(foundation.getDtEnd())){
			dend = getDateFromProperty(foundation.getDtEnd());
		}
		if (dend != null && dstart.getTime() != dend.getTime()) {
			event.setDuration((int) ((dend.getTime() - dstart.getTime()) / 1000));
		} else {
			event.setDuration(3600);
		}
	}

	private void appendDtStart(Event event,
			com.funambol.common.pim.calendar.Event foundation) throws ConvertionException {
		if(Property.isEmptyProperty(foundation.getDtStart())){
			throw new ConvertionException("DtStart in a funis event cannot be null");
		}
		Date dstart = getDateFromProperty(foundation.getDtStart());
		event.setDate(dstart);
	}

	private void appendAllday(Event event,
			com.funambol.common.pim.calendar.Event foundation) {
		event.setAllday(foundation.isAllDay());
	}

	private void appendUid(Event event,
			com.funambol.common.pim.calendar.Event foundation) {
		if (isNotEmptyProperties(foundation.getUid())) {
			EventExtId id = new EventExtId(foundation.getUid().getPropertyValueAsString());
			event.setExtId(id);
		}
	}

	private boolean isNotEmptyProperties(Property prop) {
		return prop != null && StringUtils.isNotBlank(prop.getPropertyValueAsString()) &&
				!"NULL".equalsIgnoreCase(prop.getPropertyValueAsString());
	}
	
	private void addOwnerAsAttendeeIfNotExist(String userEmail, Event event) {
		for (Attendee at: event.getAttendees()) {
			if (userEmail.equals(at.getEmail())) {
				return ;
			} 
		}
		Attendee syncingUser = new Attendee();
		syncingUser.setRequired(ParticipationRole.CHAIR);
		syncingUser.setState(ParticipationState.ACCEPTED);
		syncingUser.setEmail(userEmail);
		event.getAttendees().add(syncingUser);
	}

	private List<Attendee> getObmAttendees(
			List<com.funambol.common.pim.calendar.Attendee> attendees) {
		
		return Lists.transform(attendees, new Function<com.funambol.common.pim.calendar.Attendee, Attendee>() {
			@Override
			public Attendee apply(
					com.funambol.common.pim.calendar.Attendee input) {
					return convertAttendee(input);
				}
		});
	}

	protected Attendee convertAttendee(
			com.funambol.common.pim.calendar.Attendee attendee) {
		Attendee obmAttendee = new Attendee();
		obmAttendee.setDisplayName(attendee.getName());
		obmAttendee.setEmail(attendee.getEmail());
		
		switch (attendee.getRole()) {
		case com.funambol.common.pim.calendar.Attendee.ORGANIZER:
			obmAttendee.setOrganizer(true);
			break;
		case com.funambol.common.pim.calendar.Attendee.OWNER:
			obmAttendee.setOrganizer(true);
			break;
		}
		
		switch (attendee.getStatus()) {
		case com.funambol.common.pim.calendar.Attendee.DECLINED:
			obmAttendee.setState(ParticipationState.DECLINED);
			break;
		case com.funambol.common.pim.calendar.Attendee.TENTATIVE:
			obmAttendee.setState(ParticipationState.TENTATIVE);
			break;
		case com.funambol.common.pim.calendar.Attendee.ACCEPTED:
			obmAttendee.setState(ParticipationState.ACCEPTED);
			break;
		case com.funambol.common.pim.calendar.Attendee.IN_PROCESS:
			obmAttendee.setState(ParticipationState.INPROGRESS);
			break;
		case com.funambol.common.pim.calendar.Attendee.COMPLETED:
			obmAttendee.setState(ParticipationState.COMPLETED);
			break;
		}
		
		switch (attendee.getExpected()) {
		case com.funambol.common.pim.calendar.Attendee.NON_PARTICIPANT:
			obmAttendee.setRequired(ParticipationRole.NON);
			break;
		case com.funambol.common.pim.calendar.Attendee.OPTIONAL:
			obmAttendee.setRequired(ParticipationRole.OPT);
			break; 
		case com.funambol.common.pim.calendar.Attendee.REQUIRED:
			obmAttendee.setRequired(ParticipationRole.REQ);
			break; 
		case com.funambol.common.pim.calendar.Attendee.CHAIRMAN:
			obmAttendee.setRequired(ParticipationRole.CHAIR);
			break; 
		default:
			obmAttendee.setRequired(ParticipationRole.REQ);
		}
		
		return obmAttendee;
	}

}
