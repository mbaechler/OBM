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

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.junit.Ignore;

import junit.framework.TestCase;

@Ignore
public class SendEmailHandlerTest extends TestCase {

	public void testAndroidIsInvitation() throws MimeException, IOException{
		InputStream eml = loadDataFile("androidInvit.eml");
		SendEmailHandler handler = new SendEmailHandler("john@test.opush");
		MimeStreamParser parser = new MimeStreamParser();
		parser.setContentHandler(handler);
		parser.parse(eml);
		
		assertTrue(handler.isInvitation());
	}
	
	protected InputStream loadDataFile(String name) {
		return getClass().getClassLoader().getResourceAsStream(
				"data/eml/" + name);
	}
}
