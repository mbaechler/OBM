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
package org.columba.ristretto.composer;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.TimeZone;

import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.message.Address;
import org.columba.ristretto.message.BasicHeader;
import org.columba.ristretto.message.Header;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MimeHeader;
import org.columba.ristretto.message.MimeType;
import org.columba.ristretto.message.StreamableMimePart;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MimeTreeRendererTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
		//timsparg
		//need to set the timezone to what i guess is German
		// time so that the test can pass
		TimeZone t = TimeZone.getTimeZone("GMT+1");
		TimeZone.setDefault(t);
	}
	
	@Test
	public void testComposeSimple() {
		Header header = new Header(); 
		String body = "Body";
		BasicHeader basicHeader = new BasicHeader( header );
		basicHeader.setFrom( new Address( "Timo Stich", "tstich@users.sourceforge.net"));
		basicHeader.setTo( new Address[] { new Address( "Timo Stich", "tstich@users.sourceforge.net" )});
		basicHeader.setSubject( "This is a test mail", Charset.forName("US-ASCII"));
		basicHeader.setDate(new Date(0));
		
		MimeHeader mimeHeader = new MimeHeader(header);
		mimeHeader.setMimeType(new MimeType("text", "plain"));
		mimeHeader.setContentTransferEncoding("7bit");
		mimeHeader.putContentParameter("charset", "us-ascii");
		
		StreamableMimePart mimepart = new LocalMimePart( mimeHeader, new CharSequenceSource(body));
		
		
		// Read from stream 
		try {
			StringBuffer result = new StringBuffer();
			InputStream messageStream = MimeTreeRenderer.getInstance().renderMimePart(mimepart);
			int next = messageStream.read();
			while( next != -1 ) {
				result.append((char) next);
				next = messageStream.read();				
			}
			String message = result.toString();
			System.out.println( message );
			
			Assert.assertEquals( "Content-Type: text/plain; charset=\"us-ascii\"\r\nDate: Thu, 1 Jan 1970 01:00:00 +0100\r\nSubject: This is a test mail\r\nContent-Transfer-Encoding: 7bit\r\nTo: \"Timo Stich\" <tstich@users.sourceforge.net>\r\nFrom: \"Timo Stich\" <tstich@users.sourceforge.net>\r\n\r\nBody\r\n" , message);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testComposeMultipartMixed() {
		Header header = new Header(); 
		String body = "Body";
		BasicHeader basicHeader = new BasicHeader( header );
		basicHeader.setFrom( new Address( "Timo Stich", "tstich@users.sourceforge.net"));
		basicHeader.setTo( new Address[] { new Address( "Timo Stich", "tstich@users.sourceforge.net" )});
		basicHeader.setSubject( "This is a test mail", Charset.forName("US-ASCII"));
		basicHeader.setDate(new Date(0));
		
		MimeHeader mimeHeader = new MimeHeader(header);
		mimeHeader.setMimeType(new MimeType("multipart", "mixed"));
		
		StreamableMimePart mimepart = new LocalMimePart( mimeHeader, new CharSequenceSource(body));

		mimeHeader = new MimeHeader();
		mimeHeader.setMimeType(new MimeType("text", "plain"));
		mimeHeader.setContentTransferEncoding("7bit");
		mimeHeader.putContentParameter("charset", "us-ascii");
		
		StreamableMimePart subpart = new LocalMimePart( mimeHeader, new CharSequenceSource(body));
		mimepart.addChild( subpart );
		mimepart.addChild( subpart );
		
		
		// Read from stream 
		try {
			StringBuffer result = new StringBuffer();
			InputStream messageStream = MimeTreeRenderer.getInstance().renderMimePart(mimepart);
			int next = messageStream.read();
			while( next != -1 ) {
				result.append((char) next);
				next = messageStream.read();				
			}
			String message = result.toString();
			System.out.println( message );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testComposeQPEncoded() {
		Header header = new Header(); 
		String body = "This is a ?est";
		BasicHeader basicHeader = new BasicHeader( header );
		basicHeader.setFrom( new Address( "Timo Stich", "tstich@users.sourceforge.net"));
		basicHeader.setTo( new Address[] { new Address( "Timo Stich", "tstich@users.sourceforge.net" )});
		basicHeader.setSubject( "This is a test mail", Charset.forName("US-ASCII"));
		basicHeader.setDate(new Date(0));
		
		MimeHeader mimeHeader = new MimeHeader(header);
		mimeHeader.setMimeType(new MimeType("text", "plain"));
		mimeHeader.setContentTransferEncoding("7bit");
		mimeHeader.putContentParameter("charset", "iso-8859-1");
		mimeHeader.setContentTransferEncoding("quoted-printable");
		
		StreamableMimePart mimepart = new LocalMimePart( mimeHeader, new CharSequenceSource(body));
		
		
		// Read from stream 
		try {
			StringBuffer result = new StringBuffer();
			InputStream messageStream = MimeTreeRenderer.getInstance().renderMimePart(mimepart);
			int next = messageStream.read();
			while( next != -1 ) {
				result.append((char) next);
				next = messageStream.read();				
			}
			String message = result.toString();
			System.out.println( message );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
