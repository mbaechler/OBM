package org.obm.push;

import static org.fest.assertions.Assertions.assertThat;
import static org.obm.DateUtils.date;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.obm.push.bean.AttendeeStatus;
import org.obm.push.bean.AttendeeType;
import org.obm.push.bean.CalendarBusyStatus;
import org.obm.push.bean.CalendarMeetingStatus;
import org.obm.push.bean.CalendarSensitivity;
import org.obm.push.bean.MSAttendee;
import org.obm.push.bean.MSEvent;
import org.obm.push.bean.MSEventException;
import org.obm.push.bean.MSEventUid;
import org.obm.push.bean.PIMDataType;
import org.obm.push.bean.MSRecurrence;
import org.obm.push.bean.RecurrenceType;
import org.obm.push.bean.User;
import org.obm.sync.calendar.Attendee;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventExtId;
import org.obm.sync.calendar.EventObmId;
import org.obm.sync.calendar.EventOpacity;
import org.obm.sync.calendar.EventPrivacy;
import org.obm.sync.calendar.EventRecurrence;
import org.obm.sync.calendar.EventType;
import org.obm.sync.calendar.ParticipationRole;
import org.obm.sync.calendar.ParticipationState;
import org.obm.sync.calendar.RecurrenceKind;

import com.google.common.collect.Iterables;

public class ObmEventToMsEventConverterTest {

	private ObmEventToMsEventConverter converter;

	@Before
	public void setUp() {
		converter = new ObmEventToMsEventConverter();
	}

	private Event basicEvent() {
		Event event = new Event();
		event.setAlert(120);
		event.setAllday(false);
		event.setCategory("category");
		event.setCreatorDisplayName("creatorDisplayName");
		event.setCreatorEmail("creator@email");
		event.setDescription("description");
		event.setDuration(3600);
		event.setExtId(new EventExtId("36cb5540-aa8c-4c89-9dfc-3dff56c68df2"));
		event.setInternalEvent(false);
		event.setLocation("Lyon, France");
		event.setOpacity(EventOpacity.OPAQUE);
		event.setOwner("owner@id");
		event.setOwnerDisplayName("owner display name");
		event.setOwnerEmail("owner@email");
		event.setPriority(1);
		event.setPrivacy(EventPrivacy.PRIVATE);
		event.setSequence(1);
		event.setStartDate(date("2004-12-13T21:39:45Z"));
		event.setTimeCreate(date("2004-12-11T10:10:10Z"));
		event.setTimeUpdate(date("2004-12-11T11:15:10Z"));
		event.setTimezoneName("Europe/Paris");
		event.setTitle("title");
		event.setType(EventType.VEVENT);
		event.setUid(new EventObmId(121));
		event.addAttendees(Arrays.asList(
				new Attendee.Builder().withEmail("jaures@sfio.fr")
					.withDisplayName("Jean Jaures")
					.withParticipationState(ParticipationState.ACCEPTED)
					.withParticipationRole(ParticipationRole.REQ)
					.asOrganizer().build(),
				new Attendee.Builder().withEmail("blum@sfio.fr")
					.withDisplayName("Léon Blum")
					.withParticipationState(ParticipationState.NEEDSACTION)
					.withParticipationRole(ParticipationRole.OPT)
					.asAttendee().build()));
		return event;
	}
	
	private User jauresUser() {
		return User.Factory.create().createUser("jaures@domain", "jaures@sfio.fr", "Jean Jaures");
	}

