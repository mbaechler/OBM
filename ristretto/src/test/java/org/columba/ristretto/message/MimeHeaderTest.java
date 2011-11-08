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
package org.columba.ristretto.message;

import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.parser.HeaderParser;
import org.junit.Assert;
import org.junit.Test;

public class MimeHeaderTest {

	@Test
	public void testDefaultHeader() {
		MimeHeader header = new MimeHeader();
		MimeType type = header.getMimeType();
		Assert.assertTrue( type.getType().equals("text"));
		Assert.assertTrue( type.getSubtype().equals("plain"));
	}
	
	@Test
	public void testContentParameterSimple() {
		Header header = new Header();
		header.set("Content-Type","multipart/mixed;boundary= \"blabla\"");
		MimeHeader mimeHeader = new MimeHeader(header);
		Assert.assertTrue(mimeHeader.getContentParameter("boundary").equals("blabla"));
	}
	
	@Test
	public void testContentParameterMultiple() {
		Header header = new Header();
		header.set("Content-Type","multipart/mixed; charset=\"iso-8859-1\"; boundary=\"bla bla\"");
		MimeHeader mimeHeader = new MimeHeader(header);
		Assert.assertEquals(mimeHeader.getContentParameter("boundary"), "bla bla");
		Assert.assertEquals(mimeHeader.getContentParameter("charset"), "iso-8859-1");
	}
	
	@Test
	public void testWOQuotes() {
		Header header= new Header();
		header.set("Content-type", "text/html; charset=iso-8859-1");
		MimeHeader mimeHeader = new MimeHeader( header );
		Assert.assertEquals( mimeHeader.getContentParameter("charset"), "iso-8859-1");
		Assert.assertEquals( "html", mimeHeader.getMimeType().getSubtype());
	}
	
	@Test
	public void testFromParsedHeader() throws Exception {
		String test = "Return-Path: <xxxx@xxxxxxxx.de>\r\n" +
				"X-Flags: 0000\r\n" +
				"Delivered-To: xxxxxxxxxxxxx}\r\n" +
				"Received: (qmail 392 invoked by uid 65534); 23 Jun 2004 \r\n" +
				"12:55:08 -0000\r\n" +
				"Received: from xxxxxxxxxxxx (EHLO xxxxxxxxxxxx) \r\n" +
				"(xxx.xxx.xxx.xxx) \r\n" +
				"by xxxxxxxxx (xxxx) with SMTP; 23 Jun 2004 14:55:08 \r\n" +
				"+0200\r\n" +
				"Received: from xxxxxxx by xxxxxxxxxxxxx with local (Exim \r\n" +
				"3.35 #1 (Debian)) \r\n" +
				"id xxxxxxxxxxxxxxxx \r\n " +
				"for <xxxxxxx@xxxxxxx>; Wed, 23 Jun 2004 \r\n" +
				"14:55:04 +0200\r\n" +
				"Content-Type: text/plain; charset=iso-8859-1\r\n" +
				"Content-Transfer-Encoding: 8bit\r\n" +
				"MIME-Version: 1.0\r\n" +
				"From: \"Fxxxxxx\" <xxxxxxxxx@xxxxxxxxxxx>\r\n" +
				"To: xxxxxxxxxx@xxxxxxxxxxxxx\r\n" +
				"Reply-to: \"Fxxxxxx\" <xxxxxxxxxx@xxxxxxxxxx>\r\n" +
				"Subject: xxxxxxxxxxxxx\r\n" +
				"MIME-Version: 1.0\r\n" +
				"Content-Type: text/plain; charset=iso-8859-1\r\n" +
				"Content-Transfer-Encoding: 8bit\r\n" +
				"X-Script: xxxxxxxxxx.pl\r\n" +
				"Message-Id: <xxxxxxxxxxx@xxxxxxxxxxxx>\r\n" +
				"Date: Wed, 23 Jun 2004 14:55:04 +0200\r\n\r\n";
		
		Header header = HeaderParser.parse(new CharSequenceSource( test ));
		MimeHeader mimeHeader = new MimeHeader( header );
		Assert.assertEquals( "iso-8859-1", mimeHeader.getContentParameter("charset") );
		
	}
	
}
