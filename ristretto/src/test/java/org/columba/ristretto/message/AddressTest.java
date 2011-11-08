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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;
/**
 * @author redsolo
 */
public class AddressTest {

    /**
     * Test the get() methods.
     */
	@Test
    public void testGet() {
        Address addr = new Address("Emil", "email@lonneberga.se");
        Assert.assertEquals("The display name isnt correct", "Emil", addr.getDisplayName());
        Assert.assertEquals("The address isnt correct", "email@lonneberga.se", addr.getMailAddress());
        Assert.assertEquals("The short name isnt correct", "Emil", addr.getShortAddress());
        Assert.assertEquals("The cannonical mail isnt correct", "<email@lonneberga.se>", addr.getCanonicalMailAddress());
    }

    /**
     * Test the set() methods.
     */
	@Test
    public void testSet() {
        Address addr = new Address("not@this.on");
        Assert.assertNotNull("The display wasnt null", addr.getDisplayName());
        addr.setDisplayName("a NAME");
        Assert.assertEquals("The set display name failed", "a NAME", addr.getDisplayName());
    }

    /**
     * Test that the Serializable implementation worked.
     * @throws IOException thrown by the stream handling.
     * @throws ClassNotFoundException thrown by the readObject() method.
     */
	@Test
    public void testSerializing() throws IOException, ClassNotFoundException {
        Address expected = new Address("Emil", "email@lonneberga.se");

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(byteOutput);

        output.writeObject(expected);
        Assert.assertTrue("The stream is empty after writing", byteOutput.size() > 0);

        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(byteOutput.toByteArray()));
        Address actual = (Address) input.readObject();
        Assert.assertEquals("The serializing didnt work", expected, actual);
    }

    /**
     * Test that the Serializable implementation worked.
     * @throws IOException thrown by the stream handling.
     * @throws ClassNotFoundException thrown by the readObject() method.
     */
	@Test
    public void testSerializing2() throws IOException, ClassNotFoundException {
        Address expected = new Address("email@lonneberga.se");
        expected.setDisplayName(null);

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(byteOutput);

        output.writeObject(expected);
        Assert.assertTrue("The stream is empty after writing", byteOutput.size() > 0);

        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(byteOutput.toByteArray()));
        Address actual = (Address) input.readObject();
        Assert.assertEquals("The serializing didnt work", expected, actual);
    }

    /**
     * Test the equals() and hashCode() methods.
     */
	@Test
    public void testEquals() {
        Address addr1 = new Address("Emil", "email@lonneberga.se");
        Address addr2 = new Address("Emil", "email@lonneberga.se");
        Address addr3 = new Address("Emil", "email@lonneberga.de");
        Assert.assertTrue("The objects arent equal", addr1.equals(addr2));
        Assert.assertTrue("The objects arent equal", addr2.equals(addr1));
        Assert.assertFalse("The objects are equal though they are different types", addr2.equals(new Integer(3)));
        Assert.assertFalse("The objects are equal though one is null", addr2.equals(null));
        Assert.assertFalse("The objects are equal", addr1.equals(addr3));
        Assert.assertFalse("The objects are equal", addr3.equals(addr1));
        Assert.assertEquals("Equal objects has different hashcodes", addr1.hashCode(), addr2.hashCode());
        Assert.assertFalse("Unequal objects has same hashcodes", addr1.hashCode() == addr3.hashCode());
    }

    /**
     * Tests the compareTo method.
     */
	@Test
    public void testCompareTo() {
        Address addr1 = new Address("Emil", "email@lonneberga.se");
        Address addr2 = new Address("Emil", "email@lonneberga.se");
        Assert.assertTrue("The compareTo method failed", addr1.compareTo(addr2) == 0);
        Assert.assertTrue("The compareTo method failed", addr1.compareTo(new Address("bah@bahcity.net")) > 0);
    }
}
