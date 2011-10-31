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
public class IdleClientLoginTests extends IMAPTestCase {

	public void testConstructor() {
		create();
	}

	private IdleClient create() {
		return new IdleClient(confValue("imap"), 143, confValue("login"),
				confValue("password"));
	}

	public void testLoginLogout() {
		IdleClient sc = create();
		try {
			boolean ok = sc.login(true);
			assertTrue(ok);
			sc.select("INBOX");
			sc.stopIdle();
		} catch (Throwable e) {
			fail(e.getMessage());
		} finally {
			try {
				sc.logout();
			} catch (Throwable e) {
				fail(e.getMessage());
			}
		}
	}

	public void testLoginLogoutSpeed() {
		IdleClient sc = create();
		int COUNT = 1000;
		for (int i = 0; i < COUNT; i++) {
			boolean ok = sc.login(true);
			assertTrue(ok);
			sc.select("INBOX");
			sc.stopIdle();
			sc.logout();
		}
	}

}
