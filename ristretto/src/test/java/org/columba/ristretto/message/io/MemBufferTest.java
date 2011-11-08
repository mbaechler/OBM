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

import java.util.Random;

import org.columba.ristretto.io.MemBuffer;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tstich
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MemBufferTest {

	@Test
	public void testInputSingle() {
		MemBuffer buffer = new MemBuffer();
		
		byte[] input = new byte[2050];
		new Random().nextBytes(input);
		
		for( int i=0; i<input.length; i++) {
			Assert.assertEquals( i, buffer.size());
			buffer.append(input[i]);
			Assert.assertEquals( input[i], buffer.get(i));
			Assert.assertEquals( i+1, buffer.size());
		}
	}

	@Test
	public void testInputArray() {
		MemBuffer buffer = new MemBuffer();
		
		byte[] input = new byte[2050];
		new Random().nextBytes(input);

		buffer.append(input);
		Assert.assertEquals( input.length, buffer.size());
		
		byte[] test = new byte[2050];
		Assert.assertEquals( 2000, buffer.get(50, test)); 
		Assert.assertEquals( input[50], test[0]);
		
		
		Assert.assertEquals( 2050, buffer.get(0, test) );
		
		for( int i=0; i<input.length; i++) {			
			Assert.assertEquals( input[i], test[i]);
		}
	}
	
}
