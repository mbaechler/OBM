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

import java.util.LinkedList;
import java.util.List;

import org.obm.push.bean.MoveItem;
import org.obm.push.bean.MoveItemsStatus;
import org.obm.push.exception.activesync.NoDocumentException;
import org.obm.push.protocol.bean.MoveItemsRequest;
import org.obm.push.protocol.bean.MoveItemsResponse;
import org.obm.push.protocol.bean.MoveItemsResponse.MoveItemsItem;
import org.obm.push.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MoveItemsProtocol {

	public MoveItemsProtocol() {
	}
	
	public MoveItemsRequest getRequest(Document doc) throws NoDocumentException {
		if (doc == null) {
			throw new NoDocumentException();
		}
		
		NodeList moves = doc.getDocumentElement().getElementsByTagName("Move");
		List<MoveItem> moveItems = new LinkedList<MoveItem>();
		for (int i = 0; i < moves.getLength(); i++) {
			Element mv = (Element) moves.item(i);

			String srcMsgId = DOMUtils.getElementText(mv, "SrcMsgId");
			String srcFldId = DOMUtils.getElementText(mv, "SrcFldId");
			String dstFldId = DOMUtils.getElementText(mv, "DstFldId");

			MoveItem mi = new MoveItem(srcMsgId, srcFldId, dstFldId);
			moveItems.add(mi);
		}
		
		return new MoveItemsRequest(moveItems);
	}

	public Document encodeResponse(MoveItemsResponse moveItemsResponse) {
		Document reply = DOMUtils.createDoc(null, "MoveItems");
		Element root = reply.getDocumentElement();
		
		for (MoveItemsItem moveItemsItem: moveItemsResponse.getMoveItemsItem()) {
		
			Element response = DOMUtils.createElement(root, "Response");
			
			switch (moveItemsItem.getItemStatus()) {
			case SUCCESS:
				DOMUtils.createElementAndText(response, "Status", MoveItemsStatus.SUCCESS.asXmlValue());
				DOMUtils.createElementAndText(response, "SrcMsgId",	moveItemsItem.getSourceMessageId());
				DOMUtils.createElementAndText(response, "DstMsgId",	moveItemsItem.getNewDstId());
				break;
			case SERVER_ERROR:
				DOMUtils.createElementAndText(response, "SrcMsgId", moveItemsItem.getSourceMessageId());
				DOMUtils.createElementAndText(response, "Status", MoveItemsStatus.SERVER_ERROR.asXmlValue());
				break;
			default:
				DOMUtils.createElementAndText(response, "SrcMsgId", moveItemsItem.getSourceMessageId());
				DOMUtils.createElementAndText(response, "Status", moveItemsItem.getItemStatus().asXmlValue());
				break;
			}			
		}
		return reply;
	}
	
	public Document encodeErrorResponse(MoveItemsStatus moveItemsStatus) {
		Document document = DOMUtils.createDoc(null, "Move");
		Element root = document.getDocumentElement();
		DOMUtils.createElementAndText(root, "Status", moveItemsStatus.asXmlValue());
		return document;
	}
	
}
