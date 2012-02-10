package org.obm.push;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.obm.push.bean.AttendeeStatus;
import org.obm.push.bean.AttendeeType;
import org.obm.push.bean.CalendarBusyStatus;
import org.obm.push.bean.CalendarSensitivity;
import org.obm.push.bean.MSAttendee;
import org.obm.push.bean.MSEvent;
import org.obm.push.bean.MSEventCommon;
import org.obm.push.bean.MSEventException;
import org.obm.push.bean.MSEventUid;
import org.obm.push.bean.Recurrence;
import org.obm.push.bean.RecurrenceDayOfWeek;
import org.obm.push.bean.RecurrenceType;
import org.obm.push.bean.User;
import org.obm.sync.calendar.Attendee;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventOpacity;
import org.obm.sync.calendar.EventPrivacy;
import org.obm.sync.calendar.EventRecurrence;
import org.obm.sync.calendar.ParticipationRole;
import org.obm.sync.calendar.ParticipationState;
import org.obm.sync.calendar.RecurrenceKind;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class ObmEventToMsEventConverter {

	public MSEvent convert(Event e, MSEventUid uid, User user) {
		MSEvent mse = new MSEvent();

		fillEventCommonProperties(e, mse);
		
		mse.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		appendAttendeesAndOrganizer(e, mse, user);
		mse.setUid(uid);
		mse.setRecurrence(getRecurrence(e.getRecurrence()));
		mse.setExceptions(getException(e.getRecurrence()));
		mse.setExtId(e.getExtId());
		mse.setObmId(e.getObmId());
		mse.setObmSequence(e.getSequence());
		appendCreatedLastUpdate(mse, e);

		return mse;
	}

	private void fillEventCommonProperties(Event e, MSEventCommon mse) {
		mse.setSubject(e.getTitle());
		mse.setDescription(e.getDescription());
		mse.setLocation(e.getLocation());
		mse.setStartTime(e.getStartDate());
		
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		c.setTimeInMillis(e.getStartDate().getTime());
		c.add(Calendar.SECOND, e.getDuration());
		mse.setEndTime(c.getTime());
		
		mse.setAllDayEvent(e.isAllday());
		

		if (e.getAlert() != null && e.getAlert() > 0) {
			mse.setReminder(e.getAlert() / 60);
		}
		mse.setBusyStatus(busyStatus(e.getOpacity()));
		mse.setSensitivity(sensitivity(e.getPrivacy()));

	}

	private MSEventException convertException(Event exception) {
		MSEventException msEventException = new MSEventException();
		fillEventCommonProperties(exception, msEventException);
		msEventException.setExceptionStartTime(exception.getRecurrenceId());

		return msEventException;
	}
	
	private void appendAttendeesAndOrganizer(Event e, MSEvent mse, User user) {
		String userEmail = user.getEmail();
		boolean hasOrganizer = false;
		for (Attendee at: e.getAttendees()) {
			if (at.isOrganizer()) {
				hasOrganizer = true;
				appendOrganizer(mse, at);
			} 
			if (!hasOrganizer && userEmail.equalsIgnoreCase(at.getEmail())) {
				appendOrganizer(mse, at);
			}
			mse.addAttendee(convertAttendee(at));
		}
	}

	private void appendOrganizer(MSEvent mse, Attendee at) {
		mse.setOrganizerName(at.getDisplayName());
		mse.setOrganizerEmail(at.getEmail());		
	}

	private void appendCreatedLastUpdate(MSEvent mse, Event e) {
		mse.setCreated(e.getTimeCreate() != null ? e.getTimeCreate() : new Date());
		mse.setLastUpdate(e.getTimeUpdate() != null ? e.getTimeUpdate() : new Date());
		mse.setDtStamp(mse.getLastUpdate());
	}

	@VisibleForTesting CalendarSensitivity sensitivity(EventPrivacy privacy) {
		Preconditions.checkNotNull(privacy);
		switch (privacy) {
		case PRIVATE:
			return CalendarSensitivity.PRIVATE;
		case PUBLIC:
			return CalendarSensitivity.NORMAL;
		}
		throw new IllegalArgumentException("EventPrivacy " + privacy + " can't be converted to MSEvent property");
	}

	@VisibleForTesting CalendarBusyStatus busyStatus(EventOpacity opacity) {
		Preconditions.checkNotNull(opacity);
		switch (opacity) {
		case TRANSPARENT:
			return CalendarBusyStatus.FREE;
		case OPAQUE:
			return CalendarBusyStatus.BUSY;
		}
		throw new IllegalArgumentException("EventOpacity " + opacity + " can't be converted to MSEvent property");
	}
	
	private MSAttendee convertAttendee(Attendee at) {
		MSAttendee msa = new MSAttendee();

		msa.setAttendeeStatus(status(at.getState()));
		msa.setEmail(at.getEmail());
		msa.setName(at.getDisplayName());
		msa.setAttendeeType(participationRole(at.getParticipationRole()));

		return msa;
	}

	@VisibleForTesting AttendeeStatus status(ParticipationState state) {
		Preconditions.checkNotNull(state);
		switch (state) {
		case DECLINED:
			return AttendeeStatus.DECLINE;
		case NEEDSACTION:
			return AttendeeStatus.NOT_RESPONDED;
		case TENTATIVE:
			return AttendeeStatus.TENTATIVE;
		case ACCEPTED:
			return AttendeeStatus.ACCEPT;
		default:
		case COMPLETED:
		case DELEGATED:
		case INPROGRESS:
			return AttendeeStatus.RESPONSE_UNKNOWN;
		}
	}

	@VisibleForTesting AttendeeType participationRole(ParticipationRole role) {
		Preconditions.checkNotNull(role);
		switch (role) {
		case REQ:
		case CHAIR:
			return AttendeeType.REQUIRED;
		case NON:
		case OPT:
			return AttendeeType.OPTIONAL;
		}
		throw new IllegalArgumentException("ParticipationRole " + role + " can't be converted to MSEvent property");
	}

	private Recurrence getRecurrence(EventRecurrence recurrence) {
		if (recurrence == null || recurrence.getKind() == RecurrenceKind.none) {
			return null;
		}

		Recurrence r = new Recurrence();
		switch (recurrence.getKind()) {
		case daily:
			r.setType(RecurrenceType.DAILY);
			break;
		case monthlybydate:
			r.setType(RecurrenceType.MONTHLY);
			break;
		case monthlybyday:
			r.setType(RecurrenceType.MONTHLY_NDAY);
			break;
		case weekly:
			r.setType(RecurrenceType.WEEKLY);
			r.setDayOfWeek(daysOfWeek(recurrence.getDays()));
			break;
		case yearly:
			r.setType(RecurrenceType.YEARLY);
			break;
		case none:
			r.setType(null);
			break;
		}
		r.setUntil(recurrence.getEnd());

		r.setInterval(recurrence.getFrequence());

		return r;
	}


	private Set<RecurrenceDayOfWeek> daysOfWeek(String string) {
		char[] days = string.toCharArray();
		Set<RecurrenceDayOfWeek> daysList = new HashSet<RecurrenceDayOfWeek>();
		int i = 0;
		if (days[i++] == '1') {
			daysList.add(RecurrenceDayOfWeek.SUNDAY);
		}
		if (days[i++] == '1') {
			daysList.add(RecurrenceDayOfWeek.MONDAY);
		}
		if (days[i++] == '1') {
			daysList.add(RecurrenceDayOfWeek.TUESDAY);
		}
		if (days[i++] == '1') {
			daysList.add(RecurrenceDayOfWeek.WEDNESDAY);
		}
		if (days[i++] == '1') {
			daysList.add(RecurrenceDayOfWeek.THURSDAY);
		}
		if (days[i++] == '1') {
			daysList.add(RecurrenceDayOfWeek.FRIDAY);
		}
		if (days[i++] == '1') {
			daysList.add(RecurrenceDayOfWeek.SATURDAY);
		}

		return daysList;
	}
	

	private List<MSEventException> getException(EventRecurrence recurrence) {
		List<MSEventException> ret = new LinkedList<MSEventException>();
		if(recurrence == null){
			return ret;
		}
		
		for (Date excp : recurrence.getExceptions()) {
			MSEventException e = new MSEventException();
			e.setDeleted(true);
			e.setExceptionStartTime(excp);
			e.setStartTime(excp);
			e.setDtStamp(new Date());
			ret.add(e);
		}

		for (Event exception : recurrence.getEventExceptions()) {
			MSEventException e = convertException(exception);
			ret.add(e);
		}
		return ret;
	}

}
