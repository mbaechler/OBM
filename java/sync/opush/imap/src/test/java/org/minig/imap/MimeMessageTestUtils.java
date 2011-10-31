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

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.minig.imap.mime.IMimePart;
import org.minig.imap.mime.MimeAddress;

public class MimeMessageTestUtils {

	private static String prefixMessage(IMimePart expected) {
		return "part with address " + expected.getAddress();
	}
	
	public static void checkMimeTree(IMimePart expected, IMimePart actual) {
		Assert.assertEquals(prefixMessage(expected) + " has wrong number of children", 
				expected.getChildren().size(), actual.getChildren().size());
		Assert.assertEquals(prefixMessage(expected), expected.getMimeType(), actual.getMimeType());
		Assert.assertEquals(prefixMessage(expected), expected.getMimeSubtype(), actual.getMimeSubtype());
		Assert.assertEquals(prefixMessage(expected), expected.getContentTransfertEncoding(), actual.getContentTransfertEncoding());
		Assert.assertEquals(prefixMessage(expected), expected.getContentId(), actual.getContentId());
		Assert.assertArrayEquals(prefixMessage(expected), expected.getBodyParams().toArray(), actual.getBodyParams().toArray());
		Iterator<IMimePart> expectedParts = expected.getChildren().iterator();
		Iterator<IMimePart> actualParts = actual.getChildren().iterator();
		while (actualParts.hasNext()) {
			checkMimeTree(expectedParts.next(), actualParts.next());
		}
	}

	public static IMimePart getPartByAddress(IMimePart message, MimeAddress addr) {
		Collection<IMimePart> children = message.getChildren();
		for (IMimePart part: children) {
			if (addr.equals(part.getAddress())) {
				return part;
			}
			IMimePart result = getPartByAddress(part, addr);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
	
}
