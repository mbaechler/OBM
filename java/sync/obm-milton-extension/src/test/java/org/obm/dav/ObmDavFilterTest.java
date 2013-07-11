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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createControl;
import static org.fest.assertions.api.Assertions.*;

import javax.servlet.http.HttpServletRequest;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.obm.filter.SlowFilterRunner;

@RunWith(SlowFilterRunner.class)
public class ObmDavFilterTest {

	private IMocksControl control;

	@Before
	public void setUp() {
		control = createControl();
	}
	
	@Test
	public void testBuildRequestContextPath() {
		HttpServletRequest httpRequest = control.createMock(HttpServletRequest.class);
		expect(httpRequest.getContextPath()).andReturn("/context");
		expect(httpRequest.getRequestURI()).andReturn("/context/with/path");
		
		control.replay();
		assertThat(new ObmDavFilter().buildRequestContextPath(httpRequest)).isEqualTo("/with/path");
		control.verify();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBuildRequestContextPathWhenNotStartWithContext() throws Exception {
		HttpServletRequest httpRequest = control.createMock(HttpServletRequest.class);
		expect(httpRequest.getContextPath()).andReturn("/context");
		expect(httpRequest.getRequestURI()).andReturn("/anotherContext/with/path");
		
		control.replay();
		try {
			new ObmDavFilter().buildRequestContextPath(httpRequest);
		} catch (Exception e) {
			control.verify();
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBuildRequestContextPathWhenContextIsNull() throws Exception {
		HttpServletRequest httpRequest = control.createMock(HttpServletRequest.class);
		expect(httpRequest.getContextPath()).andReturn(null);
		expect(httpRequest.getRequestURI()).andReturn("/anotherContext/with/path");
		
		control.replay();
		try {
			new ObmDavFilter().buildRequestContextPath(httpRequest);
		} catch (Exception e) {
			control.verify();
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBuildRequestContextPathWhenContextIsEmpty() throws Exception {
		HttpServletRequest httpRequest = control.createMock(HttpServletRequest.class);
		expect(httpRequest.getContextPath()).andReturn("");
		expect(httpRequest.getRequestURI()).andReturn("/anotherContext/with/path");
		
		control.replay();
		try {
			new ObmDavFilter().buildRequestContextPath(httpRequest);
		} catch (Exception e) {
			control.verify();
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBuildRequestContextPathWhenURIIsNull() throws Exception {
		HttpServletRequest httpRequest = control.createMock(HttpServletRequest.class);
		expect(httpRequest.getContextPath()).andReturn("/context");
		expect(httpRequest.getRequestURI()).andReturn(null);
		
		control.replay();
		try {
			new ObmDavFilter().buildRequestContextPath(httpRequest);
		} catch (Exception e) {
			control.verify();
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testBuildRequestContextPathWhenURIIsEmpty() throws Exception {
		HttpServletRequest httpRequest = control.createMock(HttpServletRequest.class);
		expect(httpRequest.getContextPath()).andReturn("/context");
		expect(httpRequest.getRequestURI()).andReturn("");
		
		control.replay();
		try {
			new ObmDavFilter().buildRequestContextPath(httpRequest);
		} catch (Exception e) {
			control.verify();
			throw e;
		}
	}
	
}