	@Test
	public void testSimpleObmEvent() {
		Event event = basicEvent();
		User jaures = jauresUser();
		MSEvent msEvent = converter.convert(event, new MSEventUid("mseventuid"), jaures);
		
		assertThat(msEvent).isNotNull();
		assertThat(msEvent.getAllDayEvent()).isFalse();
		assertThat(msEvent.getAttendees()).containsOnly(
				new MSAttendee.Builder().withEmail("jaures@sfio.fr")
					.withName("Jean Jaures")
					.withStatus(AttendeeStatus.ACCEPT)
					.withType(AttendeeType.REQUIRED).build(),
				new MSAttendee.Builder().withEmail("blum@sfio.fr")
					.withName("Léon Blum")
					.withStatus(AttendeeStatus.NOT_RESPONDED)
					.withType(AttendeeType.OPTIONAL).build());
		assertThat(msEvent.getBusyStatus()).isEqualTo(CalendarBusyStatus.BUSY);
		assertThat(msEvent.getCategories()).containsOnly("category");
		assertThat(msEvent.getDescription()).isEqualTo("description");
		assertThat(msEvent.getDtStamp()).isEqualTo(date("2004-12-11T11:15:10Z"));
		assertThat(msEvent.getEndTime()).isEqualTo(date("2004-12-13T22:39:45Z"));
		assertThat(msEvent.getExceptions()).isEmpty();
		assertThat(msEvent.getExtId()).isEqualTo(new EventExtId("36cb5540-aa8c-4c89-9dfc-3dff56c68df2"));
		assertThat(msEvent.getLocation()).isEqualTo("Lyon, France");
		assertThat(msEvent.getMeetingStatus()).isEqualTo(CalendarMeetingStatus.IS_A_MEETING);
		assertThat(msEvent.getObmId()).isEqualTo(new EventObmId(121));
		assertThat(msEvent.getObmSequence()).isEqualTo(1);
		assertThat(msEvent.getOrganizerEmail()).isEqualTo("jaures@sfio.fr");
		assertThat(msEvent.getOrganizerName()).isEqualTo("Jean Jaures");
		assertThat(msEvent.getRecurrence()).isNull();
		assertThat(msEvent.getReminder()).isEqualTo(2);
		assertThat(msEvent.getSensitivity()).isEqualTo(CalendarSensitivity.PRIVATE);
		assertThat(msEvent.getStartTime()).isEqualTo(date("2004-12-13T21:39:45Z"));
		assertThat(msEvent.getSubject()).isEqualTo("title");
		assertThat(msEvent.getTimeZone()).isEqualTo(TimeZone.getTimeZone("Europe/Paris"));
		assertThat(msEvent.getType()).isEqualTo(PIMDataType.CALENDAR);
		assertThat(msEvent.getUid()).isEqualTo(new MSEventUid("mseventuid"));
	}
	
	@Test
	public void testNullAlertEvent() {
		Event event = basicEvent();
		event.setAlert(null);
		User jaures = jauresUser();
		MSEvent msEvent = converter.convert(event, new MSEventUid("mseventuid"), jaures);
		assertThat(msEvent.getReminder()).isNull();
	}
	
	@Test
	public void testAllDayEvent() {
		Event event = basicEvent();
		event.setAllday(true);
		User jaures = jauresUser();
		MSEvent msEvent = converter.convert(event, new MSEventUid("mseventuid"), jaures);
		assertThat(msEvent.getAllDayEvent()).isEqualTo(true);
		assertThat(msEvent.getEndTime()).isEqualTo(date("2004-12-14T21:39:45Z"));
	}
	
	@Test
	public void testDailyRecurrence() {
		Event event = basicEvent();
		EventRecurrence eventRecurrence = new EventRecurrence();
		eventRecurrence.setKind(RecurrenceKind.daily);
		event.setRecurrence(eventRecurrence);
		User jaures = jauresUser();
		
		MSEvent msEvent = converter.convert(event, new MSEventUid("mseventuid"), jaures);
		MSRecurrence msRecurrence = msEvent.getRecurrence();
		
		assertThat(msRecurrence.getDayOfMonth()).isNull();
		assertThat(msRecurrence.getDayOfWeek()).isNull();
		assertThat(msRecurrence.getDeadOccur()).isNull();
		assertThat(msRecurrence.getInterval()).isEqualTo(0);
		assertThat(msRecurrence.getMonthOfYear()).isNull();
		assertThat(msRecurrence.getOccurrences()).isNull();
		assertThat(msRecurrence.getRegenerate()).isNull();
		assertThat(msRecurrence.getStart()).isNull();
		assertThat(msRecurrence.getType()).isEqualTo(RecurrenceType.DAILY);
		assertThat(msRecurrence.getUntil()).isNull();
		assertThat(msRecurrence.getWeekOfMonth()).isNull();
	}
	
