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

import org.junit.Assert;
import org.junit.Test;


/**
 * @author tstich
 */
public class MailboxNameUTF7ConverterTest {

	@Test
	public void testEncode1() {
		String input = "plain";
		
		Assert.assertEquals(input, MailboxNameUTF7Converter.encode(input));
	}
	
	@Test
	public void testEncode2() {
		String input = "plain&";
		
		Assert.assertEquals("plain&-", MailboxNameUTF7Converter.encode(input));
	}

	@Test
	public void testEncode3() {
		String input = "\u00e4bel";
		
		Assert.assertEquals("&AOQ-bel", MailboxNameUTF7Converter.encode(input));
	}
	

	@Test
	public void testEncode4() {
		String input = "\u00e4bel\u00f6";
		
		Assert.assertEquals("&AOQ-bel&APY-", MailboxNameUTF7Converter.encode(input));
	}

	@Test
	public void testEncode5() {
		String input = "\u00e4bel\u00f6";
		
		Assert.assertEquals("&AOQ-bel&APY-", MailboxNameUTF7Converter.encode(input));
	}
	
	@Test
	public void testEncodeNihongo() {
		String input = "\u65E5\u672C\u8A9E";
		
		Assert.assertEquals("&ZeVnLIqe-", MailboxNameUTF7Converter.encode(input));
	}
	
	@Test
	public void testEncodetalef() {
		String input = "t\u05D0";
		
		Assert.assertEquals("t&BdA-", MailboxNameUTF7Converter.encode(input));
	}

	@Test
	public void testEncodePath() {
		String input = "~peter/mail/\u53F0\u5317/\u65E5\u672C\u8A9E";
		
		Assert.assertEquals("~peter/mail/&U,BTFw-/&ZeVnLIqe-", MailboxNameUTF7Converter.encode(input));
	}

	@Test
	public void testDecodePath() {
		String input = "~peter/mail/&U,BTFw-/&ZeVnLIqe-";
		
		Assert.assertEquals("~peter/mail/\u53F0\u5317/\u65E5\u672C\u8A9E", MailboxNameUTF7Converter.decode(input));
	}
	
	@Test
	public void testDecode2() {
		String input = "plain";
		
		Assert.assertEquals("plain", MailboxNameUTF7Converter.decode(input));
	}

	@Test
	public void testDecode3() {
		String input = "&AOQ-bel";
		
		Assert.assertEquals("\u00e4bel", MailboxNameUTF7Converter.decode(input));
	}
	
	@Test
	public void testDecode1() {
		String input = "plain&-";
		
		Assert.assertEquals("plain&", MailboxNameUTF7Converter.decode(input));
	}
	
	
}
