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


import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.io.Source;
import org.columba.ristretto.message.Header;
import org.junit.Assert;
import org.junit.Test;

public class HeaderParserTest {

	@Test
	public void testInputStreamSimpleFoldedLine() {
		try {
			String testHeaderInput = "Folded: Line\r\n test\r\n\r\n";
			Header testHeader =
				HeaderParser.parse(new CharSequenceSource(testHeaderInput));
			Assert.assertTrue(testHeader.length() == 1);
			Assert.assertTrue(testHeader.get("Folded").equals("Linetest"));

		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInputStreamMultiFoldedLine() {
		try {
			String testHeaderInput =
				"Folded: Line\r\n test\r\n\t multiple\r\n  folded\r\n\r\n";
			Header testHeader =
				HeaderParser.parse(new CharSequenceSource(testHeaderInput));
			Assert.assertTrue(testHeader.length() == 1);
			Assert.assertTrue(
				testHeader.get("Folded").equals("Linetest multiple folded"));

		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInputStreamSimpleLine() {
		try {
			String testHeaderInput = "Simple: Test\r\n\r\n";
			Header testHeader =
				HeaderParser.parse(new CharSequenceSource(testHeaderInput));

			Assert.assertTrue(testHeader.length() == 1);
			Assert.assertTrue(testHeader.get("Simple").equals("Test"));
		} catch (ParserException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testEmptyValue() {
		try {
			String testHeaderInput = "Simple: \r\n\r\n";
			Header testHeader =
				HeaderParser.parse(new CharSequenceSource(testHeaderInput));

			Assert.assertTrue(testHeader.length() == 1);
			Assert.assertTrue(testHeader.get("Simple").equals(""));
		} catch (ParserException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testInputStreamMultiLine() {
		try {
			String testHeaderInput =
				"Simple: Test\r\n" + "Is: not enough\r\n" + "\r\n";
			Header testHeader =
				HeaderParser.parse(new CharSequenceSource(testHeaderInput));

			Assert.assertTrue(testHeader.length() == 2);
			Assert.assertTrue(testHeader.get("Simple").equals("Test"));
			Assert.assertTrue(testHeader.get("Is").equals("not enough"));

		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInputStreamSimpleQuoted() {
		try {
			String testHeaderInput =
				"Simple: bla:Test\" with (simple) Quotes\"\r\n\r\n";
			Header testHeader =
				HeaderParser.parse(new CharSequenceSource(testHeaderInput));

			Assert.assertTrue(testHeader.length() == 1);
			Assert.assertTrue(
				testHeader.get("Simple").equals(
					"bla:Test\" with (simple) Quotes\""));

		} catch (ParserException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testInputStreamQuotedFolded() {
		try {
			String testHeaderInput =
				"Simple: Test\" with (fol\r\n ded) Quotes\"\r\n\r\n";
			Header testHeader =
				HeaderParser.parse(new CharSequenceSource(testHeaderInput));

			Assert.assertTrue(testHeader.length() == 1);
			Assert.assertTrue(
				testHeader.get("Simple").equals(
					"Test\" with (folded) Quotes\""));

		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInputStream8BitSafe() {
		try {
			String testHeaderInput = "Simple: \"??????\"\r\n\r\n";
			Header testHeader =
				HeaderParser.parse(new CharSequenceSource(testHeaderInput));

			Assert.assertTrue(testHeader.length() == 1);
			Assert.assertTrue(testHeader.get("Simple").equals("\"??????\""));

		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBogusQuoted() {
		try {
			String testHeaderInput =
				"Bogus: \"blabl\"bla\"bla\r\n\r\nThis is the body";
			Header testHeader =
				HeaderParser.parse(new CharSequenceSource(testHeaderInput));

			Assert.assertTrue(testHeader.length() == 1);
			Assert.assertTrue(testHeader.get("Bogus").equals("\"blabl\"bla\"bla"));

		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSourcePosition() {
		try {
			String testHeaderInput = "Test: Simple\r\n\r\nThis is the body";
			Source testSource = new CharSequenceSource(testHeaderInput);
			Header testHeader = HeaderParser.parse(testSource);
			String body = testSource.fromActualPosition().toString();
			Assert.assertTrue(body.equals("This is the body"));
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testHeaderEndDetection() {
		try {
			String testHeaderInput =
				"Test: Simple\r\n\r\nThis is the body: but with a header like text\r\n";
			Source testSource = new CharSequenceSource(testHeaderInput);
			Header testHeader = HeaderParser.parse(testSource);
			String body = testSource.fromActualPosition().toString();
			Assert.assertTrue(
				body.equals(
					"This is the body: but with a header like text\r\n"));
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testKeyNormalization() {
		try {
			String testHeaderInput =
				"mesSaGe-id: Simple\r\nIn-Reply-To: Test\r\nsubject: Bla\r\n\r\n";
			Source testSource = new CharSequenceSource(testHeaderInput);
			Header testHeader = HeaderParser.parse(testSource);
			Assert.assertTrue(testHeader.length() == 3);
			Assert.assertTrue(testHeader.get("Message-ID").equals("Simple"));
			Assert.assertTrue(testHeader.get("In-Reply-To").equals("Test"));
			Assert.assertTrue(testHeader.get("Subject").equals("Bla"));
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testKeyIgnoreCase() {
		try {
			String testHeaderInput =
				"mesSaGe-id: Simple\r\nIn-reply-To: Test\r\nsubject: Bla\r\n\r\n";
			Source testSource = new CharSequenceSource(testHeaderInput);
			Header testHeader = HeaderParser.parse(testSource);
			Assert.assertTrue(testHeader.length() == 3);
			Assert.assertTrue(testHeader.get("Message-ID").equals("Simple"));
			Assert.assertTrue(testHeader.get("in-reply-to").equals("Test"));
			Assert.assertTrue(testHeader.get("subJect").equals("Bla"));
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInputStream2() {
		try {
			String testHeaderInput =
				"Message-ID: <8-0-m-77cw0-$11151$3p$h$5@00eif>\n"
					+ "From: \"Goldie Ames\" <c0a2sg83y9s2@aol.com>\n"
					+ "To: freddy@sowi.uni-mannheim.de\n"
					+ "Subject: Find a Mortgage Loan... Refinance, 2nd, Purchase, Home Improvement AA\n"
					+ " hhxcrad i\n"
					+ "Date: Tue, 22 Jan 02 05:48:49 GMT\n"
					+ "Content-Type: multipart/alternative;\n"
					+ " boundary=\".5AEA83D7D_C8FA_E_E_\";\n";

			Header testHeader =
				HeaderParser.parse(new CharSequenceSource(testHeaderInput));

			Assert.assertTrue(testHeader.length() == 6);

		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

}