	@Test
	public void testDailyRecurrenceDeletionException() {
		Event event = basicEvent();
		EventRecurrence eventRecurrence = new EventRecurrence();
		eventRecurrence.setKind(RecurrenceKind.daily);
		eventRecurrence.addException(date("2004-12-14T21:39:45Z"));
		event.setRecurrence(eventRecurrence);
		User jaures = jauresUser();
		MSEvent msEvent = converter.convert(event, new MSEventUid("mseventuid"), jaures);
		
		List<MSEventException> exceptions = msEvent.getExceptions();
		assertThat(exceptions).hasSize(1);
		MSEventException deletedException = Iterables.getOnlyElement(exceptions);
		assertThat(deletedException.isDeletedException()).isTrue();
		assertThat(deletedException.getExceptionStartTime()).isEqualTo(date("2004-12-14T21:39:45Z"));
		assertThat(deletedException.getAllDayEvent()).isNull();
		assertThat(deletedException.getBusyStatus()).isNull();
		assertThat(deletedException.getCategories()).isNull();
		assertThat(deletedException.getDescription()).isNull();
		assertThat(deletedException.getDtStamp()).isNull();
		assertThat(deletedException.getEndTime()).isNull();
		assertThat(deletedException.getLocation()).isNull();
		assertThat(deletedException.getMeetingStatus()).isNull();
		assertThat(deletedException.getReminder()).isNull();
		assertThat(deletedException.getSensitivity()).isNull();
		assertThat(deletedException.getSubject()).isNull();
		assertThat(deletedException.getStartTime()).isNull();
	}
	
	@Test
	public void testNoneRecurrenceWithDeletionException() {
		Event event = basicEvent();
		EventRecurrence eventRecurrence = new EventRecurrence();
		eventRecurrence.setKind(RecurrenceKind.none);
		eventRecurrence.addException(date("2004-12-14T21:39:45Z"));
		event.setRecurrence(eventRecurrence);
		User jaures = jauresUser();
		MSEvent msEvent = converter.convert(event, new MSEventUid("mseventuid"), jaures);
		
		List<MSEventException> exceptions = msEvent.getExceptions();
		assertThat(exceptions).isEmpty();
	}
	
	@Test
	public void testDailyMondyWithTuesdayException() {
		Event event = basicEvent();
		EventRecurrence eventRecurrence = new EventRecurrence();
		eventRecurrence.setKind(RecurrenceKind.daily);
		eventRecurrence.setDays("0100000");
		Event secondOccurence = new Event();
		secondOccurence.setRecurrenceId(date("2004-12-20T21:39:45Z"));
		secondOccurence.setStartDate(date("2004-12-21T21:39:45Z"));
		eventRecurrence.addEventException(secondOccurence);
		event.setRecurrence(eventRecurrence);
		User jaures = jauresUser();
		MSEvent msEvent = converter.convert(event, new MSEventUid("mseventuid"), jaures);
		List<MSEventException> exceptions = msEvent.getExceptions();
		assertThat(exceptions).hasSize(1);
		MSEventException exception = Iterables.getOnlyElement(exceptions);
		assertThat(exception.getAllDayEvent()).isFalse();
		assertThat(exception.getBusyStatus()).isNull();
		assertThat(exception.getCategories()).isNull();
		assertThat(exception.getDescription()).isNull();
		assertThat(exception.getDtStamp()).isNull();
		assertThat(exception.getEndTime()).isEqualTo(date("2004-12-20T22:39:45Z"));
	}
}
