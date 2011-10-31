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
package org.obm.push.client.tests;

import java.util.HashMap;
import java.util.Map;

import org.obm.push.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SyncKeyUtils {
	
	public static String getFolderSyncKey(Document doc){
		return DOMUtils.getElementText(doc.getDocumentElement(), "SyncKey");
	}
	
	public static void appendFolderSyncKey(Document doc, String syncKey){
		DOMUtils.getUniqueElement(doc.getDocumentElement(), "SyncKey").setTextContent(syncKey);
	}
	
	public static void fillSyncKey(Element root, Map<String, String> sks) {
		NodeList nl = root.getElementsByTagName("Collection");

		for (int i = 0; i < nl.getLength(); i++) {
			Element col = (Element) nl.item(i);
			String collectionId = DOMUtils.getElementText(col, "CollectionId");
			String syncKey = sks.get(collectionId);
			Element synckeyElem = DOMUtils.getUniqueElement(col, "SyncKey");
			if (synckeyElem == null) {
				synckeyElem = DOMUtils.getUniqueElement(col, "AirSync:SyncKey");
			}
			synckeyElem.setTextContent(syncKey);
		}

	}

	public static Map<String, String> processCollection(Element root) {
		Map<String, String> ret = new HashMap<String, String>();
		NodeList nl = root.getElementsByTagName("Collection");

		for (int i = 0; i < nl.getLength(); i++) {
			Element col = (Element) nl.item(i);
			String collectionId = DOMUtils.getElementText(col, "CollectionId");
			String syncKey = DOMUtils.getElementText(col, "SyncKey");
			ret.put(collectionId, syncKey);
		}
		return ret;
	}

}
