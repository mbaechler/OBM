/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.sync.items;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.obm.sync.calendar.Attendee;
import org.obm.sync.calendar.ContactAttendee;
import org.obm.sync.calendar.DeletedEvent;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventExtId;
import org.obm.sync.calendar.EventObmId;
import org.obm.sync.calendar.EventPrivacy;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class EventChangesTest {
	
	private Date lastSync;
	
	DeletedEvent deletedEvent1;
	DeletedEvent deletedEvent2;
	
	@Before
	public void setUp() {
		lastSync = new DateTime(2012, Calendar.APRIL, 25, 14, 0).toDate();
		deletedEvent1 = DeletedEvent.builder().eventObmId(1).eventExtId("deleted_event_1").build();
		deletedEvent2 = DeletedEvent.builder().eventObmId(2).eventExtId("deleted_event_2").build();
		
	}

	@Test
	public void testAnonymize() {
		Attendee attendee1 = ContactAttendee.builder().email("attendee1@email.com").build();
		Attendee attendee2 = ContactAttendee.builder().email("attendee2@email.com").build();
		
		ParticipationChanges participationChanges1 = ParticipationChanges.builder()
				.eventExtId("participation_changes_1")
				.eventObmId(3)
				.recurrenceId("recurrence_id_1")
				.attendees(Lists.newArrayList(attendee1, attendee2))
				.build();

		Attendee attendee3 = ContactAttendee.builder().email("attendee3@email.com").build();
		Attendee attendee4 = ContactAttendee.builder().email("attendee4@email.com").build();
		
		ParticipationChanges participationChanges2 = ParticipationChanges.builder()
				.eventExtId("participation_changes_2")
				.eventObmId(4)
				.recurrenceId("recurrence_id_2")
				.attendees(Lists.newArrayList(attendee3, attendee4))
				.build();
		
		Event publicEvent = new Event();
		publicEvent.setExtId(new EventExtId("public_event"));
		publicEvent.setTitle("public event");

		Event privateEvent = new Event();
		privateEvent.setPrivacy(EventPrivacy.PRIVATE);
		privateEvent.setExtId(new EventExtId("private_event"));
		privateEvent.setTitle("private event");

		EventChanges changes = new EventChanges();
		changes.setLastSync(lastSync);
		changes.setDeletedEvents(ImmutableSet.of(deletedEvent1, deletedEvent2));
		changes.setParticipationUpdated(Lists.newArrayList(participationChanges1, participationChanges2));
		changes.setUpdated(Lists.newArrayList(publicEvent, privateEvent));
		
		Event privateAnonymizedEvent = new Event();
		privateAnonymizedEvent.setPrivacy(EventPrivacy.PRIVATE);
		privateAnonymizedEvent.setExtId(new EventExtId("private_event"));

		EventChanges expectedChanges = new EventChanges();
		expectedChanges.setLastSync(lastSync);
		expectedChanges.setDeletedEvents(ImmutableSet.of(deletedEvent1, deletedEvent2));
		expectedChanges.setParticipationUpdated(Lists.newArrayList(participationChanges1, participationChanges2));
		expectedChanges.setUpdated(Lists.newArrayList(publicEvent, privateAnonymizedEvent));

		assertThat(changes.anonymizePrivateItems()).isEqualTo(expectedChanges);
	}
	
	@Test
	public void testRemoveNotAllowedConfidentialEvents() {
		Attendee attendee1 = ContactAttendee.builder().email("attendee1@email.com").build();
		Attendee attendee2 = ContactAttendee.builder().email("attendee2@email.com").build();
		
		ParticipationChanges participationChanges1 = ParticipationChanges.builder()
				.eventExtId("participation_changes_1")
				.eventObmId(3)
				.recurrenceId("recurrence_id_1")
				.attendees(Lists.newArrayList(attendee1, attendee2))
				.build();

		Attendee attendee3 = ContactAttendee.builder().email("attendee3@email.com").build();
		Attendee attendee4 = ContactAttendee.builder().email("attendee4@email.com").build();
		
		ParticipationChanges participationChanges2 = ParticipationChanges.builder()
				.eventExtId("participation_changes_2")
				.eventObmId(4)
				.recurrenceId("recurrence_id_2")
				.attendees(Lists.newArrayList(attendee3, attendee4))
				.build();

		Event publicEvent = new Event();
		publicEvent.setExtId(new EventExtId("public_event"));
		publicEvent.setTitle("public event");
		
		Event confidentialEvent = new Event();
		confidentialEvent.setUid(new EventObmId(3));
		confidentialEvent.setPrivacy(EventPrivacy.CONFIDENTIAL);
		confidentialEvent.setExtId(new EventExtId("confidential_event"));
		confidentialEvent.addAttendee(attendee1);
		
		Event confidentialEvent2 = new Event();
		confidentialEvent2.setUid(new EventObmId(4));
		confidentialEvent2.setPrivacy(EventPrivacy.CONFIDENTIAL);
		confidentialEvent2.setExtId(new EventExtId("confidential_event2"));
		confidentialEvent2.setOwnerEmail("attendee1@email.com");
		
		Event confidentialEvent3 = new Event();
		confidentialEvent3.setUid(new EventObmId(5));
		confidentialEvent3.setPrivacy(EventPrivacy.CONFIDENTIAL);
		confidentialEvent3.setExtId(new EventExtId("confidential_event3"));
		
		EventChanges changes = new EventChanges();
		changes.setLastSync(lastSync);
		changes.setDeletedEvents(ImmutableSet.of(deletedEvent1, deletedEvent2));
		changes.setParticipationUpdated(Lists.newArrayList(participationChanges1, participationChanges2));
		changes.setUpdated(Lists.newArrayList(publicEvent, confidentialEvent, confidentialEvent2, confidentialEvent3));
		
		DeletedEvent confidentialEventToDeletedEvent =
				DeletedEvent.builder().eventObmId(5).eventExtId("confidential_event3").build();

		EventChanges expectedChanges = new EventChanges();
		expectedChanges.setLastSync(lastSync);
		expectedChanges.setDeletedEvents(ImmutableSet.of(deletedEvent1, deletedEvent2, confidentialEventToDeletedEvent));
		expectedChanges.setParticipationUpdated(Lists.newArrayList(participationChanges1, participationChanges2));
		expectedChanges.setUpdated(Lists.newArrayList(publicEvent, confidentialEvent, confidentialEvent2));

		EventChanges resultChanges = changes.removeNotAllowedConfidentialEvents("attendee1@email.com");
		assertThat(resultChanges).isEqualTo(expectedChanges);
	}
}
