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
package org.columba.ristretto.message.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.columba.ristretto.io.ByteBufferSource;
import org.columba.ristretto.io.FileSource;
import org.columba.ristretto.io.Source;
import org.columba.ristretto.io.TempSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TempSourceFactoryTest {

	private StringBuffer bufferSmall;
	private StringBuffer bufferBig;	

	private static final int SMALL = 1000;
	private static final int BIG = 100000;

	/*
	 * @see TestCase#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		Random random = new Random();
		bufferSmall = new StringBuffer(SMALL);
		for( long i=0; i<SMALL; i++ ) {
			bufferSmall.append( (char) ((random.nextFloat() * 48.) + 'A'));
		}
		
		
		bufferBig = new StringBuffer(BIG);
		for( long i=0; i<BIG; i++ ) {
			bufferBig.append( (char) ((random.nextFloat() * 48.) + 'A'));
		}
	}
	
	@Test
	public void testSmallInput() throws IOException {
		InputStream in = new ByteArrayInputStream( bufferSmall.toString().getBytes("ISO-8859-1") );		
		Source tempSource = TempSourceFactory.createTempSource(in, SMALL);

		Assert.assertTrue( tempSource instanceof ByteBufferSource );
		Assert.assertEquals( bufferSmall.toString(), tempSource.toString() );		
	}

	@Test
	public void testSmallInputUnknownSize() throws IOException {
		InputStream in = new ByteArrayInputStream( bufferSmall.toString().getBytes("ISO-8859-1") );		
		Source tempSource = TempSourceFactory.createTempSource(in);

		Assert.assertTrue( tempSource instanceof FileSource );
		Assert.assertEquals( bufferSmall.toString(), tempSource.toString() );		
	}

	@Test
	public void testBigInput() throws IOException {
		InputStream in = new ByteArrayInputStream( bufferBig.toString().getBytes("ISO-8859-1") );		
		Source tempSource = TempSourceFactory.createTempSource(in, BIG);

		Assert.assertTrue( tempSource instanceof FileSource );
		Assert.assertEquals( bufferBig.toString(), tempSource.toString() );		
	}

	@Test
	public void testBigInputUnknownSize() throws IOException {
		InputStream in = new ByteArrayInputStream( bufferBig.toString().getBytes("ISO-8859-1") );		
		Source tempSource = TempSourceFactory.createTempSource(in);

		Assert.assertTrue( tempSource instanceof FileSource );
		Assert.assertEquals( bufferBig.toString(), tempSource.toString() );		
	}

}
