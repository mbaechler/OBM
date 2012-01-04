/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 2.0
 *
 * The contents of this file are subject to the GNU General Public
 * License Version 2 or later (the "GPL").
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is
 *   MiniG.org project members
 *
 * ***** END LICENSE BLOCK ***** */

package org.minig.imap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.minig.imap.command.ImapReturn;
import org.obm.push.utils.FileUtils;

@Ignore("It's necessary to do again all tests")
public class BasicStoreTests extends LoggedTestCase {

	private static final int COUNT = 50000;

	public void testCreateSubUnsubRenameDelete() {
		String mbox = "test" + System.currentTimeMillis();
		String newMbox = "rename" + System.currentTimeMillis();
		boolean b = sc.create(mbox);
		assertTrue(b);
		boolean sub = sc.subscribe(mbox);
		assertTrue(sub);
		sub = sc.unsubscribe(mbox);
		assertTrue(sub);

		boolean renamed = sc.rename(mbox, newMbox);
		assertTrue("Rename success : ", renamed);
		boolean del = false;
		if (!renamed) {
			del = sc.delete(mbox);
		} else {
			del = sc.delete(newMbox);
		}
		assertTrue(del);
	}

	public void testAppend() {
		FlagsList fl = new FlagsList();
		fl.add(Flag.SEEN);
		long uid = sc.append("INBOX", getRfc822Message(), fl);
		assertTrue(uid > 0);
		long secondUid = sc.append("INBOX", getUtf8Rfc822Message(), fl);
		assertTrue("Added uids : " + uid + " " + secondUid, secondUid == uid + 1);
	}

	public void testUidFetchMessage() {
		FlagsList fl = new FlagsList();
		fl.add(Flag.SEEN);
		long uid = sc.append("INBOX", getUtf8Rfc822Message(), fl);
		sc.select("INBOX");
		InputStream in = sc.uidFetchMessage(uid);
		assertNotNull(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			FileUtils.transfer(in, out, true);
		} catch (IOException e) {
			fail(e.getMessage());
		}

		for (int i = 0; i < COUNT; i++) {
			sc.uidFetchMessage(uid);
			out = new ByteArrayOutputStream();
			try {
				FileUtils.transfer(in, out, true);
			} catch (IOException e) {
				fail("error");
			}
		}
	}


	public void testUidFetchBodyStructure() {
		FlagsList fl = new FlagsList();
		fl.add(Flag.SEEN);
		Collection<Long> uid = Arrays.asList(
				sc.append("INBOX", getUtf8Rfc822Message(), fl),
				sc.append("INBOX", getRfc822Message(), fl));
		sc.select("INBOX");
		sc.uidFetchBodyStructure(uid);

		for (int i = 0; i < COUNT; i++) {
			sc.uidFetchBodyStructure(uid);
		}

		sc.select("INBOX");
		Collection<Long> allUids = sc.uidSearch(new SearchQuery());
		for (long l : allUids) {
			try {
				sc.uidFetchBodyStructure(Arrays.asList(l));
			} catch (Throwable t) {
				fail(t.getMessage());
			}
		}
	}

	public void testUidSearch() {
		SearchQuery sq = new SearchQuery();
		sc.select("INBOX");
		Collection<Long> uids = sc.uidSearch(sq);
		assertNotNull(uids);
		assertTrue(uids.size() > 0);

		for (int i = 0; i < COUNT; i++) {
			Collection<Long> u = sc.uidSearch(sq);
			assertTrue(u.size() > 0);
		}
	}

	public void testUidFetchHeaders() {
		SearchQuery sq = new SearchQuery();
		sc.select("INBOX");
		Collection<Long> uids = sc.uidSearch(sq);
		String[] headers = new String[] { "date", "from", "subject" };

		long nstime = System.nanoTime();
		Collection<ImapReturn<IMAPHeaders>> h = sc.uidFetchHeaders(uids, headers);
		nstime = System.nanoTime() - nstime;
		assertEquals(uids.size(), h.size());
		
		for (ImapReturn<IMAPHeaders> r : h) {
			assertFalse(r.isError());
			IMAPHeaders header = r.getValue();
			assertNotNull(header.getSubject());
			assertNotNull(header.getDate());
			assertNotNull(header.getFrom().getMail());
			assertNotNull(header.getFrom().getDisplayName());
		}

	}

	public void testUidFetchEnvelopeReliable() {
		SearchQuery sq = new SearchQuery();
		sc.select("INBOX");
		Collection<Long> uids = sc.uidSearch(sq);

		for (long l : uids) {
			try {
				Collection<ImapReturn<Envelope>> h = sc.uidFetchEnvelope(Arrays.asList(l));
				assertEquals(1, h.size());
			} catch (Throwable t) {
				fail(t.getMessage());
			}
		}
	}

	public void testUidFetchFlags() {
		SearchQuery sq = new SearchQuery();
		sc.select("INBOX");
		Collection<Long> uids = sc.uidSearch(sq);

		Iterator<Long> iterator = uids.iterator();
		List<Long> firstTwo = Arrays.asList(iterator.next(), iterator.next());

		long nstime = System.nanoTime();
		Collection<ImapReturn<FlagsList>> h = sc.uidFetchFlags(firstTwo);
		nstime = System.nanoTime() - nstime;
		assertEquals(firstTwo.size(), h.size());

		nstime = System.nanoTime();
		h = sc.uidFetchFlags(uids);
		nstime = System.nanoTime() - nstime;
		assertEquals(uids.size(), h.size());
	}

	public void testUidCopy() {
		SearchQuery sq = new SearchQuery();
		sc.select("INBOX");
		Collection<Long> uids = sc.uidSearch(sq);

		Iterator<Long> it = uids.iterator();
		Collection<Long> firstTwo = Arrays.asList(it.next(), it.next());

		long nstime = System.nanoTime();
		Collection<ImapReturn<Long>> result = sc.uidCopy(firstTwo, "Sent");
		nstime = System.nanoTime() - nstime;
		assertNotNull(result);
		assertEquals(firstTwo.size(), result.size());
	}

	public void testUidStore() {
		SearchQuery sq = new SearchQuery();
		sc.select("INBOX");
		Collection<Long> uids = sc.uidSearch(sq);

		Iterator<Long> it = uids.iterator();
		Collection<Long> firstTwo = Arrays.asList(it.next(), it.next());

		FlagsList fl = new FlagsList();
		fl.add(Flag.ANSWERED);
		long nstime = System.nanoTime();
		boolean result = sc.uidStore(firstTwo, fl, true);
		nstime = System.nanoTime() - nstime;
		assertTrue(result);
		result = sc.uidStore(firstTwo, fl, false);
		assertTrue(result);
	}


	public void testUidFetchPart() {
		SearchQuery sq = new SearchQuery();
		sc.select("INBOX");
		Collection<Long> uids = sc.uidSearch(sq);
		long uid = uids.iterator().next();

		long nstime = System.nanoTime();
		InputStream in = sc.uidFetchPart(uid, "1");
		nstime = System.nanoTime() - nstime;
		assertNotNull(in);
		try {
			FileUtils.dumpStream(in, System.out, true);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

}
