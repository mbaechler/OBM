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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.io.SourceInputStream;
import org.junit.Assert;
import org.junit.Test;

public class Base64DecoderInputStreamTest {

	@Test
	public void test0Pads() {
		String input = "/4BA/4BA";
		CharSequenceSource source = new CharSequenceSource( input );
		Base64DecoderInputStream base64in= new Base64DecoderInputStream( new SourceInputStream(source));
		int[] result = { 0x0ff, 0x080, 0x040, 0x0ff, 0x080, 0x040 };
		int pos = 0;
		
		try {
			int nextIn = base64in.read();
			while( nextIn != -1 ) {
				Assert.assertTrue( nextIn == result[pos++]);
				nextIn = base64in.read();						 		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue( pos == 6 );
	}

	@Test
	public void test1Pads() {
		String input = "/4BA/4A=";
		CharSequenceSource source = new CharSequenceSource( input );
		Base64DecoderInputStream base64in= new Base64DecoderInputStream( new SourceInputStream(source));
		int[] result = { 0x0ff, 0x080, 0x040, 0x0ff, 0x080, 0x040 };
		int pos = 0;
		
		try {
			int nextIn = base64in.read();
			while( nextIn != -1 ) {
				Assert.assertTrue( nextIn == result[pos++]);
				nextIn = base64in.read();						 		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue( pos == 5 );
	}


	@Test
	public void test2Pads() {
		String input = "/4BA/w==";
		CharSequenceSource source = new CharSequenceSource( input );
		Base64DecoderInputStream base64in= new Base64DecoderInputStream( new SourceInputStream(source));
		int[] result = { 0x0ff, 0x080, 0x040, 0x0ff, 0x080, 0x040 };
		int pos = 0;
		
		try {
			int nextIn = base64in.read();
			while( nextIn != -1 ) {
				Assert.assertTrue( nextIn == result[pos++]);
				nextIn = base64in.read();						 		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue( pos == 4 );
	}

	@Test
	public void testBreak() {
		String input = "/4B\nA/4\r\nBA";
		CharSequenceSource source = new CharSequenceSource( input );
		Base64DecoderInputStream base64in= new Base64DecoderInputStream( new SourceInputStream(source));
		int[] result = { 0x0ff, 0x080, 0x040, 0x0ff, 0x080, 0x040 };
		int pos = 0;
		
		try {
			int nextIn = base64in.read();
			while( nextIn != -1 ) {
				Assert.assertTrue( nextIn == result[pos++]);
				nextIn = base64in.read();						 		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue( pos == 6 );
	}
	
	@Test
	public void testencodedecode() {
		Random random = new Random();
		byte[] testInput = new byte[(int) (random.nextFloat() * 1024)];
		random.nextBytes(testInput);
		InputStream in = new Base64DecoderInputStream( new Base64EncoderInputStream( new ByteArrayInputStream(testInput)));
		try {
			for( int i=0; i<testInput.length; i++) {
				byte next = (byte) in.read();
				if(testInput[i] != next ) {
					System.out.println( i + " " + testInput[i] + " != "+ next);
				}
				Assert.assertTrue(testInput[i] == next);			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
