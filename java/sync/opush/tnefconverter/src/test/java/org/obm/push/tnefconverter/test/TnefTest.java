/* ***** BEGIN LICENSE BLOCK *****
 *
 * %%
 * Copyright (C) 2000 - 2011 Linagora
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK ***** */
package org.obm.push.tnefconverter.test;

import java.io.InputStream;

import junit.framework.TestCase;
import net.freeutils.tnef.Message;
import net.freeutils.tnef.TNEFInputStream;

import org.obm.push.tnefconverter.ScheduleMeeting.ScheduleMeeting;

public class TnefTest extends TestCase {

	public void testExtract() {
		InputStream in = loadDataFile("excptRecur.tnef");
		assertNotNull(in);
		try {
			TNEFInputStream tnef = new TNEFInputStream(in);
			Message tnefMsg = new Message(tnef);
			ScheduleMeeting ics = new ScheduleMeeting(tnefMsg);
			assertNotNull(ics);
			assertNotNull(ics.getMethod());
			assertNotNull(ics.getUID());
			assertNotNull(ics.getStartDate());
			assertNotNull(ics.getEndDate());
			assertNotNull(ics.getResponseRequested());
			assertNotNull(ics.getDescription());
			assertNotNull(ics.getClazz());
			assertNotNull(ics.getLocation());
			assertNotNull(ics.isAllDay());
			assertNotNull(ics.isRecurring());
			assertNotNull(ics.getOldRecurrenceType());
			assertNotNull(ics.getInterval());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	public void testExtract2() {
		InputStream in = loadDataFile("acpInv.tnef");
		assertNotNull(in);
		try {
			TNEFInputStream tnef = new TNEFInputStream(in);
			Message tnefMsg = new Message(tnef);
			ScheduleMeeting ics = new ScheduleMeeting(tnefMsg);
			assertNotNull(ics);
			assertNotNull(ics.getMethod());
			assertNotNull(ics.getUID());
			assertNotNull(ics.getStartDate());
			assertNotNull(ics.getEndDate());
			assertNotNull(ics.getResponseRequested());
			assertNotNull(ics.getDescription());
			assertNotNull(ics.getClazz());
			assertNotNull(ics.getLocation());
			assertNotNull(ics.isAllDay());
			assertNotNull(ics.isRecurring());
			assertNotNull(ics.getOldRecurrenceType());
			assertNotNull(ics.getInterval());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	protected InputStream loadDataFile(String name) {
		return getClass().getClassLoader().getResourceAsStream(
				"data/tnef/" + name);
	}
	
}
