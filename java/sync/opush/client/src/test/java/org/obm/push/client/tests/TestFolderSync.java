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

import java.io.InputStream;

import org.junit.Ignore;
import org.obm.push.utils.DOMUtils;
import org.w3c.dom.Document;
@Ignore
public class TestFolderSync extends AbstractPushTest {

	public void testFolderSync() throws Exception {
		InputStream in = loadDataFile("FolderSyncRequest.xml");
		Document doc = DOMUtils.parse(in);
		Document ret = postXml("FolderHierarchy", doc, "FolderSync");
		assertNotNull(ret);
	}
	
	public void testFolderSyncBadSyncKey() throws Exception {
		InputStream in = loadDataFile("FolderSyncRequestBadSyncKey.xml");
		Document doc = DOMUtils.parse(in);
		
		Document ret = postXml25("FolderHierarchy", doc, "FolderSync");
		assertNotNull(ret);
		
		ret = postXml120("FolderHierarchy", doc, "FolderSync");
		assertNotNull(ret);
		
		ret = postXml("FolderHierarchy", doc, "FolderSync");
		assertNotNull(ret);
	}

}
