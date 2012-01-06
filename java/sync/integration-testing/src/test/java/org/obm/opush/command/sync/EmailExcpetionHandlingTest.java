package org.obm.opush.command.sync;

import static org.obm.opush.IntegrationTestUtils.buildOpushClient;
import static org.obm.opush.command.sync.EmailSyncTestUtils.checkSyncDefaultMailFolderHasAddItems;
import static org.obm.opush.command.sync.EmailSyncTestUtils.mockEmailSyncClasses;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.obm.opush.ActiveSyncServletModule.OpushServer;
import org.obm.opush.PortNumber;
import org.obm.opush.SingleUserFixture;
import org.obm.opush.SingleUserFixture.OpushUser;
import org.obm.opush.env.JUnitGuiceRule;
import org.obm.push.backend.DataDelta;
import org.obm.push.utils.collection.ClassToInstanceAgregateView;
import org.obm.sync.push.client.Add;
import org.obm.sync.push.client.Folder;
import org.obm.sync.push.client.FolderSyncResponse;
import org.obm.sync.push.client.FolderType;
import org.obm.sync.push.client.OPClient;
import org.obm.sync.push.client.SyncResponse;

import com.google.inject.Inject;

public class EmailExcpetionHandlingTest {

	@Rule
	public JUnitGuiceRule guiceBerry = new JUnitGuiceRule(SyncHandlerTestModule.class);

	@Inject @PortNumber int port;
	@Inject SingleUserFixture singleUserFixture;
	@Inject OpushServer opushServer;
	@Inject ClassToInstanceAgregateView<Object> classToInstanceMap;

	private List<OpushUser> fakeTestUsers;

	@Before
	public void init() {
		fakeTestUsers = Arrays.asList(singleUserFixture.jaures);
	}
	
	@After
	public void shutdown() throws Exception {
		opushServer.stop();
	}

	@Ignore
	@Test
	public void testSyncOneInboxMail() throws Exception {
		String initialSyncKey = "0";
		String syncEmailSyncKey = "13424";
		int syncEmailCollectionId = 432;
		//int emailChangeCount = 1;
		//DataDelta delta = createDelta(syncEmailCollectionId, emailChangeCount, 0, new Date());
		DataDelta delta = null;
		mockEmailSyncClasses(syncEmailSyncKey, syncEmailCollectionId, delta, fakeTestUsers, classToInstanceMap);
		opushServer.start();

		OPClient opClient = buildOpushClient(singleUserFixture.jaures, port);
		FolderSyncResponse folderSyncResponse = opClient.folderSync(initialSyncKey);
		Folder inbox = folderSyncResponse.getFolders().get(FolderType.DEFAULT_INBOX_FOLDER);
		SyncResponse syncEmailResponse = opClient.syncEmail(syncEmailSyncKey, inbox.getServerId());
		checkSyncDefaultMailFolderHasAddItems(inbox, syncEmailResponse, new Add(syncEmailCollectionId + ":" + 0));
	}
	
}
