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

public class QuotedPrintableTest {

	@Test
	public void testDecodeNone() {
		String input = "This is a Test";
		CharSequence result = QuotedPrintable.decode(input, Charset.forName("US-ASCII"));
		
		Assert.assertTrue(result.toString().equals("This is a Test"));
	}

	@Test
	public void testDecodeSimple() {
		String input = "This is a =" + Integer.toHexString((int)'T')+ "est";
		CharSequence result = QuotedPrintable.decode(input, Charset.forName("US-ASCII"));
		
		Assert.assertTrue(result.toString().equals("This is a Test"));
	}

	@Test
	public void testDecodeMulti() {
		String input = "This is a =FC=DCe";
		CharSequence result = QuotedPrintable.decode(input, Charset.forName("ISO-8859-1"));
		
		Assert.assertTrue(result.toString().equals("This is a \u00fc\u00dce"));
	}

	@Test
	public void testDecode8Bit() {
		String input = "This is a =" + Integer.toHexString((int)'\u00dc')+ "est";
		CharSequence result = QuotedPrintable.decode(input, Charset.forName("ISO-8859-1"));
		
		Assert.assertTrue(result.toString().equals("This is a \u00dcest"));
	}
	
	@Test
	public void testDecodeSoftLB() {
		String input = "This is a=\r\n Test";
		CharSequence result = QuotedPrintable.decode(input, Charset.forName("US-ASCII"));
		
		Assert.assertTrue(result.toString().equals("This is a Test"));
	}
	
	@Test
	public void testEncodedNone() {
		String input = "This is a\tTest";
		CharSequence result = QuotedPrintable.encode(input, Charset.forName("US-ASCII"));
		
		Assert.assertTrue(result.toString().equals("This is a\tTest"));
	}
	
	@Test
	public void testEncodedSimple() {
		String input = "This is a \u00e4est";
		CharSequence result = QuotedPrintable.encode(input, Charset.forName("ISO-8859-1"));
		
		Assert.assertTrue(result.toString().equals("This is a =" + Integer.toHexString((int)'\u00e4').toUpperCase()+ "est"));
	}

	@Test
	public void testEncodedLineBreak() {
		String input = "This is a\r\n \u00e4est";
		CharSequence result = QuotedPrintable.encode(input, Charset.forName("ISO-8859-1"));
		
		Assert.assertTrue(result.toString().equals("This is a\r\n =" + Integer.toHexString((int)'\u00e4').toUpperCase()+ "est"));
	}

	@Test
	public void testEncodedLineBreakWhitespace1() {
		String input = "This is a \r\n \u00e4est";
		CharSequence result = QuotedPrintable.encode(input, Charset.forName("ISO-8859-1"));
		
		Assert.assertTrue(result.toString().equals("This is a=20\r\n =" + Integer.toHexString((int)'\u00e4').toUpperCase()+ "est"));
	}
	
	@Test
	public void testEncodedLineBreakWhitespace2() {
		String input = "This is a very long line that has in total some f\u00e4nfundsiebzig +1 characters";
		CharSequence result = QuotedPrintable.encode(input, Charset.forName("ISO-8859-1"));
		
		Assert.assertTrue(result.toString().equals("This is a very long line that has in total some f=" + Integer.toHexString((int)'\u00e4').toUpperCase()+"nfundsiebzig +1 charact=\r\ners"));
	}

}
