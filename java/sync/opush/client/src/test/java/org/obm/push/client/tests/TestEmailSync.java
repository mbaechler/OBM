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
import org.obm.sync.push.client.Collection;
import org.obm.sync.push.client.Folder;
import org.obm.sync.push.client.FolderSyncResponse;
import org.obm.sync.push.client.FolderType;
import org.obm.sync.push.client.SyncResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Ignore("It's necessary to do again all tests")
public class TestEmailSync extends OPClientTests {

	public void testMailSync() throws Exception {
		testOptions();
		FolderSyncResponse fsr = testInitialFolderSync();
		Folder inbox = fsr.getFolders().get(FolderType.DEFAULT_INBOX_FOLDER);
		SyncResponse syncResp = testInitialSync(inbox);
		
		InputStream in = null;
		Document doc = null;
		
		in = loadDataFile("GetItemEstimateRequestEmail.xml");
		doc = DOMUtils.parse(in);
		replace(doc, inbox, syncResp);
		Document estimateRet = postXml("GetItemEstimate", doc, "GetItemEstimate");
		assertNotNull(estimateRet);
		
		
		in = loadDataFile("EmailSyncRequest.xml");
		Document docFirst = DOMUtils.parse(in);
		replace(docFirst, inbox, syncResp);
		syncResp = testSync(docFirst);
		Collection colInbox = syncResp.getCollection(inbox.getServerId());
		assertNotNull(colInbox);
		assertTrue(colInbox.getAdds().size() > 0);
		
		in = loadDataFile("EmailSyncRequest.xml");
		doc = DOMUtils.parse(in);
		replace(doc, inbox, syncResp);
		syncResp = testSync(doc);
		colInbox = syncResp.getCollection(inbox.getServerId());
		assertNotNull(colInbox);
		assertTrue(colInbox.getAdds().size() > 0);
		
		syncResp = testSync(docFirst);
		colInbox = syncResp.getCollection(inbox.getServerId());
		assertNotNull(colInbox);
		assertTrue(colInbox.getAdds().size() > 0);
	}

	public void testMailSync2() throws Exception {
		testOptions();
		FolderSyncResponse fsr = testInitialFolderSync();
		Folder inbox = fsr.getFolders().get(FolderType.DEFAULT_INBOX_FOLDER);
		SyncResponse syncResp = testInitialSync(inbox);
		
		InputStream in = null;
		Document doc = null;
		
		in = loadDataFile("GetItemEstimateRequestEmail.xml");
		doc = DOMUtils.parse(in);
		replace(doc, inbox, syncResp);
		Document estimateRet = postXml("GetItemEstimate", doc, "GetItemEstimate");
		assertNotNull(estimateRet);
		
		
		in = loadDataFile("EmailSyncRequest.xml");
		doc = DOMUtils.parse(in);
		replace(doc, inbox, syncResp);
		syncResp = testSync(doc);
		assertNotNull(syncResp);
		Collection colInbox = syncResp.getCollection(inbox.getServerId());
		assertNotNull(colInbox);
		assertTrue(colInbox.getAdds().size() > 0);

		in = loadDataFile("EmailSyncRequest.xml");
		doc = DOMUtils.parse(in);
		replace(doc, inbox, syncResp);
		syncResp = testSync(doc);
		colInbox = syncResp.getCollection(inbox.getServerId());
		assertNotNull(colInbox);
		assertEquals(0, colInbox.getAdds().size());

	}
	
	public void testMailChangeRead() throws Exception {
		testOptions();
		FolderSyncResponse fsr = testInitialFolderSync();
		Folder inbox = fsr.getFolders().get(FolderType.DEFAULT_INBOX_FOLDER);
		SyncResponse syncResp = testInitialSync(inbox);
		
		InputStream in = null;
		Document doc = null;
		Document ret = null;
		
		in = loadDataFile("GetItemEstimateRequestEmail.xml");
		doc = DOMUtils.parse(in);
		replace(doc, inbox, syncResp);
		ret = postXml("GetItemEstimate", doc, "GetItemEstimate");
		assertNotNull(ret);
		
		in = loadDataFile("EmailSyncRequest.xml");
		doc = DOMUtils.parse(in);
		replace(doc, inbox, syncResp);
		syncResp = testSync(doc);
		assertNotNull(syncResp);
		Collection colInbox = syncResp.getCollection(inbox.getServerId());
		assertNotNull(colInbox);
		assertTrue(colInbox.getAdds().size() > 0);
		String serverId = colInbox.getAdds().get(0).getServerId();
		
		in = loadDataFile("EmailSyncReadRequest.xml");
		doc = DOMUtils.parse(in);
		replace(doc, inbox, syncResp);
		DOMUtils.getUniqueElement(doc.getDocumentElement(), "ServerId").setTextContent(serverId);
		syncResp = testSync(doc);
		syncResp = testSync(doc);
		assertNotNull(syncResp);
		colInbox = syncResp.getCollection(inbox.getServerId());
		assertNotNull(colInbox);
		assertEquals(0, colInbox.getAdds().size());

	}

	public void testMailSyncMultiBodyPref() throws Exception {
		InputStream in = loadDataFile("FolderSyncRequest.xml");
		Document doc = DOMUtils.parse(in);
		Document ret = postXml("FolderHierarchy", doc, "FolderSync");
		assertNotNull(ret);

		in = loadDataFile("EmailSyncRequest.xml");
		doc = DOMUtils.parse(in);
		Element synckeyElem = DOMUtils.getUniqueElement(doc
				.getDocumentElement(), "SyncKey");
		synckeyElem.setTextContent("0");
		DOMUtils.logDom(doc);
		ret = postXml("AirSync", doc, "Sync");
		assertNotNull(ret);

		String sk = DOMUtils.getUniqueElement(ret.getDocumentElement(),
				"SyncKey").getTextContent();
		in = loadDataFile("EmailSyncRequestMultipleBodyPref.xml");
		doc = DOMUtils.parse(in);
		synckeyElem = DOMUtils.getUniqueElement(doc.getDocumentElement(),
				"SyncKey");
		synckeyElem.setTextContent(sk);
		DOMUtils.logDom(doc);
		ret = postXml("AirSync", doc, "Sync");
		assertNotNull(ret);

	}
}
