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

import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class Base64Test {

	@Test
	public void testDecode1Pack0Pads() {
		String input = "/4BA";
		ByteBuffer decoded = Base64.decode(input);
		
		Assert.assertTrue( decoded.capacity()== 3);
		Assert.assertTrue( decoded.get() == (byte) 0x0ff );
		Assert.assertTrue( decoded.get()== (byte) 0x080 );
		Assert.assertTrue( decoded.get() == (byte) 0x040 );		
	}

	@Test
	public void testDecode2Pack0Pads() {
		String input = "/4BA/4BA";
		ByteBuffer decoded = Base64.decode(input);
		
		Assert.assertTrue( decoded.capacity() == 6);
		Assert.assertTrue( decoded.get() == (byte) 0x0ff );
		Assert.assertTrue( decoded.get() == (byte) 0x080 );
		Assert.assertTrue( decoded.get() == (byte) 0x040 );		
		Assert.assertTrue( decoded.get() == (byte) 0x0ff );
		Assert.assertTrue( decoded.get() == (byte) 0x080 );
		Assert.assertTrue( decoded.get() == (byte) 0x040 );		
	}

	@Test
	public void testDecode1Pack1Pads() {
		String input = "/4A=";
		ByteBuffer decoded = Base64.decode(input);
		
		Assert.assertTrue( decoded.limit() == 2);
		Assert.assertTrue( decoded.get() == (byte) 0x0ff );
		Assert.assertTrue( decoded.get() == (byte) 0x080 );
	}

	@Test
	public void testDecode1Pack2Pads() {
		String input = "/w==";
		ByteBuffer decoded = Base64.decode(input);
		
		Assert.assertTrue( decoded.limit() == 1);
		Assert.assertTrue( decoded.get() == (byte) 0x0ff );
	}

	@Test
	public void testDecode1Pack2PadsAndGarbageAtEnd() {
		String input = "/4== asldkfie sdhr oi";
		ByteBuffer decoded = Base64.decode(input);
		
		Assert.assertTrue( decoded.limit() == 1);
		Assert.assertTrue( decoded.get() == (byte) 0x0ff );
	}
	
	@Test
	public void testEncode1Pack0Pads() {
		byte[] input = {(byte)0x0ff, (byte)0x080, (byte)0x040};
		StringBuffer result = Base64.encode(ByteBuffer.wrap(input));
		Assert.assertTrue(result.length() == 4);
		Assert.assertTrue(result.toString().equals("/4BA"));
	}

	@Test
	public void testEncode2Pack0Pads() {
		byte[] input = {(byte)0x0ff, (byte)0x080, (byte)0x040,(byte)0x0ff, (byte)0x080, (byte)0x040};
		StringBuffer result = Base64.encode(ByteBuffer.wrap(input));
		Assert.assertTrue(result.length() == 8);
		Assert.assertTrue(result.toString().equals("/4BA/4BA"));
	}

	@Test
	public void testEncode1Pack1Pads() {
		byte[] input = {(byte)0x0ff, (byte)0x080};
		StringBuffer result = Base64.encode(ByteBuffer.wrap(input));
		Assert.assertTrue(result.length() == 4);
		Assert.assertTrue(result.toString().equals("/4A="));
	}

	@Test
	public void testEncode1Pack2Pads() {
		byte[] input = {(byte)0x0ff};
		StringBuffer result = Base64.encode(ByteBuffer.wrap(input));
		Assert.assertTrue(result.length() == 4);
		Assert.assertTrue(result.toString().equals("/w=="));
	}
	
	@Test
	public void testEncodeDecode() {
		Random random = new Random();
		//byte[] testInput = new byte[(int) (random.nextFloat() * 1024)];
		byte[] testInput = new byte[91];
		random.nextBytes(testInput);
		StringBuffer encoded = Base64.encode(ByteBuffer.wrap(testInput));
		System.out.println( encoded );
		ByteBuffer decoded = Base64.decode(encoded);
		for( int i=0; i<testInput.length; i++) {
			Assert.assertEquals(testInput[i],decoded.get());			
		}
	}

	@Test
	public void testDecodeMalformed() {
		String input = "tqEgx9G55r/vILHuwfYgu/27/cfPsNQgwPzH2LXluLO0z7TZLiC18Lryvbogv7XIrcDHIMPWsO26wCEh=";
		
		ByteBuffer decoded = Base64.decode(input);
	}
	
}
