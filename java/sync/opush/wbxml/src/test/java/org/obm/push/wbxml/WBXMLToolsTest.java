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
package org.obm.push.wbxml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.FactoryConfigurationError;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.matchers.StringContains;
import org.obm.push.utils.DOMUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class WBXMLToolsTest {
	@Test
	public void testToWbxmlWithAccents() throws IOException, SAXException, FactoryConfigurationError, WBXmlException{
		String expectedString = "éàâè";
		
		String xmlActiveSync = 
				"<?xml version=\"1.0\"?>" +
				"<Sync>" +
					"<Collections>" +
						"<Collection>" +
							"<Class>Email</Class>" +
							"<SyncKey>" +
								"c488f7b0-0602-4910-82ab-45a6a117e66c" +
								expectedString +
							"</SyncKey>" +
							"<CollectionId>1169</CollectionId>" +
							"<DeletesAsMoves/>" +
							"<GetChanges/>" +
							"<WindowSize>5</WindowSize>" +
							"<Options>" +
								"<FilterType>5</FilterType>" +
							"</Options>" +
						"</Collection>" +
					"</Collections>" +
				"</Sync>";
		ByteArrayInputStream is = new ByteArrayInputStream(xmlActiveSync.getBytes("UTF-8"));		
		Document doc = DOMUtils.parse(is);
		
		byte[] byteDoc = WBXMLTools.toWbxml("AirSync", doc);

		Assert.assertThat(new String(byteDoc), 
				StringContains.containsString(expectedString));
		
	}
}
