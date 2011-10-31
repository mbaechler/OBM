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
package org.obm.push;

import java.security.InvalidParameterException;

import org.fest.assertions.Assertions;
import org.junit.Test;

public class ActiveSyncServletTest {

	private ActiveSyncServlet createActiveSyncServlet() {
		return new ActiveSyncServlet(null, null, null, null, null);
	}
	
	@Test
	public void testGetLoginAtDomainWithArrobase() {
		ActiveSyncServlet activeSyncServlet = createActiveSyncServlet();
		String text = "login@domain";
		String result = activeSyncServlet.getLoginAtDomain(text);
		Assertions.assertThat(result).isEqualTo(text);
	}

	@Test(expected=InvalidParameterException.class)
	public void testGetLoginAtDomainWithTwoArrobases() {
		ActiveSyncServlet activeSyncServlet = createActiveSyncServlet();
		String text = "login@domain@error";
		activeSyncServlet.getLoginAtDomain(text);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void testGetLoginAtDomainWithNoDomain() {
		ActiveSyncServlet activeSyncServlet = createActiveSyncServlet();
		String text = "login";
		activeSyncServlet.getLoginAtDomain(text);
	}
	
	@Test
	public void testGetLoginAtDomainWithSlashes() {
		ActiveSyncServlet activeSyncServlet = createActiveSyncServlet();
		String text = "domain\\login";
		String result = activeSyncServlet.getLoginAtDomain(text);
		Assertions.assertThat(result).isEqualTo("login@domain");
	}
	
	@Test(expected=InvalidParameterException.class)
	public void testGetLoginAtDomainWithSlashesAndArrobase() {
		ActiveSyncServlet activeSyncServlet = createActiveSyncServlet();
		String text = "domain\\login@domain2";
		activeSyncServlet.getLoginAtDomain(text);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void testGetLoginAtDomainWithArrobaseAndSlashes() {
		ActiveSyncServlet activeSyncServlet = createActiveSyncServlet();
		String text = "doma@in\\login";
		activeSyncServlet.getLoginAtDomain(text);
	}
}
