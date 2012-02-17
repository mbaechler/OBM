/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.sync.book;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.obm.sync.utils.DOMUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class BookItemsParserTest {
	
	private BookItemsParser parser;
	
	@Before
	public void initCalendarParser(){
		parser = new BookItemsParser();
	}
	
	@Test
	public void testGetEventKeyListAxXml() throws SAXException, IOException, FactoryConfigurationError {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<contactkeylist xmlns=\"http://www.obm.org/xsd/sync/contactkeylist.xsd\">" +
				"<key uid=\"1\" addressBookUid=\"3\"/>" +
				"<key uid=\"2\" addressBookUid=\"4\"/>$" +
				"</contactkeylist>";
		
		
		Document doc = DOMUtils.parse(new ByteArrayInputStream(xml.getBytes()));
		List<ContactKey> keyList = parser.parseContactKeyList(doc);
		
		ContactKey contactKey1 = new ContactKey(1,  3);
		ContactKey contactKey2 = new ContactKey(2,  4);
		
		Assert.assertEquals(2, keyList.size());
		Assert.assertTrue(keyList.contains(contactKey1));
		Assert.assertTrue(keyList.contains(contactKey2));
		
	}
}
