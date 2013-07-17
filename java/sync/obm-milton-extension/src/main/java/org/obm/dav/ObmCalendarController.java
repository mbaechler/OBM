/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2013  Linagora
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
package org.obm.dav;

import fr.aliacom.obm.common.calendar.CalendarDao;
import fr.aliacom.obm.common.user.ObmUser;
import fr.aliacom.obm.utils.HelperService;
import io.milton.annotations.AccessControlList;
import io.milton.annotations.Calendars;
import io.milton.annotations.ChildOf;
import io.milton.annotations.ChildrenOf;
import io.milton.annotations.CreatedDate;
import io.milton.annotations.Delete;
import io.milton.annotations.Get;
import io.milton.annotations.ICalData;
import io.milton.annotations.ModifiedDate;
import io.milton.annotations.Name;
import io.milton.annotations.Principal;
import io.milton.annotations.PutChild;
import io.milton.annotations.ResourceController;
import io.milton.annotations.UniqueId;
import io.milton.resource.AccessControlledResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.data.ParserException;

import org.obm.icalendar.Ical4jHelper;
import org.obm.icalendar.Ical4jUser;
import org.obm.sync.NotAllowedException;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.EventAlreadyExistException;
import org.obm.sync.auth.EventNotFoundException;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventExtId;
import org.obm.sync.calendar.EventObmId;
import org.obm.sync.calendar.EventType;
import org.obm.sync.services.ICalendar;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

@ResourceController
public class ObmCalendarController {

	@Inject
	private CalendarDao calendarDao;
	@Inject
	private Ical4jHelper ical4jHelper;
	@Inject
	private ICalendar calendarService;
	@Inject
	private Ical4jUser.Factory ical4jUserFactory;
	@Inject
	private HelperService helperService;

	private static AccessToken requestAccessToken() {
		return ObmUsersController.getAccessToken();
	}

	@ChildrenOf
	public ObmUserCalendars getUsers(@Principal ObmUser user) {
		return new ObmUserCalendars(user);
	}

	@ChildrenOf
	@Calendars
	public List<ObmUserCalendar> getCalendarsForUser(ObmUserCalendars userCalendarsHome) {
		return ImmutableList.of(new ObmUserCalendar(userCalendarsHome.user, "default"));
	}

	
//	@ChildOf
//	@Calendars
//	public ObmUserCalendar getCalendarForUser(ObmUserCalendars userCalendarsHome, String name) {
//		return new ObmUserCalendar(userCalendarsHome.user, name);
//	}
	
	@ChildrenOf
	public List<EventResource> getEvents(ObmUserCalendar cal) {
		AccessToken requestAccessToken = requestAccessToken();
		// TODO: Hack because milton sometimes tries to find if an event already exist without authentication
		if (requestAccessToken != null) { 
			return eventsToEventResources(calendarDao.findAllEvents(requestAccessToken, cal.user, EventType.VEVENT));
		}
		return ImmutableList.of();
	}

	@ChildOf
	public EventResource getEvent(ObmUserCalendar cal, String eventName) throws SQLException {
		EventExtId eventExtId = new EventExtId(eventName);
		if (calendarDao.doesEventExist(cal.user, eventExtId)) {
			return new EventResource(eventExtId, cal.user);
		}
		return null;
	}

	@AccessControlList
	public List<AccessControlledResource.Priviledge> getAccessControlList(ObmUserCalendar calendar, ObmUser currentUser) {
		if (calendar != null) {
			AccessToken token = requestAccessToken();
			if (token != null) {
				if (helperService.canWriteOnCalendar(token, calendar.getUser().getLogin())) {
					return AccessControlledResource.READ_WRITE;
				}
				if (helperService.canReadCalendar(token, calendar.getUser().getLogin())) {
					return AccessControlledResource.READ_BROWSE;
				}
			}
		}
		return ImmutableList.of();
	}

	@ICalData
	@Get
	public byte[] getEventData(EventResource eventResource) throws UnsupportedEncodingException {
		String ical = ical4jHelper.buildIcs(null, ImmutableList.of(eventResource.getEvent()), requestAccessToken());
		return ical.getBytes("UTF-8");
	}

