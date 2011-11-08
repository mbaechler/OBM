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
package org.columba.ristretto.coder;

import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

public class EncodedWordTest {

	@Test
	public void testDecodeNone() {
		String input = "This is a simple Test";

		StringBuffer result = EncodedWord.decode(input);
		Assert.assertTrue(result.toString().equals(input));
	}

	@Test
	public void testDecodeSimple() {
		String input = "This is a =?iso-8859-1?q?s=FCmple?= Test";

		StringBuffer result = EncodedWord.decode(input);
		Assert.assertTrue(result.toString().equals("This is a s\u00fcmple Test"));
	}

	@Test
	public void testDecodeWhitespaceNone() {
		String input = "=?ISO-8859-1?Q?a?==?ISO-8859-1?Q?b?=";

		StringBuffer result = EncodedWord.decode(input);
		Assert.assertTrue(result.toString().equals("ab"));
	}

	@Test
	public void testDecodeWhitespaceMulti() {
		String input = "=?ISO-8859-1?Q?a?=  =?ISO-8859-1?Q?b?=";

		StringBuffer result = EncodedWord.decode(input);
		Assert.assertTrue(result.toString().equals("ab"));
	}

	@Test
	public void testDecodeSimpleWithWS() {
		String input = "This is a =?iso-8859-1?q?s=FCmple_?=Test";

		StringBuffer result = EncodedWord.decode(input);
		Assert.assertTrue(result.toString().equals("This is a s\u00fcmple Test"));
	}

	@Test
	public void testDecodeBase64() {
		String input =
			"=?ISO-8859-1?B?SWYgeW91IGNhbiByZWFkIHRoaXMgeW8=?==?ISO-8859-2?B?dSB1bmRlcnN0YW5kIHRoZSBleGFtcGxlLg==?=";

		StringBuffer result = EncodedWord.decode(input);
		Assert.assertTrue(
			result.toString().equals(
				"If you can read this you understand the example."));
	}

	@Test
	public void testEncodeNone() {
		String input = "This is a test";

		StringBuffer result =
			EncodedWord.encode(
				input,
				Charset.forName("ISO-8859-1"),
				EncodedWord.QUOTED_PRINTABLE);
		Assert.assertEquals("This is a test", result.toString());
	}

	@Test
	public void testEncodeSimple() {
		String input = "This is a s\u00fcmple test";

		StringBuffer result =
			EncodedWord.encode(
				input,
				Charset.forName("ISO-8859-1"),
				EncodedWord.QUOTED_PRINTABLE);
		Assert.assertTrue(
			result.toString().equals(
				"This is a =?ISO-8859-1?q?s=FCmple_?=test"));
	}

	@Test
	public void testEncodeCombined() {
		String input = "This is a s\u00fcmple s\u00fcmple test";

		StringBuffer result =
			EncodedWord.encode(
				input,
				Charset.forName("ISO-8859-1"),
				EncodedWord.QUOTED_PRINTABLE);
		Assert.assertEquals("This is a =?ISO-8859-1?q?s=FCmple_s=FCmple_?=test",
			result.toString()
				);
	}

	@Test
	public void testEncodeSimpleBase64() {
		String input = "This is a s\u00fcmple test";

		StringBuffer result =
			EncodedWord.encode(
				input,
				Charset.forName("ISO-8859-1"),
				EncodedWord.BASE64);
		Assert.assertTrue(
			result.toString().equals(
				"This is a =?ISO-8859-1?b?c/xtcGxlIA==?=test"));
	}

	@Test
	public void testEncodeBase64EOF() {
		String input = "This is a s\u00fcmple ";

		StringBuffer result =
			EncodedWord.encode(
				input,
				Charset.forName("ISO-8859-1"),
				EncodedWord.BASE64);
		Assert.assertTrue(
			result.toString().equals(
				"This is a =?ISO-8859-1?b?c/xtcGxlIA==?="));
	}


	@Test
	public void testEncodelong() {
		String input =
			"This is a s\u00fcmplebutmegalongencodedwordsothatthesecondencodedwordmustbe s\u00fcmple test";

		StringBuffer result =
			EncodedWord.encode(
				input,
				Charset.forName("ISO-8859-1"),
				EncodedWord.QUOTED_PRINTABLE);
		Assert.assertEquals("This is a =?ISO-8859-1?q?s=FCmplebutmegalongencodedwordsothatthesecondencodedwordmu?=stbe =?ISO-8859-1?q?s=FCmple_?=test",
			result.toString());
	}
	
	@Test
	public void testEncodelong2() {
		String input =
			"This is a s\u00fcmplebutmegalongencodedwordsothatthesecondencodedwordmustbesplitwithoutthehelpofawhitespacewhatsoever";

		StringBuffer result =
			EncodedWord.encode(
				input,
				Charset.forName("ISO-8859-1"),
				EncodedWord.BASE64);
		Assert.assertEquals(
			"This is a =?ISO-8859-1?b?c/xtcGxlYnV0bWVnYWxvbmdlbmNvZGVkd29yZHNvdGhhdHRoZXNlYw==?==?ISO-8859-1?b?b25kZW5jb2RlZHdvcmRtdXN0YmVzcGxpdHdpdGhvdXR0aGVoZWxwb2Y=?=awhitespacewhatsoever", result.toString());
	}


}
