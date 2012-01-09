package org.obm.push.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Test;
import org.obm.push.bean.Address;
import org.obm.push.bean.BackendSession;
import org.obm.push.bean.User;
import org.obm.push.bean.User.Factory;
import org.obm.push.exception.SendEmailException;
import org.obm.push.exception.SmtpInvalidRcptException;
import org.obm.push.exception.activesync.ProcessingEmailException;
import org.obm.push.exception.activesync.StoreEmailException;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.ServerFault;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.services.ICalendar;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;


public class MailBackendTest {
	
	@Test
	public void testSendEmailWithBigMail()
			throws ProcessingEmailException, ServerFault, StoreEmailException, SendEmailException, SmtpInvalidRcptException, IOException {
		final User user = Factory.create().createUser("test@test", "test@domain");
		final String password = "pass";
		final AccessToken at = new AccessToken(1, "o-push");
		
		ImapMailboxService emailManager = EasyMock.createMock(ImapMailboxService.class);
		ICalendar calendarClient = EasyMock.createMock(ICalendar.class);
		BackendSession backendSession = EasyMock.createMock(BackendSession.class);
		LoginService login = EasyMock.createMock(LoginService.class);
		
		EasyMock.expect(backendSession.getUser()).andReturn(user).once();
		EasyMock.expect(backendSession.getPassword()).andReturn(password).once();

		EasyMock.expect(login.login(user.getLoginAtDomain(), password))
				.andReturn(at).once();
		EasyMock.expect(calendarClient.getUserEmail(at)).andReturn(user.getLoginAtDomain()).once();
		login.logout(at);
		EasyMock.expectLastCall().once();
		Set<Address> addrs = Sets.newHashSet();
		emailManager.sendEmail(EasyMock.anyObject(BackendSession.class), EasyMock.anyObject(Address.class), EasyMock.anyObject(addrs.getClass()), EasyMock.anyObject(addrs.getClass()), EasyMock.anyObject(addrs.getClass()), EasyMock.anyObject(InputStream.class), EasyMock.anyBoolean());
		EasyMock.expectLastCall().once();
		
		MailBackend mailBackend = new MailBackendImpl(emailManager, calendarClient, login, null);

		EasyMock.replay(emailManager, calendarClient, backendSession, login);

		InputStream emailStream = loadDataFile("bigEml.eml");
		mailBackend.sendEmail(backendSession, ByteStreams.toByteArray(emailStream), true);
		
		EasyMock.verify(emailManager, calendarClient, backendSession, login);
	}
	
	protected InputStream loadDataFile(String name) {
		return getClass().getClassLoader().getResourceAsStream(
				"eml/" + name);
	}
}
