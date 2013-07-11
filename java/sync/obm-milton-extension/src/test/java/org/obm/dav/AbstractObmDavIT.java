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

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.FluentPropFind;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.DefaultHttpClient;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.obm.icalendar.Ical4jHelper;
import org.obm.sync.date.DateProvider;
import org.obm.sync.services.AttendeeService;
import org.obm.sync.services.ICalendar;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;

import fr.aliacom.obm.common.calendar.CalendarDao;
import fr.aliacom.obm.common.domain.DomainService;
import fr.aliacom.obm.common.session.SessionManagement;
import fr.aliacom.obm.common.user.UserService;
import fr.aliacom.obm.utils.HelperService;

public class AbstractObmDavIT {

	public static class Env extends AbstractModule {

		@Provides
		@Singleton
		protected Server createServer() {
			Server server = new Server(0);
			Context root = new Context(server, "/", Context.SESSIONS);

			root.addFilter(GuiceFilter.class, "/*", 0);
			root.addServlet(DefaultServlet.class, "/*");

			return server;
		}

		@Override
		protected void configure() {
			IMocksControl control = EasyMock.createControl();
			install(new DavModule());
			bind(IMocksControl.class).toInstance(control);
			bind(CalendarDao.class).toInstance(control.createMock(CalendarDao.class));
			bind(DomainService.class).toInstance(control.createMock(DomainService.class));
			bind(UserService.class).toInstance(control.createMock(UserService.class));
			bind(SessionManagement.class).toInstance(control.createMock(SessionManagement.class));
			bind(DateProvider.class).toInstance(control.createMock(DateProvider.class));
			bind(AttendeeService.class).toInstance(control.createMock(AttendeeService.class));
			bind(Ical4jHelper.class).toInstance(control.createMock(Ical4jHelper.class));
			bind(ICalendar.class).toInstance(control.createMock(ICalendar.class));
			bind(HelperService.class).toInstance(control.createMock(HelperService.class));
		}
	}

	@Inject
	private Server server;

	protected String baseUrl;
	protected int serverPort;

	protected Executor executor;

	@Before
	public void setUp() throws Exception {
		server.start();

		serverPort = server.getConnectors()[0].getLocalPort();
		baseUrl = "http://localhost:" + serverPort;
		executor = Executor.newInstance(new DefaultHttpClient());
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	protected HttpResponse options(String path) throws Exception {
		return Request.Options(baseUrl + path).execute().returnResponse();
	}

	protected Request get(String path) {
		return Request.Get(baseUrl + path);
	}

	protected Request propfind(String path, int depth) {
		FluentPropFind fluentPropFind = new FluentPropFind(baseUrl + path);
		return fluentPropFind.addHeader("Depth", String.valueOf(depth));
	}
}
