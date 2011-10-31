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

import java.util.Arrays;
import java.util.Collection;

import org.minig.imap.impl.MessageSet;

public class MessageSetTests extends IMAPTestCase {

	private void testParse(Collection<Long> data, String expectedSet, Collection<Long> expectedCollection) {
		String set = MessageSet.asString(data);
		assertEquals(expectedSet, set);
		assertEquals(expectedCollection, MessageSet.asLongCollection(set, data.size()));
	}
	
	public void testParse1() {
		testParse(Arrays.asList(1l, 2l, 3l, 8l, 9l, 10l, 12l), "1:3,8:10,12", 
				Arrays.asList(1l, 2l, 3l, 8l, 9l, 10l, 12l));
	}

	public void testParse2() {
		testParse(Arrays.asList(8l, 2l, 3l, 4l, 9l, 10l, 12l, 13l), "2:4,8:10,12:13",
				Arrays.asList(2l, 3l, 4l, 8l, 9l, 10l, 12l, 13l));
	}
	
	public void testParse3() {
		testParse(Arrays.asList(1l, 2l), "1:2", Arrays.asList(1l, 2l));
	}
	
	public void testParse4() {
		testParse(Arrays.asList(1l), "1", Arrays.asList(1l));
	}
	
}
