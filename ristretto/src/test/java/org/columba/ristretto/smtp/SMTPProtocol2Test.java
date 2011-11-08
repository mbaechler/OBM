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
package org.columba.ristretto.smtp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;

import org.columba.ristretto.coder.Base64;
import org.columba.ristretto.message.Address;
import org.columba.ristretto.testserver.SimpleTestServerSession;
import org.columba.ristretto.testserver.TestServer;
import org.junit.Assert;
import org.junit.Test;

public class SMTPProtocol2Test{

	@Test
    public void testOpenPort() throws IOException, SMTPException {
        SimpleTestServerSession testSession = new SimpleTestServerSession("220 test.domain Hello\r\n", "QUIT\r\n");
        testSession.addDialog("QUIT\r\n", "250 bye\r\n");

        TestServer testServer = new TestServer(50025, testSession);

        SMTPProtocol protocol = new SMTPProtocol("localhost", 50025);
        
        Assert.assertTrue( protocol.openPort().equals("test.domain") );
        
        protocol.quit();
        
        testServer.stop();
    }
    
	@Test
    public void testOpenPort2() throws IOException, SMTPException {
        SimpleTestServerSession testSession = new SimpleTestServerSession("220  ESMTP\r\n", "QUIT\r\n");
        testSession.addDialog("QUIT\r\n", "250 bye\r\n");

        TestServer testServer = new TestServer(50025, testSession);

        SMTPProtocol protocol = new SMTPProtocol("localhost", 50025);
       
        protocol.openPort();
        
        protocol.quit();
        
        testServer.stop();
    }
    
	@Test
    public void testAuthenticate() throws Exception {
        SimpleTestServerSession testSession = new SimpleTestServerSession("220 test.domain Hello\r\n", "QUIT\r\n");
        testSession.addDialog("AUTH PLAIN\r\n", "334 continue\r\n");
        testSession.addDialog( Base64.encode("\0test\0bar") + "\r\n", "250 OK\r\n");
        testSession.addDialog("QUIT\r\n", "250 bye\r\n");

        TestServer testServer = new TestServer(50025, testSession);

        SMTPProtocol protocol = new SMTPProtocol("localhost", 50025);
        
        Assert.assertTrue( protocol.openPort().equals("test.domain") );
        protocol.auth("PLAIN", "test", "bar".toCharArray());
        
        protocol.quit();
        
        testServer.stop();
    }
    
	@Test
    public void testSimpleSession() throws IOException, SMTPException {
        SimpleTestServerSession testSession = new SimpleTestServerSession("220 test.domain Hello\r\n", "QUIT\r\n");
        testSession.addDialog("MAIL FROM:<smith@bar.com>\r\n", "250 OK\r\n");
        testSession.addDialog("RCPT TO:<Jones@foo.com>\r\n", "250 OK\r\n");
        testSession.addDialog("DATA\r\n", "354 Start mail input; end with <CRLF>.<CRLF>\r\n");
        testSession.addDialog("This is a test mail\r\n", "");        
        testSession.addDialog(".\r\n", "250 OK\r\n");        
        testSession.addDialog("QUIT\r\n", "250 bye\r\n");

        TestServer testServer = new TestServer(50025, testSession);

        SMTPProtocol protocol = new SMTPProtocol("localhost", 50025);
        
        Assert.assertTrue( protocol.openPort().equals("test.domain") );
        
        protocol.mail(new Address("smith@bar.com"));
        protocol.rcpt(new Address("Jones@foo.com"));
        protocol.data(new ByteArrayInputStream("This is a test mail".getBytes()));
        protocol.quit();
        
        testServer.stop();
    }
    
	@Test
    public void testEhloFail() throws IOException, SMTPException {
        SimpleTestServerSession testSession = new SimpleTestServerSession("220  ESMTP\r\n", "QUIT\r\n");
        testSession.addDialog("EHLO [127.0.0.1]\r\n", "500 5.5.1 Command unrecognized: \"XXXX [67.84.198.179]\"\r\n");
        testSession.addDialog("HELO [127.0.0.1]\r\n", "220 OK\r\n");
        testSession.addDialog("QUIT\r\n", "250 bye\r\n");

        TestServer testServer = new TestServer(50025, testSession);

        SMTPProtocol protocol = new SMTPProtocol("localhost", 50025);
       
        InetAddress local = InetAddress.getByAddress(new byte[] {127,0,0,1});
        
        protocol.openPort();
        try {
			protocol.ehlo(local);
			Assert.assertTrue(false);
		} catch (SMTPException e) {
			
		}
        protocol.helo(local);
        
        protocol.quit();
        
        testServer.stop();
    }
    
	@Test
    public void testCapabilities() throws Exception {
        SimpleTestServerSession testSession = new SimpleTestServerSession("220 test.domain Hello\r\n", "QUIT\r\n");
        testSession.addDialog("EHLO [127.0.0.1]\r\n", "250-foo.com greets bar.com\r\n250-AUTH=LOGIN PLAIN\r\n250 HELP\r\n");
        testSession.addDialog("QUIT\r\n", "250 bye\r\n");

        TestServer testServer = new TestServer(50025, testSession);

        SMTPProtocol protocol = new SMTPProtocol("localhost", 50025);        
        Assert.assertTrue( protocol.openPort().equals("test.domain") );
        
        String capas[] = protocol.ehlo(InetAddress.getByAddress(new byte[] {127,0,0,1}));
        Assert.assertEquals(2,capas.length);
        Assert.assertEquals("AUTH=LOGIN PLAIN", capas[0]);
        Assert.assertEquals("HELP", capas[1]);
        
        protocol.quit();
        
        testServer.stop();
    }
    
    
}
