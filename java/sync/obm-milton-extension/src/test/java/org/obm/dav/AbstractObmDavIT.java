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
import org.apache.http.client.fluent.Request;
import org.junit.After;
import org.junit.Before;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;

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
			install(new DavModule());
		}
	}

	@Inject
	private Server server;

	protected String baseUrl;
	protected int serverPort;

	@Before
	public void setUp() throws Exception {
		server.start();

		serverPort = server.getConnectors()[0].getLocalPort();
		baseUrl = "http://localhost:" + serverPort;
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	protected HttpResponse options(String path) throws Exception {
		return Request.Options(baseUrl + path).execute().returnResponse();
	}
}
