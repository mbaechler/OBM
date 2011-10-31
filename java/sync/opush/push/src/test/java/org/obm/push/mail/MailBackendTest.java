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
package org.obm.push.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.columba.ristretto.message.Address;
import org.easymock.EasyMock;
import org.junit.Test;
import org.obm.push.bean.BackendSession;
import org.obm.push.exception.SendEmailException;
import org.obm.push.exception.SmtpInvalidRcptException;
import org.obm.push.exception.activesync.ProcessingEmailException;
import org.obm.push.exception.activesync.StoreEmailException;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.client.calendar.CalendarClient;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

public class MailBackendTest {

	@Test
	public void testSendEmailWithBigMail()
			throws ProcessingEmailException, ServerFault, StoreEmailException, SendEmailException, SmtpInvalidRcptException, IOException {
		final String loginAtDomain = "test@test";
		final String password = "pass";
		final AccessToken at = new AccessToken(1, 1, "o-push");
		
		EmailManager emailManager = EasyMock.createMock(EmailManager.class);
		CalendarClient calendarClient = EasyMock.createMock(CalendarClient.class);
		BackendSession backendSession = EasyMock.createMock(BackendSession.class);
		
		EasyMock.expect(backendSession.getLoginAtDomain()).andReturn(loginAtDomain).once();
		EasyMock.expect(backendSession.getPassword()).andReturn(password).once();

		EasyMock.expect(calendarClient.login(loginAtDomain, password, "o-push"))
				.andReturn(at).once();
		EasyMock.expect(calendarClient.getUserEmail(at)).andReturn(loginAtDomain).once();
		calendarClient.logout(at);
		EasyMock.expectLastCall().once();
		Set<Address> addrs = Sets.newHashSet();
		emailManager.sendEmail(EasyMock.anyObject(BackendSession.class), EasyMock.anyObject(Address.class), EasyMock.anyObject(addrs.getClass()), EasyMock.anyObject(addrs.getClass()), EasyMock.anyObject(addrs.getClass()), EasyMock.anyObject(InputStream.class), EasyMock.anyBoolean());
		EasyMock.expectLastCall().once();
		
		MailBackend mailBackend = new MailBackend(emailManager, null, null, null, calendarClient, null);

		EasyMock.replay(emailManager, calendarClient, backendSession);

		InputStream emailStream = loadDataFile("bigEml.eml");
		mailBackend.sendEmail(backendSession, ByteStreams.toByteArray(emailStream), true);
		
		EasyMock.verify(emailManager, calendarClient, backendSession);
	}
	
	protected InputStream loadDataFile(String name) {
		return getClass().getClassLoader().getResourceAsStream(
				"eml/" + name);
	}
}
