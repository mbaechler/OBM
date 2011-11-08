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
package org.columba.ristretto.pop3.protocol;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.io.Source;
import org.columba.ristretto.io.StreamUtils;
import org.columba.ristretto.pop3.POP3Exception;
import org.columba.ristretto.pop3.POP3Protocol;
import org.columba.ristretto.pop3.ScanListEntry;
import org.columba.ristretto.pop3.UidListEntry;
import org.columba.ristretto.testserver.SimpleTestServerSession;
import org.columba.ristretto.testserver.TestServer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class POP3Protocol2Test {

	@Test
	public void testOpenPort() throws IOException, POP3Exception {
       
        
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		 protocol.openPort();
		 Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.quit();
		
		testServer.stop();
	}
	
	@Test
	public void testPlainLogin() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}

	@Test
	public void testApopLogin() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK POP3 server ready <1896.697170952@dbc.mtview.ca.us>\r\n", "QUIT\r\n");
		testSession.addDialog("APOP mrose c4c9334bac560ecc979e58001b3e22fb\r\n", "+OK\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.apop("mrose", "tanstaaf".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}
	
	@Test
	public void testLoginServerDrop() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "USER test\r\n");
		testSession.addDialog("USER test\r\n", "");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		try {
			protocol.userPass("test", "star".toCharArray());
			Assert.assertTrue(false);
		} catch (IOException e) {
			Assert.assertTrue(true);
		} 
		
		Assert.assertTrue( protocol.getState() == POP3Protocol.NOT_CONNECTED);
		
		testServer.stop();
	}
	
	@Test
	public void testStat() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("STAT\r\n", "+OK 2 230\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		int[] stat = protocol.stat();
		Assert.assertTrue( stat[0] == 2);
		Assert.assertTrue( stat[1] == 230);
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}

	@Test
	public void testList() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("LIST\r\n", "+OK 2 messages\r\n1 150\r\n2 230\r\n.\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		ScanListEntry[] list = protocol.list();
		Assert.assertTrue( list.length == 2);
		Assert.assertTrue( list[0].getIndex() == 1);
		Assert.assertTrue( list[0].getSize() == 150);		
		
		Assert.assertTrue( list[1].getIndex() == 2);
		Assert.assertTrue( list[1].getSize() == 230);
		
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}

	@Test
	public void testListParam() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("LIST 2\r\n", "+OK 2 230\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		ScanListEntry list = protocol.list(2);
		Assert.assertTrue( list.getIndex() == 2);
		Assert.assertTrue( list.getSize() == 230);
		
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}
	
	@Test
	public void testRetrSizeOk() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("RETR 1\r\n", "+OK 150 octets\r\nThis is a simple Test\r\nmessage\r\n.. with a dot at start\r\n.\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		String messageOriginal = "This is a simple Test\r\nmessage\r\n. with a dot at start\r\n";
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		
		Source message = new CharSequenceSource( StreamUtils.readInString( protocol.retr(1 ,messageOriginal.length()) ) );		
		Assert.assertEquals( messageOriginal, message.toString());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}
	
	@Test
	public void testRetrSizeSmaller() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("RETR 1\r\n", "+OK 150 octets\r\nThis is a simple Test\r\nmessage\r\n.. with a dot at start\r\n.\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		String messageOriginal = "This is a simple Test\r\nmessage\r\n. with a dot at start\r\n";
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		
		Source message = new CharSequenceSource( StreamUtils.readInString( protocol.retr(1 ,messageOriginal.length()-5) ) );		
		Assert.assertEquals( messageOriginal, message.toString());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}

	@Test
	public void testRetrSizeBigger() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("RETR 1\r\n", "+OK 150 octets\r\nThis is a simple Test\r\nmessage\r\n.. with a dot at start\r\n.\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		String messageOriginal = "This is a simple Test\r\nmessage\r\n. with a dot at start\r\n";
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		
		Source message = new CharSequenceSource( StreamUtils.readInString( protocol.retr(1 ,messageOriginal.length()+5) ) );		
		Assert.assertEquals( messageOriginal, message.toString());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}
	
	@Test
	public void testDele() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("DELE 1\r\n", "+OK\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		Assert.assertTrue( protocol.dele(1));
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}

	@Test
	public void testNoop() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("NOOP\r\n", "+OK\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.noop();
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}
	
	@Test
	public void testRset() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("RSET\r\n", "+OK\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.rset();
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}

	@Test
	public void testTop() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("TOP 1 1\r\n", "+OK\r\nThis is the first line\r\n.\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		Source top = protocol.top(1,1);
		Assert.assertTrue( top.toString().equals("This is the first line\r\n"));
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}

	@Test
	public void testUidl() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("UIDL\r\n", "+OK 2 messages\r\n1 uid1\r\n2 uid2\r\n.\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		UidListEntry[] list = protocol.uidl();
		Assert.assertTrue( list.length == 2);
		Assert.assertTrue( list[0].getIndex() == 1);
		Assert.assertTrue( list[0].getUid().equals("uid1"));		
		
		Assert.assertTrue( list[1].getIndex() == 2);
		Assert.assertTrue( list[1].getUid().equals("uid2"));
		
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}

	@Test
	public void testUidlParam() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("USER test\r\n", "+OK\r\n");
		testSession.addDialog("PASS star\r\n", "+OK user authenticated\r\n");
		testSession.addDialog("UIDL 2\r\n", "+OK 2 uid2\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		protocol.userPass("test", "star".toCharArray());
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		UidListEntry list = protocol.uidl(2);
		Assert.assertTrue( list.getIndex() == 2);
		Assert.assertTrue( list.getUid().equals("uid2"));
		
		Assert.assertTrue( protocol.getState() == POP3Protocol.TRANSACTION);
		
		protocol.quit();
		
		testServer.stop();
	}
	
	@Test
	public void testCapa() throws IOException, POP3Exception {
		SimpleTestServerSession testSession = new SimpleTestServerSession("+OK <test@timestamp>\r\n", "QUIT\r\n");
		testSession.addDialog("CAPA\r\n", "+OK\r\nTOP\r\nAUTH\r\n.\r\n");
		testSession.addDialog("QUIT\r\n", "+OK und tschuess\r\n");

		TestServer testServer = new TestServer(50110, testSession);

		POP3Protocol protocol = new POP3Protocol("localhost", 50110);
		
		protocol.openPort();
		Assert.assertTrue( protocol.getState() == POP3Protocol.AUTHORIZATION);
		
		String[] list = protocol.capa();
		
		Assert.assertTrue( list.length == 2);
		Assert.assertTrue( list[0].equals("TOP"));
		Assert.assertTrue( list[1].equals("AUTH"));
		
		protocol.quit();
		
		testServer.stop();
	}
	
	
    /**
     * @see junit.framework.TestCase#setUp()
     */
	@BeforeClass
    public static void setUp() throws Exception {
        Logger.getLogger("org.columba.ristretto").setLevel(Level.ALL);
    }

}
