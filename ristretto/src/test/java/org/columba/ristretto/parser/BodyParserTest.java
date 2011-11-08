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
package org.columba.ristretto.parser;

import java.io.IOException;

import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.io.Source;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MimeHeader;
import org.columba.ristretto.message.MimePart;
import org.junit.Assert;
import org.junit.Test;

public class BodyParserTest {
	
	@Test
	public void testSimpleBody() {
		MimeHeader mimeHeader = new MimeHeader();
		String testmail = "This is a test\r\n";		
		try {
			LocalMimePart message = BodyParser.parseMimePart(mimeHeader, new CharSequenceSource(testmail));
			Source source = message.getBody();
			Assert.assertTrue( source.length() == testmail.length() );			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMultipartBody() {
		MimeHeader mimeHeader = new MimeHeader("multipart","mixed");
		mimeHeader.putContentParameter("boundary","+?*");
		String testmail = "This is a test\r\n--+?*\r\nContent-Type: text/plain\r\n\r\n1\r\n--+?*\r\nContent-Type: text/plain\r\n\r\n2\r\n--+?*--\r\n";		
		try {
			LocalMimePart message = BodyParser.parseMimePart(mimeHeader, new CharSequenceSource(testmail));
			Source source = message.getBody();
			Assert.assertTrue( source.length() == testmail.length() );
			Assert.assertTrue( message.countChilds() == 2);
			Source body1 = ((LocalMimePart)message.getChild(0)).getBody();
			Assert.assertEquals( "1", body1.toString());
			Source body2 = ((LocalMimePart)message.getChild(1)).getBody();
			Assert.assertTrue(  body2.toString().equals("2"));
						
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMultipartBody2() {
		MimeHeader mimeHeader = new MimeHeader("multipart","mixed");
		mimeHeader.putContentParameter("boundary","=_alternative 0047EBBC85256D9F_=");
		String testmail = "This is a test\r\n--=_alternative 0047EBBC85256D9F_=\r\nContent-Type: text/plain\r\n\r\n1\r\n--=_alternative 0047EBBC85256D9F_=\r\nContent-Type: text/plain\r\n\r\n2\r\n--=_alternative 0047EBBC85256D9F_=--\r\n";		
		try {
			LocalMimePart message = BodyParser.parseMimePart(mimeHeader, new CharSequenceSource(testmail));
			Source source = message.getBody();
			Assert.assertTrue( source.length() == testmail.length() );
			Assert.assertTrue( message.countChilds() == 2);
			Source body1 = ((LocalMimePart)message.getChild(0)).getBody();
			Assert.assertTrue( body1.toString().equals("1"));
			Source body2 = ((LocalMimePart)message.getChild(1)).getBody();
			Assert.assertTrue(  body2.toString().equals("2"));
						
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMultipartMultipartBody() {
		MimeHeader mimeHeader = new MimeHeader("multipart","mixed");
		mimeHeader.putContentParameter("boundary","boundary");
		String testmail = "This is a test\r\n--boundary\r\nContent-Type: text/plain\r\n\r\n1\r\n--boundary\r\nContent-Type: multipart/mixed; boundary=\"bound2\"\r\n\r\nblablbala\r\n--bound2\r\nContent-Type: text/plain\r\n\r\n21\r\n--bound2\r\nContent-Type: text/plain\r\n\r\n22\r\n--bound2--\r\n\r\n--boundary--\r\n";		
		try {
			LocalMimePart message = BodyParser.parseMimePart(mimeHeader, new CharSequenceSource(testmail));
			Source source = message.getBody();
			Assert.assertTrue( source.length() == testmail.length() );
			Assert.assertTrue( message.countChilds() == 2);
			Source body1 = ((LocalMimePart)message.getChild(0)).getBody();
			Assert.assertTrue( body1.toString().equals("1"));
			MimePart nestedMultipart = (MimePart)message.getChild(1);
			Source body21 = ((LocalMimePart)nestedMultipart.getChild(0)).getBody();
			Assert.assertTrue( body21.toString().equals("21"));
			Source body22 = ((LocalMimePart)nestedMultipart.getChild(1)).getBody();
			Assert.assertTrue( body22.toString().equals("22"));
						
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMultipartBodyNoStartBoundary() {
	    MimeHeader mimeHeader = new MimeHeader("multipart","mixed");
	    mimeHeader.putContentParameter("boundary","willNotFind");
	    String testmail = "This is a test\r\n--+?*\r\nContent-Type: text/plain\r\n\r\n1\r\n--+?*\r\nContent-Type: text/plain\r\n\r\n2\r\n--+?*--\r\n";		
	    try {
	        LocalMimePart message = BodyParser.parseMimePart(mimeHeader, new CharSequenceSource(testmail));
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (ParserException e) {
	    	Assert.assertTrue(true);
	        System.err.println( e.getSource() );
	        return;
	    }
	    
	    Assert.assertTrue( false );
	}
	
	@Test
	public void testMultipartBodyMissingEndBoundary() {
		MimeHeader mimeHeader = new MimeHeader("multipart","mixed");
		mimeHeader.putContentParameter("boundary","+?*");
		String testmail = "This is a test\r\n--+?*\r\nContent-Type: text/plain\r\n\r\n1\r\n--+?*\r\nContent-Type: text/plain\r\n\r\n2";		
		try {
			LocalMimePart message = BodyParser.parseMimePart(mimeHeader, new CharSequenceSource(testmail));
			Source source = message.getBody();
			Assert.assertTrue( source.length() == testmail.length() );
			Assert.assertTrue( message.countChilds() == 2);
			Source body1 = ((LocalMimePart)message.getChild(0)).getBody();
			Assert.assertEquals( "1", body1.toString());
			Source body2 = ((LocalMimePart)message.getChild(1)).getBody();
			Assert.assertEquals(  "2", body2.toString());
						
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
	
	
}
