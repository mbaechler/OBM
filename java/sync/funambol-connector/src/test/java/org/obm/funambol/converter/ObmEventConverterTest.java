package org.obm.funambol.converter;

import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.obm.funambol.converter.ObmEventConverter;
import org.obm.funambol.exception.ConvertionException;
import org.obm.sync.calendar.Attendee;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventObmId;
import org.obm.sync.calendar.EventOpacity;
import org.obm.sync.calendar.ParticipationRole;
import org.obm.sync.calendar.ParticipationState;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.common.Property;


public class ObmEventConverterTest {
	
	@Test
	public void testConvertObmId() throws ConvertionException{
		Integer obmId = 10;
		Event event = getMinimalEvent();
		EventObmId id = new EventObmId(obmId);
		event.setUid(id);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertNotNull(funisEvent.getEvent().getUid());
		Assert.assertEquals(obmId.toString(), funisEvent.getEvent().getUid().getPropertyValueAsString());
	}
	
	@Test(expected=ConvertionException.class)
	public void testConvertNullObmId() throws ConvertionException{
		Event event = getMinimalEvent();
		event.setUid(null);
		
		ObmEventConverter converter = new ObmEventConverter();
		converter.obmEventToFoundationCalendar(event);
	}
	
	@Test
	public void testConvertObmStart() throws ConvertionException{
		Event event = getMinimalEvent();
		java.util.Calendar start = java.util.Calendar.getInstance();
		start.set(2012, java.util.Calendar.FEBRUARY, 7, 16, 30, 0);
		event.setDate(start.getTime());
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertNotNull(funisEvent.getEvent().getDtStart());
		Assert.assertEquals("20120207T153000Z", funisEvent.getEvent().getDtStart().getPropertyValueAsString());
	}
	
	@Test(expected=ConvertionException.class)
	public void testConvertNullObmStart() throws ConvertionException{
		Event event = getMinimalEvent();
		event.setDate(null);
		
		ObmEventConverter converter = new ObmEventConverter();
		converter.obmEventToFoundationCalendar(event);
	}
	
	@Test
	public void testConvertObmEnd() throws ConvertionException{
		Event event = getMinimalEvent();
		java.util.Calendar start = java.util.Calendar.getInstance();
		start.set(2012, java.util.Calendar.FEBRUARY, 7, 16, 30, 0);
		event.setDate(start.getTime());
		event.setDuration(3600);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertNotNull(funisEvent.getEvent().getDtStart());
		Assert.assertEquals("20120207T163000Z", funisEvent.getEvent().getDtEnd().getPropertyValueAsString());
	}
	
	@Test(expected=ConvertionException.class)
	public void testConvertObmNoAllDayWithNullDuration() throws ConvertionException{
		Event event = getMinimalEvent();
		java.util.Calendar start = java.util.Calendar.getInstance();
		start.set(2012, java.util.Calendar.FEBRUARY, 7, 16, 30, 0);
		event.setDate(start.getTime());
		event.setDuration(0);
		event.setAllday(false);
		
		ObmEventConverter converter = new ObmEventConverter();
		converter.obmEventToFoundationCalendar(event);
		
	}
	
	@Test
	public void testConvertObmNoAllDay() throws ConvertionException{
		Event event = getMinimalEvent();
		java.util.Calendar start = java.util.Calendar.getInstance();
		start.set(2012, java.util.Calendar.FEBRUARY, 7, 16, 30, 0);
		event.setDate(start.getTime());
		event.setDuration(3600);
		event.setAllday(false);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertFalse(funisEvent.getEvent().isAllDay());
	}
	
