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

package org.minig.imap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Ignore;

@Ignore("It's necessary to do again all tests")
public abstract class LoggedTestCase extends IMAPTestCase {

	protected StoreClient sc;

	public void setUp() {
		String port = confValue("port");
		if (port == null) {
			port = "143";
		}
		sc = new StoreClient(confValue("imap"), Integer.parseInt(port), confValue("login"),
				confValue("password"));
		boolean login = sc.login();
		if (!login) {
			fail("login failed for "+confValue("login")+" / "+confValue("password"));
		}
	}

	public void tearDown() {
		sc.logout();
	}

	public InputStream getRfc822Message() {
		String m = "From: Thomas Cataldo <thomas@zz.com>\r\n"
				+ "Subject: test message "
				+ System.currentTimeMillis()
				+ "\r\n"
				+ "MIME-Version: 1.0\r\n"
				+ "Content-Type: text/plain; CHARSET=UTF-8\r\n\r\n"
				+ "Hi, this is message about my 300euros from the casino.\r\n\r\n";
		return new ByteArrayInputStream(m.getBytes());
	}

	public InputStream getUtf8Rfc822Message() {
		String m = "From: Thomas Cataldo <thomas@zz.com>\r\n"
				+ "Subject: test message " + System.currentTimeMillis()
				+ "\r\n" + "MIME-Version: 1.0\r\n"
				+ "Content-Type: text/plain; CHARSET=UTF-8\r\n\r\n"
				+ "Hi, this is message about my 300€ from the casino.\r\n\r\n";
		return new ByteArrayInputStream(m.getBytes());
	}
}
