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

import fr.aliacom.obm.common.user.ObmUser;
import io.milton.annotations.ChildrenOf;
import io.milton.annotations.CreatedDate;
import io.milton.annotations.Get;
import io.milton.annotations.ICalData;
import io.milton.annotations.ModifiedDate;
import io.milton.annotations.Name;
import io.milton.annotations.PutChild;
import io.milton.annotations.UniqueId;

import java.util.Date;
import java.util.List;

import org.obm.sync.calendar.Event;

public class ObmCalendarController {

	@ChildrenOf
	public ObmUserCalendars getUsers(ObmUser user) {
		return new ObmUserCalendars(user);
	}

	@ChildrenOf
	public ObmUserCalendar getCalendarsForUser(ObmUserCalendars userCalendarsHome) {
		return new ObmUserCalendar(userCalendarsHome.user);
	}

	@ChildrenOf
	public List<Event> getEvents(ObmUserCalendar cal) {
		return null;
	}

	@ICalData
	@Get
	public byte[] getEventData(Event c) {
		return null;
	}

	@PutChild
	public Event createEvent(ObmUserCalendar cal, String newName, byte[] ical) {
		return null;
	}

	@Name
	public String getEventName(Event event) {
		return event.getTitle();
	}

	@ModifiedDate
	public Date getModifiedDateForEvent(Event event) {
		return event.getTimeUpdate();
	}

	@CreatedDate
	public Date getCreatedDate(Event event) {
		return event.getTimeCreate();
	}

	@UniqueId
	public String getEventId(Event event) {
		return event.getExtId().toString();
	}

	// @CTag
	// public static getCalendarCTag(ObmUserCalendar cal ) {
	// return
	// }

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

		public ObmUserCalendar(ObmUser user) {
			this.user = user;
		}

		public String getName() {
			return "default";
		}
	}
}