	@Test
	public void testConvertObmAllDay() throws ConvertionException{
		Event event = getMinimalEvent();
		java.util.Calendar start = java.util.Calendar.getInstance();
		start.set(2012, java.util.Calendar.FEBRUARY, 7, 16, 30, 0);
		event.setDate(start.getTime());
		event.setAllday(true);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertTrue(funisEvent.getEvent().isAllDay());
		Assert.assertEquals("20120207", funisEvent.getEvent().getDtStart().getPropertyValueAsString());
		Assert.assertEquals("20120207", funisEvent.getEvent().getDtEnd().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmAllTwoDay() throws ConvertionException{
		Event event = getMinimalEvent();
		java.util.Calendar start = java.util.Calendar.getInstance();
		start.set(2012, java.util.Calendar.FEBRUARY, 7, 16, 30, 0);
		event.setDate(start.getTime());
		event.setDuration(172800);
		event.setAllday(true);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertTrue(funisEvent.getEvent().isAllDay());
		Assert.assertEquals("20120207", funisEvent.getEvent().getDtStart().getPropertyValueAsString());
		Assert.assertEquals("20120209", funisEvent.getEvent().getDtEnd().getPropertyValueAsString());
	}
	
	
	
	@Test
	public void testConvertObmAlert() throws ConvertionException{
		Event event = getMinimalEvent();
		event.setAlert(120);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals(2,funisEvent.getEvent().getReminder().getMinutes());
		Assert.assertTrue(funisEvent.getEvent().getReminder().isActive());
	}
	
	@Test
	public void testConvertObmEmptyAlert() throws ConvertionException{
		Event event = getMinimalEvent();
		event.setAlert(null);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertFalse(funisEvent.getEvent().getReminder().isActive());
	}
	
	@Test
	public void testConvertObmTitle() throws ConvertionException{
		final String title = "tttt1";
		
		Event event = getMinimalEvent();
		event.setTitle(title);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals(title,funisEvent.getEvent().getSummary().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmUnsanitizeTitle() throws ConvertionException{
		final String title = "t\n. ttt1.\r\n zefgyzefy.\n";
		
		Event event = getMinimalEvent();
		event.setTitle(title);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals("t. ttt1. zefgyzefy.",funisEvent.getEvent().getSummary().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmEmptyTitle() throws ConvertionException{
		Event event = getMinimalEvent();
		event.setTitle(null);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertNull(funisEvent.getEvent().getSummary().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmDescription() throws ConvertionException{
		final String description = "tttt1";
		
		Event event = getMinimalEvent();
		event.setDescription(description);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals(description,funisEvent.getEvent().getDescription().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmEmptyDescription() throws ConvertionException{
		
		Event event = getMinimalEvent();
		event.setDescription(null);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertNull(funisEvent.getEvent().getDescription().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmLocation() throws ConvertionException{
		final String location = "Toulouse";
		
		Event event = getMinimalEvent();
		event.setLocation(location);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals(location,funisEvent.getEvent().getLocation().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmUnsanitizeLocation() throws ConvertionException{
		final String location = "t\n. Toulouse.\r\n zefgyzefy.\n";
		
		Event event = getMinimalEvent();
		event.setLocation(location);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals("t. Toulouse. zefgyzefy.",funisEvent.getEvent().getLocation().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmEmptyLocation() throws ConvertionException{
		Event event = getMinimalEvent();
		event.setLocation(null);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertNull(funisEvent.getEvent().getLocation().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmPriority() throws ConvertionException {
		Event event = getMinimalEvent();
		event.setPriority(3);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals("3", funisEvent.getEvent().getPriority().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmEmptyPriority() throws ConvertionException {
		Event event = getMinimalEvent();
		event.setPriority(null);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertNull(funisEvent.getEvent().getPriority().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmPrivacy() throws ConvertionException {
		Event event = getMinimalEvent();
		event.setPrivacy(1);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals("2", funisEvent.getEvent().getAccessClass().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmOpacityOpaque() throws ConvertionException {
		Event event = getMinimalEvent();
		event.setOpacity(EventOpacity.OPAQUE);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals("0", funisEvent.getEvent().getTransp().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmOpacityTransparent() throws ConvertionException {
		Event event = getMinimalEvent();
		event.setOpacity(EventOpacity.TRANSPARENT);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals("1", funisEvent.getEvent().getTransp().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmEmptyOpacity() throws ConvertionException {
		Event event = getMinimalEvent();
		event.setPriority(null);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertNull(funisEvent.getEvent().getPriority().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmEmptyStatus() throws ConvertionException {
		Event event = getMinimalEvent();
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		Assert.assertEquals("0", funisEvent.getEvent().getStatus().getPropertyValueAsString());
	}
	
	@Test
	public void testConvertObmOrganizer() throws ConvertionException{
		final String name = "Display Name";
		final String email = "dips@test.tlse";
		final boolean organizer = true;
		final ParticipationRole participationRole = ParticipationRole.OPT;
		final ParticipationState participationState = ParticipationState.ACCEPTED;
		
		
		Attendee att = createAttende(name, email, organizer, participationRole, participationState);
		
		Event event = getMinimalEvent();
		event.addAttendee(att);
		
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals(1, funisEvent.getEvent().getAttendees().size());
		
		com.funambol.common.pim.calendar.Attendee funisAtt = funisEvent.getEvent().getAttendees().iterator().next();
		
		assertAttendee(funisAtt, name, email, 
				com.funambol.common.pim.calendar.Attendee.OPTIONAL, 
				com.funambol.common.pim.calendar.Attendee.INDIVIDUAL, 
				com.funambol.common.pim.calendar.Attendee.ORGANIZER , 
				com.funambol.common.pim.calendar.Attendee.ACCEPTED);
	}

	@Test
	public void testConvertObmAttendee() throws ConvertionException{
		final String name = "Display Name";
		final String email = "dips@test.tlse";
		final boolean organizer = false;
		final ParticipationRole participationRole = ParticipationRole.REQ;
		final ParticipationState participationState = ParticipationState.DECLINED;
		
		Attendee att = createAttende(name, email, organizer, participationRole, participationState);
		
		Event event = getMinimalEvent();
		event.addAttendee(att);
		
		ObmEventConverter converter = new ObmEventConverter();
		Calendar funisEvent = converter.obmEventToFoundationCalendar(event);
		
		Assert.assertEquals(1, funisEvent.getEvent().getAttendees().size());
		com.funambol.common.pim.calendar.Attendee funisAtt = funisEvent.getEvent().getAttendees().iterator().next();
		assertAttendee(funisAtt, name, email, 
				com.funambol.common.pim.calendar.Attendee.REQUIRED, 
				com.funambol.common.pim.calendar.Attendee.INDIVIDUAL, 
				com.funambol.common.pim.calendar.Attendee.ATTENDEE , 
				com.funambol.common.pim.calendar.Attendee.DECLINED);
	}
	
	@Test
	public void testConvertUid() throws ConvertionException {
		final String email = "dips@test.tlse";
		String uid = "10";
		com.funambol.common.pim.calendar.Event event = getMinimalFunisEvent();
		event.setUid(new Property(uid.toString()));
		Calendar calendar = new Calendar();
		calendar.setEvent(event);
		
		ObmEventConverter converter = new ObmEventConverter();
		Event obmEvent = converter.foundationCalendarToObmEvent(calendar, email);
		
		Assert.assertEquals(uid, obmEvent.getObmId().serializeToString());
	}
	
	@Test
	public void testConvertStringUid() throws ConvertionException {
		final String email = "dips@test.tlse";
		String uid = UUID.randomUUID().toString();
		com.funambol.common.pim.calendar.Event event = getMinimalFunisEvent();
		event.setUid(new Property(uid));
		Calendar calendar = new Calendar();
		calendar.setEvent(event);
		
		ObmEventConverter converter = new ObmEventConverter();
		Event obmEvent = converter.foundationCalendarToObmEvent(calendar, email);
		
		Assert.assertNull(obmEvent.getObmId());
	}
	
	@Test
	public void testConvertFunisAttendee() throws ConvertionException {
		final String name = "Display Name";
		final String email = "dips@test.tlse";
		short expected = com.funambol.common.pim.calendar.Attendee.REQUIRED; 
		short role = com.funambol.common.pim.calendar.Attendee.ATTENDEE; 
		short status = com.funambol.common.pim.calendar.Attendee.DECLINED;
		
		com.funambol.common.pim.calendar.Attendee att = createFunisAttende(name, email, expected, role, status);
		
		com.funambol.common.pim.calendar.Event event = getMinimalFunisEvent();
		event.addAttendee(att);
		Calendar calendar = new Calendar();
		calendar.setEvent(event);
		
		ObmEventConverter converter = new ObmEventConverter();
		Event obmEvent = converter.foundationCalendarToObmEvent(calendar, email);
		
		Assert.assertEquals(1, obmEvent.getAttendees().size());
		Attendee obmAtt = obmEvent.getAttendees().iterator().next();
		assertAttendee(obmAtt, name, email, false, ParticipationRole.REQ, ParticipationState.DECLINED);
	}
	
	@Test
	public void testConvertFunisAttendeeWithoutOwnerAsAttendee() throws ConvertionException {
		final String userEmail = "adrien@test.tlse.lng";
		
		final String name = "Display Name";
		final String email = "dips@test.tlse";
		short expected = com.funambol.common.pim.calendar.Attendee.REQUIRED; 
		short role = com.funambol.common.pim.calendar.Attendee.ATTENDEE; 
		short status = com.funambol.common.pim.calendar.Attendee.DECLINED;
		
		com.funambol.common.pim.calendar.Attendee att = createFunisAttende(name, email, expected, role, status);
		
		com.funambol.common.pim.calendar.Event event = getMinimalFunisEvent();
		event.addAttendee(att);
		Calendar calendar = new Calendar();
		calendar.setEvent(event);
		
		ObmEventConverter converter = new ObmEventConverter();
		Event obmEvent = converter.foundationCalendarToObmEvent(calendar, userEmail);
		
		Assert.assertEquals(2, obmEvent.getAttendees().size());
	}
	
	@Test(expected=ConvertionException.class)
	public void testConvertFunisAttendeeWithoutDtStart() throws ConvertionException {
		final String userEmail = "adrien@test.tlse.lng";
		com.funambol.common.pim.calendar.Event event = getMinimalFunisEvent();
		event.setDtStart(null);
		Calendar calendar = new Calendar();
		calendar.setEvent(event);
		
		ObmEventConverter converter = new ObmEventConverter();
		Event obmEvent = converter.foundationCalendarToObmEvent(calendar, userEmail);
		
		Assert.assertEquals(2, obmEvent.getAttendees().size());
	}
	
	private void assertAttendee(Attendee obmAtt, String name, String email,
			boolean isOrganiser, ParticipationRole role, ParticipationState state) {
		Assert.assertEquals(name, obmAtt.getDisplayName());
		Assert.assertEquals(email, obmAtt.getEmail());
		Assert.assertEquals(isOrganiser, obmAtt.isOrganizer());
		Assert.assertEquals(role, obmAtt.getRequired());
		Assert.assertEquals(state, obmAtt.getState());
	}

	private com.funambol.common.pim.calendar.Attendee createFunisAttende(
			String name, String email, short expected, short role, short status) {
		com.funambol.common.pim.calendar.Attendee att = new com.funambol.common.pim.calendar.Attendee();
		att.setName(name);
		att.setEmail(email);
		att.setExpected(expected);
		att.setRole(role);
		att.setStatus(status);
		return att;
	}

	private void assertAttendee(com.funambol.common.pim.calendar.Attendee funisAtt, String name,
			String email, short expected, short kind , short role , short status) {
		Assert.assertEquals(name, funisAtt.getName());
		Assert.assertEquals(email, funisAtt.getEmail());
		Assert.assertEquals(expected, funisAtt.getExpected());
		Assert.assertEquals(kind, funisAtt.getKind());
		Assert.assertEquals(role, funisAtt.getRole());
		Assert.assertEquals(status, funisAtt.getStatus());
	}

	private Event getMinimalEvent() {
		Event event = new Event();
		event.setUid(new EventObmId(10));
		event.setDate(new Date());
		event.setDuration(3600);
		return event;
		
	}
	
	private com.funambol.common.pim.calendar.Event getMinimalFunisEvent() {
		com.funambol.common.pim.calendar.Event event = new com.funambol.common.pim.calendar.Event();
		Property start = new Property();
		start.setPropertyValue("19700329T020000");
		event.setDtStart(start);
		return event;
		
	}

	private Attendee createAttende(String name1, String email2, boolean organizer,
			ParticipationRole role, ParticipationState state) {
		Attendee att = new Attendee();
		att.setDisplayName(name1);
		att.setEmail(email2);
		att.setOrganizer(organizer);
		att.setRequired(role);
		att.setState(state);
		return att;
	}
}
