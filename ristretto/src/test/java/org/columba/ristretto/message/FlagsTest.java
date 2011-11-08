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
package org.columba.ristretto.message;

import org.junit.Assert;
import org.junit.Test;

public class FlagsTest{

	@Test
    public void testGet() {
        Flags testFlags = new Flags();
        Assert.assertFalse(testFlags.get(Flags.ANSWERED));
        Assert.assertFalse(testFlags.get(Flags.SEEN));
        Assert.assertFalse(testFlags.get(Flags.FLAGGED));
    }

	@Test
    public void testSet() {
        Flags testFlags = new Flags();
        testFlags.set(Flags.ANSWERED);
        Assert.assertTrue("The flag wasnt set correctly.", testFlags.get(Flags.ANSWERED));
    }

	@Test
    public void testClear() {
        Flags testFlags = new Flags();
        testFlags.set(Flags.ANSWERED);
        testFlags.clear(Flags.ANSWERED);
        Assert.assertFalse(testFlags.get(Flags.ANSWERED));
    }

	@Test
    public void testToggle() {
        Flags testFlags = new Flags();
        testFlags.set(Flags.ANSWERED);
        testFlags.toggle(Flags.ANSWERED);
        Assert.assertFalse(testFlags.get(Flags.ANSWERED));
    }

    /**
     * Test the equals() method.
     */
	@Test
    public void testEquals() {
        Flags expected = new Flags();
        expected.setAnswered(true);
        expected.setDraft(false);
        expected.setFlagged(true);

        Flags actual = new Flags();
        actual.setAnswered(true);
        actual.setDraft(false);
        actual.setFlagged(true);
        Assert.assertTrue("The equals() method returned false, when the objects were equal", actual.equals(expected));
        Assert.assertTrue("The equals() method returned false, when the objects were equal", expected.equals(actual));
        Assert.assertFalse("The objects are equal though one is null", expected.equals(null));

        actual = new Flags();
        actual.setAnswered(true);
        actual.setDraft(true);
        actual.setFlagged(true);
        Assert.assertFalse("The equals() method returned true, when the objects were unequal", actual.equals(expected));
        Assert.assertFalse("The equals() method returned true, when the objects were unequal", expected.equals(actual));
        Assert.assertFalse("The equals() method returned true, when the objects were of different types", expected.equals(new Integer(4)));
    }

    /**
     * Test the hashcode() method.
     */
	@Test
    public void testHashCode() {
        Flags expected = new Flags();
        expected.setAnswered(true);
        expected.setDraft(false);
        expected.setFlagged(true);

        Flags actual = new Flags();
        actual.setAnswered(true);
        actual.setDraft(false);
        actual.setFlagged(true);
        Assert.assertEquals("The hashCode() returned differnt value for equal objects.", expected.hashCode(), actual.hashCode());

        actual = new Flags();
        actual.setAnswered(true);
        actual.setDraft(true);
        actual.setFlagged(true);
        Assert.assertTrue("The hashCode() returned same value for unequal objects", actual.hashCode() != expected.hashCode());
    }

    /**
     * Test the clone() method.
     *
     */
	@Test
    public void testClone() {

        Flags expected = new Flags();
        expected.setAnswered(true);
        expected.setDraft(false);
        expected.setFlagged(true);

        Flags actual = (Flags) expected.clone();
        Assert.assertNotSame("The object is the same object after a clone", expected, actual);
        Assert.assertEquals("The objects are not equal", expected, actual);
        Assert.assertEquals("The object's flags are not equal", expected.getFlags(), actual.getFlags());
    }
}
