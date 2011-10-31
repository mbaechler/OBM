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
package org.obm.push.protocol;

import java.util.HashSet;

import org.obm.push.bean.SyncCollection;
import org.obm.push.protocol.bean.PingRequest;
import org.obm.push.protocol.bean.PingResponse;
import org.obm.push.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PingProtocol {

	public PingRequest getRequest(Document doc) {
		PingRequest pingRequest = new PingRequest();
		if (doc == null) {
			return pingRequest;
		}
		Element pr = doc.getDocumentElement();
		Element hb = DOMUtils.getUniqueElement(pr, "HeartbeatInterval");
		if (hb != null) {
			pingRequest.setHeartbeatInterval(Long.valueOf(hb.getTextContent()));
		}
		HashSet<SyncCollection> syncCollections = new HashSet<SyncCollection>();
		NodeList folders = pr.getElementsByTagName("Folder");
		for (int i = 0; i < folders.getLength(); i++) {
			SyncCollection syncCollection = new SyncCollection();
			Element f = (Element) folders.item(i);
			syncCollection.setDataClass(DOMUtils.getElementText(f, "Class"));
			int id = Integer.valueOf(DOMUtils.getElementText(f, "Id"));
			syncCollection.setCollectionId(id);
			syncCollections.add(syncCollection);
		}
		pingRequest.setSyncCollections(syncCollections);
		return pingRequest;
	}
	
	public Document encodeResponse(PingResponse pingResponse) {
		Document document = DOMUtils.createDoc(null, "Ping");
		Element root = document.getDocumentElement();
		
		DOMUtils.createElementAndText(root, "Status", pingResponse.getPingStatus().asXmlValue());
		Element folders = DOMUtils.createElement(root, "Folders");
		for (SyncCollection sc : pingResponse.getSyncCollections()) {
			DOMUtils.createElementAndText(folders, "Folder", sc.getCollectionId().toString());
		}
		return document;
	}

	public Document buildError(String errorStatus) {
		Document document = DOMUtils.createDoc(null, "Ping");
		Element root = document.getDocumentElement();
		DOMUtils.createElementAndText(root, "Status", errorStatus);
		return document;
	}
	
}
