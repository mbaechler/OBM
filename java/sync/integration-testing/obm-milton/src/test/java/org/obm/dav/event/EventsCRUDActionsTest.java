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
package org.obm.dav.event;

import static org.fest.assertions.api.Assertions.assertThat;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.obm.dav.ObmMiltonIntegrationTest;
import org.obm.filter.Slow;
import org.obm.sync.arquillian.ManagedTomcatSlowGuiceArquillianRunner;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

@Slow
@RunWith(ManagedTomcatSlowGuiceArquillianRunner.class)
public class EventsCRUDActionsTest extends ObmMiltonIntegrationTest {

	@Test
	@RunAsClient
	public void testGetUnknownEvent() throws Exception {
		executor.auth("user1@domain.org", "user1");
		HttpResponse getResponse = executor
				.execute(Request.Get(baseURL + "users/user1@domain.org/calendars/user1@domain.org/event1")).returnResponse();
		
		assertThat(getResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	@RunAsClient
	public void testCreateAndGetEvent() throws Exception {
		String expectedIcs = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("ics/Event.ics"));

		executor.auth("user1@domain.org", "user1");
		HttpResponse putResponse = executor
				.execute(Request.Put(baseURL + "users/user1@domain.org/calendars/user1@domain.org/event1")
						.bodyByteArray(expectedIcs.getBytes())).returnResponse();
		assertThat(putResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_CREATED);
		
		String eventExtId = getEventExtId(putResponse);
		HttpResponse getResponse = executor
				.execute(Request.Get(baseURL + "users/user1@domain.org/calendars/user1@domain.org/" + eventExtId)).returnResponse();
		assertThat(getResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
		// TODO: assert event with caldav4j
	}

	@Test
	@RunAsClient
	public void testDeleteEvent() throws Exception {
		String expectedIcs = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("ics/Event.ics"));

		executor.auth("user1@domain.org", "user1");
		HttpResponse putResponse = executor
				.execute(Request.Put(baseURL + "users/user1@domain.org/calendars/user1@domain.org/event1")
						.bodyByteArray(expectedIcs.getBytes())).returnResponse();
		assertThat(putResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_CREATED);

		// To be sure that the event is really inserted
		String eventExtId = getEventExtId(putResponse);
		HttpResponse getResponse = executor
				.execute(Request.Get(baseURL + "users/user1@domain.org/calendars/user1@domain.org/" + eventExtId)).returnResponse();
		assertThat(getResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
		
		HttpResponse deleteResponse = executor
				.execute(Request.Delete(baseURL + "users/user1@domain.org/calendars/user1@domain.org/" + eventExtId)).returnResponse();
		assertThat(deleteResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
		
		HttpResponse getAfterDeleteResponse = executor
				.execute(Request.Get(baseURL + "users/user1@domain.org/calendars/user1@domain.org/" + eventExtId)).returnResponse();
		assertThat(getAfterDeleteResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	@RunAsClient
	public void testUpdateEvent() throws Exception {
		String expectedIcs = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("ics/Event.ics"));

		executor.auth("user1@domain.org", "user1");
		HttpResponse putResponse = executor
				.execute(Request.Put(baseURL + "users/user1@domain.org/calendars/user1@domain.org/event1")
						.bodyByteArray(expectedIcs.getBytes())).returnResponse();
		assertThat(putResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_CREATED);
		
		// To be sure that the event is really inserted
		String eventExtId = getEventExtId(putResponse);
		HttpResponse getResponse = executor
				.execute(Request.Get(baseURL + "users/user1@domain.org/calendars/user1@domain.org/" + eventExtId)).returnResponse();
		assertThat(getResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
		
		HttpResponse updateResponse = executor
				.execute(Request.Put(baseURL + "users/user1@domain.org/calendars/user1@domain.org/" + eventExtId)
						.bodyByteArray(expectedIcs.getBytes())).returnResponse();
		assertThat(updateResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
		
		HttpResponse getAfterUpdateResponse = executor
				.execute(Request.Get(baseURL + "users/user1@domain.org/calendars/user1@domain.org/" + eventExtId)).returnResponse();
		assertThat(getAfterUpdateResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
		// TODO: assert event with caldav4j
	}

	private String getEventExtId(HttpResponse putResponse) {
		String eventExtId = putResponse.getFirstHeader("Etag").getValue();
		Iterable<String> split = Splitter.on('"').split(eventExtId);
		return Iterables.get(split, 1);
	}
}
