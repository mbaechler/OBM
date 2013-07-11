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
package org.obm.dav;

import static org.fest.assertions.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;

import fr.aliacom.obm.common.domain.ObmDomain;
import fr.aliacom.obm.common.user.ObmUser;



public class ClientIdTest {

	private ObmUser user;

	@Before
	public void setup() {
		user = ObmUser.builder()
				.login("jaures")
				.domain(ObmDomain.builder().name("sfio").uuid("4276e7d0-970d-4216-9279-2df255702ae1").build())
				.build();
	}
	
	@Test(expected=IllegalStateException.class)
	public void emptyClientId() {
		ClientId.builder().build();
	}
	
	@Test(expected=IllegalStateException.class)
	public void userOnlyClientId() {
		ClientId.builder().user(user).build();
	}
	
	@Test(expected=IllegalStateException.class)
	public void filenameOnlyClientId() {
		ClientId.builder().filename("file").build();
	}
	
	@Test
	public void correctClientId() {
		ClientId clientId = ClientId.builder().user(user).filename("file").build();
		assertThat(clientId.getHash()).isEqualTo("64fa18275e98a703058cd83813f41672adda6a49");
	}

}
