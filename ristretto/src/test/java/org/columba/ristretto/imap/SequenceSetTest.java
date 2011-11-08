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

import junitx.framework.ArrayAssert;

import org.junit.Assert;
import org.junit.Test;


public class SequenceSetTest {
    
	@Test
    public void testAll1() {
        SequenceSet s = new SequenceSet();
        s.addAll();
        s.addAll();
        
        Assert.assertEquals("1:*", s.toString());
        
        ArrayAssert.assertEquals(new int[] {1,2,3}, s.toArray(3) );
    }
    
	@Test
    public void testAll2() {
        SequenceSet s = new SequenceSet();
        s.addAll();
        s.add(100);
        
        Assert.assertEquals("1:*", s.toString());
    }

	@Test
    public void testAll3() {
        SequenceSet s = new SequenceSet();
        s.addAll();
        s.add(100,200);
        
        Assert.assertEquals("1:*", s.toString());
    }

	@Test
    public void testAll6() {
        SequenceSet s = new SequenceSet();
        s.add(1,100);
        s.add(100,SequenceEntry.STAR);
        
        Assert.assertEquals("1:*", s.toString());
    }

	@Test
    public void testAll7() {
        SequenceSet s = new SequenceSet();
        s.add(1,100);
        s.add(500);
        s.add(100, SequenceEntry.STAR);
        
        Assert.assertEquals("1:*", s.toString());
    }

	@Test
    public void testAll8() {
        SequenceSet s = new SequenceSet();
        s.add(1,10);
        s.add(100, SequenceEntry.STAR);
        
        Assert.assertEquals("1:10,100:*", s.toString());
        ArrayAssert.assertEquals(new int[] {1,2,3,4,5,6,7,8,9,10,100,101,102}, s.toArray(102));
    }
    
	@Test
    public void testOpenRange1() {
        SequenceSet s = new SequenceSet();
        s.add(1,10);
        s.add(100);
        
        Assert.assertEquals("1:10,100", s.toString());
        
        ArrayAssert.assertEquals(new int[] {1,2,3,4,5,6,7,8,9,10,100}, s.toArray(13));
    }

	@Test
    public void testOpenRange2() {
        SequenceSet s = new SequenceSet();
        s.add(10,1);
        s.add(8);
        
        Assert.assertEquals("1:10", s.toString());
        ArrayAssert.assertEquals(new int[] {1,2,3,4,5,6,7,8,9,10}, s.toArray(13));
    }

	@Test
    public void testOpenRange3() {
        SequenceSet s = new SequenceSet();
        s.add(1,10);
        s.add(8,10);
        
        Assert.assertEquals("1:10", s.toString());
        ArrayAssert.assertEquals(new int[] {1,2,3,4,5,6,7,8,9,10}, s.toArray(13));
        
    }

	@Test
    public void testOpenRange4() {
        SequenceSet s = new SequenceSet();
        s.add(10, SequenceEntry.STAR);
        s.add(8,9);
        
        Assert.assertEquals("8:*", s.toString());
        ArrayAssert.assertEquals(new int[] {8,9,10,11,12,13}, s.toArray(13));
    }
    
	@Test
    public void testOpenRange5() {
        SequenceSet s = new SequenceSet();
        s.add(SequenceEntry.STAR,10);
        s.add(8);
        
        Assert.assertEquals("8,10:*", s.toString());
        ArrayAssert.assertEquals(new int[] {8,10,11,12,13}, s.toArray(13));
    }

	@Test
    public void testRange1() {
        SequenceSet s = new SequenceSet();
        s.add(10,9);
        s.add(5,8);
        
        Assert.assertEquals("5:10", s.toString());
        ArrayAssert.assertEquals(new int[] {5,6,7,8,9,10}, s.toArray(13));
    }
    
	@Test
    public void testRange2() {
        SequenceSet s = new SequenceSet();
        s.add(5,10);
        s.add(5,10);
        
        Assert.assertEquals("5:10", s.toString());
        ArrayAssert.assertEquals(new int[] {5,6,7,8,9,10}, s.toArray(13));
    }

	@Test
    public void testRange3() {
        SequenceSet s = new SequenceSet();
        s.add(7);
        s.add(5);
        s.add(10);
        s.add(6);
        
        Assert.assertEquals("5:7,10", s.toString());
        ArrayAssert.assertEquals(new int[] {5,6,7,10}, s.toArray(13));
    }

	@Test
    public void testRange4() {
        SequenceSet s = new SequenceSet();
        s.add(7);
        s.add(5,10);
        s.add(10);
        s.add(6);
        
        Assert.assertEquals("5:10", s.toString());
        ArrayAssert.assertEquals(new int[] {5,6,7,8,9,10}, s.toArray(13));
    }
    
	@Test
    public void testRange5() {
        SequenceSet s = new SequenceSet();
        s.add(3,10);
        s.add(10);
        s.add(2,20);
        s.add(5,100);
        
        Assert.assertEquals("2:100", s.toString());
    }


	@Test
    public void testSingle1() {
        SequenceSet s = new SequenceSet();
        s.add(1);
        s.add(SequenceEntry.STAR);
        
        Assert.assertEquals("1,*", s.toString());
        ArrayAssert.assertEquals(new int[] {1,13}, s.toArray(13));        
    }
    
	@Test
    public void testSingle2() {
        SequenceSet s = new SequenceSet();
        s.add(1);
        s.add(2,5);
        s.add(6);
        
        Assert.assertEquals("1:6", s.toString());
        ArrayAssert.assertEquals(new int[] {1,2,3,4,5,6}, s.toArray(13));        
    }
    
	@Test
    public void testConstructor() {
    	int[] test = new int[] {1,2,3,4,5,6,7,8,9,10};
    	SequenceSet s = new SequenceSet(test,0,4);
    	Assert.assertEquals("1:4", s.toString());
        
        s = new SequenceSet(test,4,4);
        Assert.assertEquals("5:8", s.toString());

        s = new SequenceSet(test,8,2);
        Assert.assertEquals("9:10", s.toString());   
    }
}