	@Name
	public String getEventName(EventResource eventResource) {
		return eventResource.getExtId().getExtId();
	}

	@ModifiedDate
	public Date getModifiedDateForEvent(EventResource eventResource) {
		return eventResource.getEvent().getTimeUpdate();
	}

	@CreatedDate
	public Date getCreatedDate(EventResource eventResource) {
		return eventResource.getEvent().getTimeCreate();
	}

	@UniqueId
	public String getEventId(EventResource eventResource) {
		return eventResource.getExtId().getExtId();
	}

	@PutChild
	public EventResource updateEvent(EventResource eventResource, byte[] ical, ObmUserCalendar cal) throws ServerFault, NotAllowedException, IOException, ParserException, EventNotFoundException {
		AccessToken token = requestAccessToken();
		ObmUser user = cal.getUser();
		Event newEvent = parseIcalendarBytes(ical, user);
		Event event = calendarService.getEventFromExtId(token, user.getLogin(), eventResource.getExtId());
		newEvent.setUid(event.getUid());
		calendarService.modifyEvent(token, user.getLogin(), newEvent, true, false);
		return eventResource;
	}

	@PutChild
	public EventResource createEvent(ObmUserCalendar cal, String newName, byte[] ical) 
			throws IOException, ParserException, ServerFault, EventAlreadyExistException, NotAllowedException, EventNotFoundException {
		AccessToken token = requestAccessToken();
		ObmUser user = cal.getUser();
		String calendar = user.getLogin();
		Event event = parseIcalendarBytes(ical, user);
		EventObmId createdEventId = calendarService.createEvent(token, calendar, event, false, 
				ClientId.builder().user(user).filename(newName).build().getHash());
		return new EventResource(calendarService.getEventFromId(token, calendar, createdEventId));
	}

	private Event parseIcalendarBytes(byte[] ical, ObmUser user)
			throws IOException, ParserException {
		Ical4jUser ical4jUser = ical4jUserFactory.createIcal4jUser(user.getEmail(), user.getDomain());
		List<Event> events = ical4jHelper.parseICS(new String(ical, Charsets.UTF_8), ical4jUser, user.getEntityId());
		return Iterables.getOnlyElement(events);
	}

	@Delete
	public void deleteEvent(EventResource eventResource, ObmUserCalendar calendar) throws ServerFault, NotAllowedException {
		AccessToken token = requestAccessToken();
		calendarService.removeEventByExtId(token, calendar.getUser().getLogin(), eventResource.getExtId(), eventResource.getEvent().getSequence(), false);
	}


	//    @CTag
	//    public static getCalendarCTag(ObmUserCalendar cal   ) {
	//        return 
	//    }
	public class ObmUserCalendars {

		private ObmUser user;

		public ObmUserCalendars(ObmUser user) {
			this.user = user;
		}

		public String getName() {
			return "calendars";
		}
	}

	public class ObmUserCalendar {

		private ObmUser user;
		private String name;

		public ObmUserCalendar(ObmUser user, String name) { 
			this.user = user;
			this.name = name;
		}

		public ObmUser getUser() {
			return user;
		}

		public String getName() {
			return name;
		}
	}
	
	public class EventResource {
		
		private final EventExtId eventExtId;
		private Event event;
		private ObmUser user;
		
		public EventResource(EventExtId eventExtId, ObmUser user) {
			this.eventExtId = eventExtId;
			this.user = user;
			this.event = null; 
		}

		public EventResource(Event event) {
			this.eventExtId = event.getExtId();
			this.event = event; 
		}

		public Event getEvent() {
			if (event == null) {
				event = calendarDao.findEventByExtId(requestAccessToken(), user, eventExtId);
			}
			return event;
		}

		public EventExtId getExtId() {
			return eventExtId;
		}
	}

	public List<EventResource> eventsToEventResources(Iterable<Event> events) {
		return FluentIterable
			.from(events)
			.transform(new Function<Event, EventResource>() {

				@Override
				public EventResource apply(Event input) {
					return new EventResource(input);
				}
			}).toList();
	}
		
}
