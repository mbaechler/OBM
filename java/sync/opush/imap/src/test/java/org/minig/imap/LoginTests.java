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

import org.junit.Ignore;

@Ignore("It's necessary to do again all tests")
public class LoginTests extends IMAPTestCase {

	public void testConstructor() {
		StoreClient storeClient = new StoreClient(confValue("imap"), 143, confValue("login"),
				confValue("password"));
		assertNotNull(storeClient);
	}

	public void testLoginLogout() {
		StoreClient sc = new StoreClient(confValue("imap"), 143,
				confValue("login"), confValue("password"));
		try {
			boolean ok = sc.login();
			assertTrue(ok);
		} finally {
			sc.logout();
		}
	}

	public void testLoginLogoutSpeed() {
		StoreClient sc = new StoreClient(confValue("imap"), 143,
				confValue("login"), confValue("password"));
		int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			boolean ok = sc.login();
			assertTrue(ok);
			sc.logout();
		}
	}

}
