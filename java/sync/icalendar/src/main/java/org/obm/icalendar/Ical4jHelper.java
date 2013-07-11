/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2013 Linagora
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
package org.obm.icalendar;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.fortuna.ical4j.data.ParserException;

import org.obm.sync.auth.AccessToken;
import org.obm.sync.calendar.Event;
import org.obm.sync.calendar.EventRecurrence;
import org.obm.sync.calendar.FreeBusy;
import org.obm.sync.calendar.FreeBusyRequest;

import fr.aliacom.obm.common.domain.ObmDomain;

public interface Ical4jHelper {

	String buildIcsInvitationRequest(Ical4jUser iCal4jUser, Event event,
			AccessToken token);

	String buildIcsInvitationReply(Event event, Ical4jUser replyICal4jUser,
			AccessToken token);

	String buildIcsInvitationCancel(Ical4jUser iCal4jUser, Event event,
			AccessToken token);

	String buildIcs(Ical4jUser iCal4jUser, Collection<Event> events,
			AccessToken token);

	FreeBusyRequest parseICSFreeBusy(String ics, ObmDomain domain,
			Integer ownerId) throws IOException, ParserException;

	List<Event> parseICS(String ics, Ical4jUser ical4jUser, Integer ownerId)
			throws IOException, ParserException;

	List<Event> parseICSEvent(String ics, Ical4jUser ical4jUser, Integer ownerId)
			throws IOException, ParserException;

	String parseEvents(Ical4jUser iCal4jUser, Collection<Event> listEvent,
			AccessToken token);

	String parseEvent(Event event, Ical4jUser iCal4jUser, AccessToken token);

	Date isInIntervalDate(Event event, Date start, Date end, Set<Date> dateExce);

	Date isInIntervalDate(EventRecurrence recurrence, Date eventDate,
			Date start, Date end, Set<Date> dateExce);

	List<Date> dateInInterval(EventRecurrence recurrence, Date eventDate,
			Date start, Date end, Set<Date> dateExce);

	String parseFreeBusy(FreeBusy fb);

}