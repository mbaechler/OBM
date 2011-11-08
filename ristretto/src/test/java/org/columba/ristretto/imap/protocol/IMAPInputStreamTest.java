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
package org.columba.ristretto.imap.protocol;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Random;

import org.columba.ristretto.imap.IMAPException;
import org.columba.ristretto.imap.IMAPFlags;
import org.columba.ristretto.imap.IMAPInputStream;
import org.columba.ristretto.imap.IMAPListener;
import org.columba.ristretto.imap.IMAPProtocol;
import org.columba.ristretto.imap.IMAPResponse;
import org.junit.Assert;
import org.junit.Test;

public class IMAPInputStreamTest {
	
	boolean IMAPHandlerCalled; 
	
	public void testLiteralSimple() throws Exception {
		String test = "* LIST () \"/\" {8}\r\n12345678\r\n";
		InputStream in = new ByteArrayInputStream( test.getBytes() );		
		IMAPInputStream imap = new IMAPInputStream( in, null );
		
		IMAPResponse response = imap.readResponse();
		
		Assert.assertEquals( "() \"/\" {0}", response.getResponseMessage() );
		Assert.assertEquals( "12345678", response.getData("{0}").toString());
	}
	
	@Test
	public void testLiteralMultiple() throws Exception {
		String test = "* LIST () {8}\r\n12345678 {8}\r\n87654321\r\n";
		InputStream in = new ByteArrayInputStream( test.getBytes() );		
		IMAPInputStream imap = new IMAPInputStream( in, null );
		
		IMAPResponse response = imap.readResponse();
		
		Assert.assertTrue( response.getResponseMessage().equals("() {0} {1}") );
		Assert.assertTrue( response.getData("{0}").toString().equals("12345678"));
		Assert.assertTrue( response.getData("{1}").toString().equals("87654321"));
	}
	
	@Test
	public void testReadBodyNonBlock1() throws Exception {
	    byte[] test = new byte[100000];
	    new Random().nextBytes(test);
	    String text = "* 12 FETCH (BODY.PEEK {100000}\r\n";
	    String rest = ")\r\na004 OK FETCH completed\r\n";
	    InputStream in = new SequenceInputStream(new SequenceInputStream(new ByteArrayInputStream(text.getBytes("US-ASCII")),new ByteArrayInputStream( test) ),new ByteArrayInputStream( rest.getBytes("US-ASCII")) );
	    IMAPInputStream imap = new IMAPInputStream( in, null );
	    
	    InputStream result = imap.readBodyNonBlocking();
	    
	    for( int i=0; i<100000; i++) {
	    	Assert.assertEquals( test[i], (byte) result.read());	    
	    }
	}

	@Test
	public void testReadBodyNonBlock2() throws Exception {
	    byte[] test = new byte[1000];
	    new Random().nextBytes(test);
	    String text = "* 12 FETCH (BODY.PEEK {1000}\r\n";
	    String rest = ")\r\na004 OK FETCH completed\r\n";
	    InputStream in = new SequenceInputStream(new SequenceInputStream(new ByteArrayInputStream(text.getBytes("US-ASCII")),new ByteArrayInputStream( test) ),new ByteArrayInputStream( rest.getBytes("US-ASCII")) );
	    IMAPInputStream imap = new IMAPInputStream( in, null );
	    
	    InputStream result = imap.readBodyNonBlocking();
	    
	    for( int i=0; i<1000; i++) {
	    	Assert.assertEquals( test[i], (byte) result.read());	    
	    }
	}

	@Test
	public void testReadBodyNonBlock3() throws Exception {
	    byte[] test = "This is short body".getBytes("US-ASCII");
	    String text = "* 12 FETCH (BODY \"This is short body\" )\r\n";
	    String rest = ")\r\na004 OK FETCH completed\r\n";
	    InputStream in = new SequenceInputStream(new SequenceInputStream(new ByteArrayInputStream(text.getBytes("US-ASCII")),new ByteArrayInputStream( test) ),new ByteArrayInputStream( rest.getBytes("US-ASCII")) );
	    IMAPInputStream imap = new IMAPInputStream( in, null );
	    
	    InputStream result = imap.readBodyNonBlocking();
	    
	    for( int i=0; i<test.length; i++) {
	    	Assert.assertEquals( test[i], (byte) result.read());	    
	    }
	}

	@Test
	public void testReadBodyNonBlockUnsolicited() throws Exception {
		IMAPHandlerCalled = false;
	    byte[] test = "This is short body".getBytes("US-ASCII");
	    String text = "* 2048 EXISTS\r\n* 12 FETCH (BODY \"This is short body\" )\r\n";
	    String rest = ")\r\na004 OK FETCH completed\r\n";
	    InputStream in = new SequenceInputStream(new SequenceInputStream(new ByteArrayInputStream(text.getBytes("US-ASCII")),new ByteArrayInputStream( test) ),new ByteArrayInputStream( rest.getBytes("US-ASCII")) );
	    IMAPProtocol testProtocol = new IMAPProtocol("localhost", 123);
	    testProtocol.addIMAPListener(new IMAPListener() {
	    	public void connectionClosed(String message, String responseCode) {}
	    	
	    	public void flagsChanged(String mailbox, IMAPFlags flags) {}
	    	
	    	public void existsChanged(String mailbox, int exists) {
	    		Assert.assertEquals(2048, exists);
	    		
	    		IMAPHandlerCalled = true;
	    	}
	    	
	    	public void recentChanged(String mailbox, int recent) {}
	    	
	    	public void parseError(String message) {}
	    	
	    	public void alertMessage(String message) {}
	    	
	    	public void warningMessage(String message) {}

	    	});
	    
	    IMAPInputStream imap = new IMAPInputStream( in, testProtocol );
	    
	    
	    InputStream result = imap.readBodyNonBlocking();
	    Assert.assertTrue( IMAPHandlerCalled );
	    
	    for( int i=0; i<test.length; i++) {
	    	Assert.assertEquals( test[i], (byte) result.read());	    
	    }
	}

	@Test
	public void testReadBodyNonBlockUnsolicitedError() throws Exception {
		IMAPHandlerCalled = false;
	    String text = "* NO Error 6 processing FETCH command.\r\nR511 NO Error 6 processing FETCH command.\r\n";
	    IMAPProtocol testProtocol = new IMAPProtocol("localhost", 123);
	    testProtocol.addIMAPListener(new IMAPListener() {
	    	public void connectionClosed(String message, String responseCode) {}
	    	
	    	public void flagsChanged(String mailbox, IMAPFlags flags) {}
	    	
	    	public void existsChanged(String mailbox, int exists) {
	    	}
	    	
	    	public void recentChanged(String mailbox, int recent) {}
	    	
	    	public void parseError(String message) {}
	    	
	    	public void alertMessage(String message) {}
	    	
	    	public void warningMessage(String message) {}

	    	});
	    
	    IMAPInputStream imap = new IMAPInputStream( new ByteArrayInputStream(text.getBytes()), testProtocol );
	    
	    
	    try {
			InputStream result = imap.readBodyNonBlocking();
			
			// An Exception should have been thrown!
			Assert.assertTrue(false);			
		} catch (IMAPException e) {
			Assert.assertTrue(true);
		}
	}

}
