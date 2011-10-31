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
import java.util.List;

import org.junit.Ignore;
import org.minig.imap.sieve.SieveScript;

@Ignore("It's necessary to do again all tests")
public class SieveClientTests extends SieveTestCase {

	private SieveClient sc;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sc = new SieveClient(confValue("imap"), 2000, confValue("login"),
				confValue("password"));
		sc.login();
	}

	public void testEmpty() {
		// empty test to validate setup & teardown
	}

	public void testPutscript() {
		String name = "test." + System.currentTimeMillis() + ".sieve";
		String content = "" // test script
				+ "require [ \"fileinto\", \"imapflags\", "
				// +"\"body\", " // cyrus 2.3 extensions ?!
				+ "\"vacation\" ];\n" // extensions
				// +"if body :text :contains \"viagra\"{\n   discard;\n}\n"
				+ "if size :over 500K {\n   setflag \"\\\\Flagged\";\n}\n"
				+ "fileinto \"INBOX\";\n";
		InputStream contentStream = new ByteArrayInputStream(content.getBytes());
		boolean ret = sc.putscript(name, contentStream);
		assertTrue(ret);
	}

	public void testListscripts() {
		sc.listscripts();
	}

	public void testListscriptsBenchmark() {
		int COUNT = 1000;

		sc.logout();

		sc.login();
		int old = sc.listscripts().size();
		sc.logout();

		
		for (int i = 0; i < COUNT; i++) {
			boolean loginOk = sc.login();
			assertTrue(loginOk);
			int cur = sc.listscripts().size();
			sc.logout();
			assertEquals(old, cur);
			old = cur;
		}
	}

	public void testListAndDeleteAll() {
		List<SieveScript> scripts = sc.listscripts();
		for (SieveScript ss : scripts) {
			sc.deletescript(ss.getName());
		}
		scripts = sc.listscripts();
		assertTrue(scripts.isEmpty());
	}

	@Override
	protected void tearDown() throws Exception {
		sc.logout();
		sc = null;
		super.tearDown();
	}

}
