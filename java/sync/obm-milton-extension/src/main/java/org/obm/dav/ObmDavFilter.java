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

import io.milton.config.HttpManagerBuilder;
import io.milton.ent.config.HttpManagerBuilderEnt;
import io.milton.http.HttpManager;
import io.milton.http.Request;
import io.milton.http.Response;
import io.milton.http.annotated.AnnotationResourceFactory;
import io.milton.servlet.FilterConfigWrapper;
import io.milton.servlet.MiltonFilter;
import io.milton.servlet.MiltonServlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ObmDavFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(MiltonFilter.class);
	private FilterConfigWrapper configWrapper;
	private ServletContext servletContext;
	protected HttpManager httpManager;

	@Inject
	private ObmRootController rootController;
	@Inject
	private ObmCalendarController calendarController;
	@Inject
	private ObmUsersController usersController;

	@Override
	public void init(FilterConfig config) throws ServletException {
		try {
			configWrapper = new FilterConfigWrapper(config);
			servletContext = configWrapper.getServletContext();

			AnnotationResourceFactory resourceFactory = new AnnotationResourceFactory();
			resourceFactory.setContextPath(config.getServletContext().getContextPath());
			resourceFactory.setControllers(ImmutableList.of(
					calendarController, 
					usersController, 
					rootController));
			
			HttpManagerBuilder builder = new HttpManagerBuilderEnt();
			builder.setEnableDigestAuth(false);
			builder.setMainResourceFactory(resourceFactory);
			
			httpManager = builder.buildHttpManager();
		} catch (Throwable e) {
			log.error("Exception starting milton servlet", e);
			throw Throwables.propagate(e);
		}
	}

	@Override
	public void destroy() {
		log.debug("destroy");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String requestContextPath = buildRequestContextPath(httpRequest);
			if (isCalDavSpecificHttpMethod(httpRequest) || requestContextPath.startsWith("/users")) {
				log.debug("Processing a DAV request: {}", requestContextPath);
				doMiltonProcessing((HttpServletRequest) request, (HttpServletResponse) response);
			} else {
				log.debug("Propagate the request as it not seems to be a DAV request: {}", requestContextPath);
				chain.doFilter(request, response);
			}
		} else {
			log.debug("Not an HttpServletRequest, let propagate it as not seems to be a DAV request");
			chain.doFilter(request, response);
		}
	}

	@VisibleForTesting String buildRequestContextPath(HttpServletRequest httpRequest) {
		String requestURI = httpRequest.getRequestURI();
		String contextPath = httpRequest.getContextPath();
		Preconditions.checkArgument(!Strings.isNullOrEmpty(requestURI), "requestURI cannot be null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(contextPath), "contextPath cannot be null");
		Preconditions.checkArgument(requestURI.startsWith(contextPath), "requestURI must start with contextPath");
		return requestURI.substring(contextPath.length());
	}

	private boolean isCalDavSpecificHttpMethod(HttpServletRequest request) {
		return request.getMethod().equals("PROPFIND")
			|| request.getMethod().equals("OPTIONS");
	}

	private void doMiltonProcessing(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		// Do this part in its own try/catch block, because if there's a
		// classpath
		// problem it will probably be seen here
		Request request;
		Response response;
		try {
			request = new io.milton.servlet.ServletRequest(httpRequest, servletContext);
			response = new io.milton.servlet.ServletResponse(httpResponse);
		} catch (Throwable e) {
			// OK, I know its not cool to log AND throw. But we really want to
			// log the error
			// so it goes to the log4j logs, but we also want the container to
			// handle
			// the exception because we're outside the milton response handling
			// framework
			// So log and throw it is. But should never happen anyway...
			log.error("Exception creating milton request/response objects", e);
			throw new IOException(
					"Exception creating milton request/response objects", e);
		}

		try {
			MiltonServlet.setThreadlocals(httpRequest, httpResponse);
			httpManager.process(request, response);
		} finally {
			MiltonServlet.clearThreadlocals();
			httpResponse.getOutputStream().flush();
			httpResponse.flushBuffer();
		}
	}
}
