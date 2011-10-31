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
package org.obm.sync.push.client.commands;

import java.util.HashMap;
import java.util.Map;

import org.obm.push.utils.DOMUtils;
import org.obm.sync.push.client.AccountInfos;
import org.obm.sync.push.client.Folder;
import org.obm.sync.push.client.FolderSyncResponse;
import org.obm.sync.push.client.FolderType;
import org.obm.sync.push.client.OPClient;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Performs a FolderSync AS command with the given sync key
 */
public class FolderSync extends TemplateBasedCommand<FolderSyncResponse> {

	private String syncKey;

	public FolderSync(String syncKey) {
		super(NS.FolderHierarchy, "FolderSync", "FolderSyncRequest.xml");
		this.syncKey = syncKey;
	}

	@Override
	protected void customizeTemplate(AccountInfos ai, OPClient opc) {
		Element sk = DOMUtils.getUniqueElement(tpl.getDocumentElement(),
				"SyncKey");
		sk.setTextContent(syncKey);
	}

	@Override
	protected FolderSyncResponse parseResponse(Element root) {
		String key = DOMUtils.getElementText(root, "SyncKey");
		int count = Integer.parseInt(DOMUtils.getElementText(root, "Count"));
		Map<FolderType, Folder> ret = new HashMap<FolderType, Folder>(count + 1);

		// TODO process updates / deletions
		NodeList nl = root.getElementsByTagName("Add");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			Folder f = new Folder();
			f.setServerId(DOMUtils.getElementText(e, "ServerId"));
			f.setParentId(DOMUtils.getElementText(e, "ParentId"));
			f.setName(DOMUtils.getElementText(e, "DisplayName"));
			f.setType(FolderType.getValue(Integer.parseInt(DOMUtils.getElementText(e, "Type"))));
			ret.put(f.getType(), f);
		}

		return new FolderSyncResponse(key, ret);
	}

}
