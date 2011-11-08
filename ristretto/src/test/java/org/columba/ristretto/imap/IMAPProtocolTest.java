/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Ristretto Mail API.
 *
 * The Initial Developers of the Original Code are
 * Timo Stich and Frederik Dietz.
 * Portions created by the Initial Developers are Copyright (C) 2004
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package org.columba.ristretto.imap;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.columba.ristretto.coder.Base64;
import org.columba.ristretto.io.ConnectionDroppedException;
import org.columba.ristretto.log.RistrettoLogger;
import org.columba.ristretto.message.MailboxInfo;
import org.columba.ristretto.message.MimeTree;
import org.columba.ristretto.testserver.SimpleTestServerSession;
import org.columba.ristretto.testserver.TestServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IMAPProtocolTest {

    /** JDK 1.4+ logging framework logger, used for logging. */
    private static final Logger LOG = Logger.getLogger("org.columba.ristretto.imap");
    
    private TestServer testServer;
	boolean flagsUpdated = false;
    
	@Test
    public void testOpenCloseConnection() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n", "LOGOUT");
		testSession.addDialog("0 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n0 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        Assert.assertEquals( IMAPProtocol.NON_AUTHENTICATED, protocol.getState());
        
        protocol.logout();
        Assert.assertEquals( IMAPProtocol.NOT_CONNECTED, protocol.getState());
        
		testServer.stop();
		System.out.println("--- next test");
    }
    
	@Test
    public void testPreAuthOpenCloseConnection() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* PREAUTH IMAP4rev1 server logged in as Smith\r\n", "LOGOUT");
		testSession.addDialog("0 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n0 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        Assert.assertEquals( IMAPProtocol.AUTHENTICATED, protocol.getState());
        
        protocol.logout();
        Assert.assertEquals( IMAPProtocol.NOT_CONNECTED, protocol.getState());
        
		testServer.stop();        
		System.out.println("--- next test");
    }

	@Test
    public void testConnectionRefused() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* BYE IMAP4rev1 Connection refused\r\n","LOGOUT");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        try {
            protocol.openPort();
            Assert.fail();
        } catch (IMAPException e) {
        	Assert.assertEquals( "IMAP4rev1 Connection refused", e.getMessage() );
        }
        Assert.assertEquals( IMAPProtocol.NOT_CONNECTED, protocol.getState());        
        
		testServer.stop();
		System.out.println("--- next test");
    }

	@Test
    public void testCapabilitiy() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n", "LOGOUT");
		testSession.addDialog("0 CAPABILITY\r\n", "* CAPABILITY IMAP4rev1 STARTTLS AUTH=GSSAPI\r\n0 OK CAPABILITY completed\r\n");
		testSession.addDialog("1 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n1 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);
		String[] capas = new String[] { "IMAP4rev1","STARTTLS","AUTH=GSSAPI"};
		
		
		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        String[] serverCaps = protocol.capability();
        for( int i=0; i<capas.length; i++) {
        	Assert.assertEquals(capas[i], serverCaps[i]);
        }
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }
    
	@Test
    public void testNoop() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 NOOP\r\n", "0 OK NOOP completed\r\n");
		testSession.addDialog("1 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n1 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.noop();
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

	@Test
    public void testAuthenticate() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n", "LOGOUT");
		testSession.addDialog("0 AUTHENTICATE LOGIN\r\n", "+ \r\n");
		testSession.addDialog(Base64.encode("test") + "\r\n", "+ \r\n");
		testSession.addDialog(Base64.encode("user") + "\r\n", "0 OK AUTHENTICATE LOGIN authentication successful\r\n");
		testSession.addDialog("1 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n1 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.authenticate("LOGIN", "test", "user".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

	@Test
    public void testLogin() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n1 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

	@Test
    public void testDropConnection() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN\0");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        try {
			protocol.login("SMITH", "SESAME".toCharArray());
			Assert.assertTrue(false);
		} catch (ConnectionDroppedException e) {
			Assert.assertTrue(true);
			
		} finally {
			testServer.stop();
			System.out.println("--- next test");			
		}
    }
    
	@Test
    public void testCreate() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n", "LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 CREATE owatagusiam/\r\n", "1 OK CREATE completed\r\n");
		
		testSession.addDialog("2 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n2 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());        
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.create("owatagusiam/");        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }
    
	@Test
    public void testDelete() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n", "LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 DELETE blurdybloop\r\n", "1 OK DELETE completed\r\n");
		
		testSession.addDialog("2 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n2 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());        
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.delete("blurdybloop");        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

	@Test
    public void testRename() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 RENAME blurdybloop sarasoop\r\n", "1 OK RENAME completed\r\n");
		
		testSession.addDialog("2 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n2 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());        
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.rename("blurdybloop", "sarasoop");        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

	@Test
    public void testSubscribe() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n", "LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN completed\r\n");
		testSession.addDialog("1 SUBSCRIBE #news.comp.mail.mime\r\n", "1 OK SUBSCRIBE completed\r\n");
		
		testSession.addDialog("2 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n2 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());        
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.subscribe("#news.comp.mail.mime");        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }
    
	@Test
    public void testList() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n", "LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 LIST ~/Mail/ \"%\"\r\n", "* LIST (\\Noselect) \"/\" ~/Mail/foo\r\n* LIST () \"/\" ~/Mail/meetings\r\n1 OK LIST completed\r\n");
		
		testSession.addDialog("2 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n2 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());        
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        ListInfo[] result = protocol.list("~/Mail/","%");
        Assert.assertEquals(2,  result.length);
        Assert.assertEquals( "~/Mail/foo", result[0].getName());
        Assert.assertEquals( "~/Mail/meetings", result[1].getName());
        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

	@Test
    public void testNamespace() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n", "LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 NAMESPACE\r\n", "* NAMESPACE ((\"\" \"/\")) NIL NIL\r\n1 OK NAMESPACE command completed\r\n");
		
		testSession.addDialog("2 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n2 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());        
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        
        NamespaceCollection result = protocol.namespace();

        Assert.assertEquals( "", result.getPersonalNamespace().getPrefix());
        Assert.assertEquals("/", result.getPersonalNamespace().getDelimiter());
        Assert.assertEquals( null, result.getOtherUserNamespace());
        Assert.assertEquals( null, result.getSharedNamespace());
		
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

	@Test
    public void testUnsubscribe() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n", "LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 UNSUBSCRIBE #news.comp.mail.mime\r\n", "1 OK UNSUBSCRIBE completed\r\n");
		
		testSession.addDialog("2 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n2 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());        
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.unsubscribe("#news.comp.mail.mime");        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

    @Test
    public void testLsub() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n", "LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 LSUB #news. \"comp.mail.*\"\r\n", "* LSUB () \".\" #news.comp.mail.mime\r\n1 OK LSUB completed\r\n");
		
		testSession.addDialog("2 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n2 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());        
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        ListInfo[] result = protocol.lsub("#news.","comp.mail.*");
        Assert.assertEquals(1,  result.length);
        Assert.assertEquals( "#news.comp.mail.mime", result[0].getName());
        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }
    
    @Test
    public void testStatus() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 STATUS blurdybloop (UIDNEXT MESSAGES)\r\n", "* STATUS blurdybloop (MESSAGES 231 UIDNEXT 44292)\r\n1 OK STATUS completed\r\n");
		
		testSession.addDialog("2 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n2 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        MailboxStatus status = protocol.status("blurdybloop", new String[] {"UIDNEXT","MESSAGES"});
        Assert.assertEquals(status.getName(), "blurdybloop");
        
        protocol.logout();        

		testServer.stop();
    }

    @Test
    public void testSelect() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n2 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        MailboxInfo info = protocol.select("blurdybloop");
        Assert.assertTrue(info.isWriteAccess());
        Assert.assertEquals( IMAPProtocol.SELECTED, protocol.getState());
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }
    
    @Test
    public void testClose() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 CLOSE\r\n", "2 OK CLOSE completed\r\n");
		testSession.addDialog("3 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n3 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.select("blurdybloop");
        Assert.assertEquals( IMAPProtocol.SELECTED, protocol.getState());
        protocol.close();
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

    @Test
    public void testExpunge() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 EXPUNGE\r\n", "* 3 EXPUNGE\r\n* 3 EXPUNGE\r\n* 5 EXPUNGE\r\n* 7 EXPUNGE\r\n2 OK EXPUNGE completed\r\n");
		testSession.addDialog("3 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n3 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.select("blurdybloop");
        Assert.assertEquals( IMAPProtocol.SELECTED, protocol.getState());
        int[] expunged = protocol.expunge();
        Assert.assertEquals( 4, expunged.length);
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

    @Test
    public void testSearch() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 SEARCH FLAGGED (SINCE 01-Feb-1994) NOT (FROM Smith)\r\n", "* SEARCH 2 84 882\r\n2 OK SEARCH completed\r\n");
		testSession.addDialog("3 SEARCH (TEXT \"string not in mailbox\")\r\n", "* SEARCH\r\n3 OK SEARCH completed\r\n");
		testSession.addDialog("4 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n4 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.select("blurdybloop");
        Assert.assertEquals( IMAPProtocol.SELECTED, protocol.getState());
        
        Calendar cal = Calendar.getInstance();
        cal.set(1994,1,1);
        
        SearchKey[] testSearch = new SearchKey[] {
                new SearchKey( SearchKey.FLAGGED),
                new SearchKey( SearchKey.SINCE, new IMAPDate(cal.getTime())),
                new SearchKey( SearchKey.NOT),
                new SearchKey( SearchKey.FROM, "Smith")
        };
        
        Integer[] result = protocol.search(testSearch);
        Assert.assertEquals( 3, result.length);
        
        testSearch = new SearchKey[] {
                 new SearchKey( SearchKey.TEXT, "string not in mailbox")
        };
        result = protocol.search(testSearch);
        Assert.assertEquals( 0, result.length);
        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

    @Test
    public void testStore() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 STORE 3 +FLAGS.SILENT (\\Flagged \\Deleted)\r\n", "* 3 FETCH (FLAGS (\\Flagged \\Deleted))\r\n2 OK STORE completed\r\n");
		testSession.addDialog("3 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n3 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.select("blurdybloop");
        Assert.assertEquals( IMAPProtocol.SELECTED, protocol.getState());
        IMAPFlags flags = new IMAPFlags();
        flags.setFlagged(true);
        flags.setDeleted(true);
        protocol.store(new SequenceSet(3), true, flags);
        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }
    
    @Test
    public void testUnsolicitedQuit() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n* BYE Astalavista\r\n");
		testSession.addDialog("2 STORE 3 +FLAGS.SILENT (\\Flagged \\Deleted)\r\n", "* 3 FETCH (FLAGS (\\Flagged \\Deleted))\r\n2 OK STORE completed\r\n");
		testSession.addDialog("3 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n3 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.select("blurdybloop");
        Assert.assertEquals( IMAPProtocol.NOT_CONNECTED, protocol.getState());

		testServer.stop();
		System.out.println("--- next test");
    }
    
    @Test
    public void testFetchFlags() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 FETCH 1:* (FLAGS UID)\r\n", "* 1 FETCH (FLAGS (\\Flagged \\Deleted) UID 100)\r\n2 OK FETCH completed\r\n");
		testSession.addDialog("3 UID FETCH 100 (FLAGS UID)\r\n", "* 1 FETCH (FLAGS (\\Flagged \\Deleted) UID 100)\r\n3 OK FETCH completed\r\n");
		testSession.addDialog("4 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n4 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.select("blurdybloop");
        Assert.assertEquals( IMAPProtocol.SELECTED, protocol.getState());
        
        IMAPFlags[] flags = protocol.fetchFlags(SequenceSet.getAll());
        Assert.assertEquals( 1, flags.length );
        Assert.assertTrue( flags[0].getFlagged() );
        Assert.assertTrue( flags[0].getDeleted() );

        flags = protocol.uidFetchFlags(new SequenceSet((Integer)flags[0].getUid()));
        Assert.assertEquals( 1, flags.length );
        Assert.assertTrue( flags[0].getFlagged() );
        Assert.assertTrue( flags[0].getDeleted() );
        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

    @Test
    public void testFetchHeaderLiteral() throws Exception {
    	flagsUpdated = false;
    	
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 UID FETCH 192 (RFC822.SIZE BODY.PEEK[HEADER.FIELDS (Subject Date From To Reply-To Message-ID In-Reply-To References)])\r\n", "* 169 FETCH (UID 192 RFC822.SIZE 5035 BODY[HEADER.FIELDS (\"Subject\" \"Date\" \"From\" \"To\" \"Reply-To\" \"Message-ID\" \"In-Reply-To\" \"References\")] {380}\r\nMessage-ID: <35703.194.39.131.39.1118302427.squirrel@194.39.131.39>\r\nIn-Reply-To: <1118300780.27726.14.camel@pcwab.lpzu.siemens.de>\r\nReferences: <1118300780.27726.14.camel@pcwab.lpzu.siemens.de>\r\nSubject: Re: [Columba-devel] JSCF 0.3 released\r\nFrom: \"Frederik Dietz\" <mail@frederikdietz.de>\r\nTo: columba-devel@lists.sourceforge.net\r\nDate: Thu, 9 Jun 2005 09:33:47 +0200 (CEST)\r\n\r\n)\r\n* 169 FETCH (FLAGS (\\Seen))\r\n2 OK FETCH completed.\r\n");
		testSession.addDialog("3 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n3 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.select("blurdybloop");
        Assert.assertEquals( IMAPProtocol.SELECTED, protocol.getState());
        
        protocol.addIMAPListener(new IMAPListener() {

			public void connectionClosed(String message, String responseCode) {
				// TODO Auto-generated method stub
				
			}

			public void flagsChanged(String mailbox, IMAPFlags flags) {
				flagsUpdated = true;				
			}

			public void existsChanged(String mailbox, int exists) {
				// TODO Auto-generated method stub
				
			}

			public void recentChanged(String mailbox, int recent) {
				// TODO Auto-generated method stub
				
			}

			public void parseError(String message) {
				// TODO Auto-generated method stub
				
			}

			public void alertMessage(String message) {
				// TODO Auto-generated method stub
				
			}

			public void warningMessage(String message) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        IMAPHeader[] header = protocol.uidFetchHeaderFields(new SequenceSet(192),new String[] {"Subject", "Date", "From", "To", "Reply-To", "Message-ID", "In-Reply-To", "References"});
        Assert.assertEquals( 1, header.length );
        Assert.assertTrue( flagsUpdated );
        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }
    
    
    @Test
    public void testNoUnsolicitedFlagsResponse() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 UID STORE 134 +FLAGS.SILENT (JUNK)\r\n", "* FLAGS (JUNK \\Draft \\Answered \\Flagged \\Deleted \\Seen \\Recent)\r\n* OK [PERMANENTFLAGS (JUNK \\* \\Draft \\Answered \\Flagged \\Deleted \\Seen)] Limited\r\n2 OK STORE completed.\r\n");
		testSession.addDialog("3 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n3 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.select("blurdybloop");
        Assert.assertEquals( IMAPProtocol.SELECTED, protocol.getState());
        
        protocol.addIMAPListener(new IMAPListener() {

			public void connectionClosed(String message, String responseCode) {
				// TODO Auto-generated method stub
				
			}

			public void flagsChanged(String mailbox, IMAPFlags flags) {
				Assert.assertTrue(false);
			}

			public void existsChanged(String mailbox, int exists) {
				// TODO Auto-generated method stub
				
			}

			public void recentChanged(String mailbox, int recent) {
				// TODO Auto-generated method stub
				
			}

			public void parseError(String message) {
				// TODO Auto-generated method stub
				
			}

			public void alertMessage(String message) {
				// TODO Auto-generated method stub
				
			}

			public void warningMessage(String message) {
				// TODO Auto-generated method stub
				
			}});
        IMAPFlags flags = new IMAPFlags();
        flags.setJunk(true);
        protocol.uidStore(new SequenceSet(134), true, flags);
        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }

    @Test
    public void testFetchBodystructure() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 UID FETCH 1216 BODYSTRUCTURE\r\n", "* 612 FETCH (FLAGS (\\SEEN))\r\n* 612 FETCH (UID 1216 BODYSTRUCTURE (\"TEXT\" \"PLAIN\" (\"CHARSET\" \"ISO-8859-1\") NIL NIL \"7BIT\" 1890 35 NIL NIL NIL))\r\n2 OK FETCH completed\r\n");
		testSession.addDialog("3 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n3 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.select("blurdybloop");
        Assert.assertEquals( IMAPProtocol.SELECTED, protocol.getState());
        
        MimeTree flags = protocol.uidFetchBodystructure(1216);
        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }
    
    @Test
    public void testNoUnsolicitedFlagsResponse2() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 EXPUNGE\r\n", "* 495 EXPUNGE\r\n* 507 EXISTS\r\n* 76 RECENT\r\n* FLAGS (JUNK \\Draft \\Answered \\Flagged \\Deleted \\Seen \\Recent)\r\n* OK [PERMANENTFLAGS (JUNK \\* \\Draft \\Answered \\Flagged \\Deleted \\Seen)] Limited\r\n2 OK EXPUNGE completed.\r\n");
		testSession.addDialog("3 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n3 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        Assert.assertEquals( protocol.getState(), IMAPProtocol.AUTHENTICATED);
        protocol.select("blurdybloop");
        Assert.assertEquals( IMAPProtocol.SELECTED, protocol.getState());
        
        protocol.addIMAPListener(new IMAPListener() {

			public void connectionClosed(String message, String responseCode) {
				// TODO Auto-generated method stub
				
			}

			public void flagsChanged(String mailbox, IMAPFlags flags) {
				Assert.assertTrue(false);
			}

			public void existsChanged(String mailbox, int exists) {
				// TODO Auto-generated method stub
				
			}

			public void recentChanged(String mailbox, int recent) {
				// TODO Auto-generated method stub
				
			}

			public void parseError(String message) {
				// TODO Auto-generated method stub
				
			}

			public void alertMessage(String message) {
				// TODO Auto-generated method stub
				
			}

			public void warningMessage(String message) {
				// TODO Auto-generated method stub
				
			}});
        IMAPFlags flags = new IMAPFlags();
        flags.setJunk(true);
        int[] expunged = protocol.expunge();
        Assert.assertEquals(1,expunged.length);
        Assert.assertEquals(495,expunged[0]);
        
        protocol.logout();        

		testServer.stop();
		System.out.println("--- next test");
    }    
    
    @Test
    public void testIdle() throws Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("* OK IMAP4rev1 Service Ready\r\n","LOGOUT");
		testSession.addDialog("0 LOGIN SMITH SESAME\r\n", "0 OK LOGIN copmleted\r\n");
		testSession.addDialog("1 SELECT blurdybloop\r\n", "1 OK [READ-WRITE] SELECT completed\r\n");
		testSession.addDialog("2 IDLE\r\n", "+ idling\r\n* 4 EXISTS\r\n");
		testSession.addDialog("DONE\r\n", "2 OK IDLE terminated\r\n");		
		testSession.addDialog("3 FETCH 4 (FLAGS UID)\r\n", "* 4 FETCH (FLAGS (\\Flagged \\Deleted))\r\n3 OK FETCH completed\r\n");
		testSession.addDialog("4 LOGOUT\r\n", "* BYE IMAP4rev1 Server logging out\r\n4 OK LOGOUT completed\r\n");

		testServer = new TestServer(50110, testSession);

		IMAPProtocol protocol = new IMAPProtocol("localhost", 50110);
		flagsUpdated = false;
		
		protocol.addIMAPListener(new IMAPListener() {

			public void connectionClosed(String message, String responseCode) {
				// TODO Auto-generated method stub
				
			}

			public void flagsChanged(String mailbox, IMAPFlags flags) {
				// TODO Auto-generated method stub
				
			}

			public void existsChanged(String mailbox, int exists) {
				Assert.assertEquals(4, exists);
				flagsUpdated = true;
			}

			public void recentChanged(String mailbox, int recent) {
				// TODO Auto-generated method stub
				
			}

			public void parseError(String message) {
				// TODO Auto-generated method stub
				
			}

			public void alertMessage(String message) {
				// TODO Auto-generated method stub
				
			}

			public void warningMessage(String message) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
        protocol.openPort();
        protocol.login("SMITH", "SESAME".toCharArray());
        protocol.select("blurdybloop");
        protocol.idle();        
        Assert.assertEquals(IMAPProtocol.IDLE, protocol.getState());
        Assert.assertTrue(flagsUpdated);
        protocol.fetchFlags(new SequenceSet(4));
        Assert.assertEquals(IMAPProtocol.SELECTED, protocol.getState());
        
        protocol.logout();
        
		testServer.stop();
		System.out.println("--- next test");
    }
    
    
    
    @Before
    public void setUp() throws Exception {
        LOG.setLevel(Level.ALL);
        RistrettoLogger.setLogStream(System.out);
    }
    
    @After
    public void tearDown() throws Exception {
        testServer.stop();
    }
    
    
}
