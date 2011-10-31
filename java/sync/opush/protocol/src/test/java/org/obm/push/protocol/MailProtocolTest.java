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
package org.obm.push.protocol;

import java.io.IOException;
import java.io.InputStream;

import org.easymock.EasyMock;
import org.junit.Test;
import org.obm.configuration.EmailConfiguration;
import org.obm.push.exception.QuotaExceededException;
import org.obm.push.protocol.request.ActiveSyncRequest;


public class MailProtocolTest {
	
	@Test
	public void testWithBigMessageMaxSize() throws IOException, QuotaExceededException {
		EmailConfiguration emailConfiguration = EasyMock.createMock(EmailConfiguration.class);
		ActiveSyncRequest request = EasyMock.createMock(ActiveSyncRequest.class);
		
		EasyMock.expect(request.getParameter("CollectionId")).andReturn("1").once();
		EasyMock.expect(request.getParameter("ItemId")).andReturn("1").once();
		EasyMock.expect(request.getInputStream()).andReturn(loadDataFile("bigEml.eml")).once();
		EasyMock.expect(request.getParameter("SaveInSent")).andReturn("T").once();
		
		EasyMock.expect(emailConfiguration.getMessageMaxSize()).andReturn(10485760).once();
		EasyMock.replay(request, emailConfiguration);
		
		MailProtocol mailProtocol = new MailProtocol(emailConfiguration);
		mailProtocol.getRequest(request);
		EasyMock.verify(request, emailConfiguration);
		
	}
	
	@Test(expected=QuotaExceededException.class)
	public void testWithSmallMessageMaxSize() throws IOException, QuotaExceededException {
		EmailConfiguration emailConfiguration = EasyMock.createMock(EmailConfiguration.class);
		ActiveSyncRequest request = EasyMock.createMock(ActiveSyncRequest.class);
		
		EasyMock.expect(request.getParameter("CollectionId")).andReturn("1").once();
		EasyMock.expect(request.getParameter("ItemId")).andReturn("1").once();
		EasyMock.expect(request.getInputStream()).andReturn(loadDataFile("bigEml.eml")).once();
		EasyMock.expect(request.getParameter("SaveInSent")).andReturn("T").once();
		
		EasyMock.expect(emailConfiguration.getMessageMaxSize()).andReturn(1024).once();
		EasyMock.replay(request, emailConfiguration);
		
		MailProtocol mailProtocol = new MailProtocol(emailConfiguration);
		mailProtocol.getRequest(request);
		EasyMock.verify(request, emailConfiguration);
		
	}

	protected InputStream loadDataFile(String name) {
		return getClass().getClassLoader().getResourceAsStream(
				"file/" + name);
	}
}
