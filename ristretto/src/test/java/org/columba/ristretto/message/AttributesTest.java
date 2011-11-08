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
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for the Attributes class.
 *
 * @author redsolo
 */
public class AttributesTest {

    /**
     * Tests the get() and put methods.
     */
	@Test
    public void testPutAndGet() {
        Attributes attrs = new Attributes();
        Object value1 = new Integer(13);
        attrs.put("key-1", value1);
        Object value2 = Boolean.FALSE;
        attrs.put("key-2", value2);
        attrs.put("key-4", value1);
        Assert.assertSame("Value for the first key was not correct", value1, attrs.get("key-1"));
        Assert.assertSame("Value for the second key was not correct", value2, attrs.get("key-2"));
        Assert.assertSame("Value for the third key was not correct", value1, attrs.get("key-4"));
    }

    /**
     * Tests the load() method, ie. actually tests the constructor.
     * @throws IOException thrown by the write() method in the object output stream.
     */
	@Test
    public void testLoad() throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(byteOutputStream);
        output.writeInt(3);
        output.writeUTF("float-1");
        output.writeObject(new Float(3.5f));
        output.writeUTF("int-1");
        output.writeObject(new Integer(1));
        output.writeUTF("float-2");
        output.writeObject(new Float(999.9f));
        output.close();

        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(byteOutputStream.toByteArray()));
        Attributes attrs = new Attributes(input);

        Assert.assertEquals("The size was not correct after loading", 3, attrs.count());
        Assert.assertEquals("Float key 1's value is not correct", new Float(3.5f), attrs.get("float-1"));
        Assert.assertEquals("Float key 2's value is not correct", new Float(999.9f), attrs.get("float-2"));
        Assert.assertEquals("Integer key 1's value is not correct", new Integer(1), attrs.get("int-1"));
    }

    /**
     * Tests the save() method.
     * @throws IOException thrown by the object input stream.
     * @throws ClassNotFoundException thrown by the object input stream.
     */
	@Test
    public void testSave() throws IOException, ClassNotFoundException {
        Map expected = new HashMap();
        expected.put("put-str-1", "hejhopp");
        expected.put("put-int-1", new Integer(-1));
        expected.put("put-str-2", "ristrettos");
        expected.put("put-float-1", new Float(3.5f));

        Attributes attr = new Attributes();
        attr.put("put-str-1", "hejhopp");
        attr.put("put-int-1", new Integer(-1));
        attr.put("put-str-2", "ristrettos");
        attr.put("put-float-1", new Float(3.5f));

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        attr.save(new ObjectOutputStream(byteOutputStream));

        ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(byteOutputStream.toByteArray()));
        Assert.assertEquals("Number of written objects wasnt correct", 4, input.readInt());
        String key = input.readUTF();
        Assert.assertEquals("Value for key '" + key + "' wasnt correct", expected.get(key), input.readObject());
        key = input.readUTF();
        Assert.assertEquals("Value for key '" + key + "' wasnt correct", expected.get(key), input.readObject());
        key = input.readUTF();
        Assert.assertEquals("Value for key '" + key + "' wasnt correct", expected.get(key), input.readObject());
        key = input.readUTF();
        Assert.assertEquals("Value for key '" + key + "' wasnt correct", expected.get(key), input.readObject());
        Assert.assertEquals("The stream was not empty after reading all four objects", 0, input.available());
    }

    /**
     * Tests the count() method.
     */
	@Test
    public void testCount() {
        Attributes attr = new Attributes();
        attr.put("put-str-1", "hejhopp");
        attr.put("put-int-1", new Integer(-1));
        attr.put("put-str-2", "ristrettos");
        attr.put("put-float-1", new Float(3.5f));
        Assert.assertEquals("The count() method didnt return the correct size", 4, attr.count());
    }

    /**
     * Tests the clone() method.
     */
	@Test
    public void testClone() {
        Attributes attr = new Attributes();
        attr.put("key", "value");
        attr.put("key2", "value2");
        attr.put("key3", "value5");
        Attributes clone = (Attributes) attr.clone();
        Assert.assertNotSame("The object is the same object after a clone", attr, clone);
        Assert.assertEquals("Attributes sizes are not the same", attr.count(), clone.count());
        Assert.assertSame("The key pairs are not the same", attr.get("key"), clone.get("key"));
        Assert.assertSame("The key pairs are not the same", attr.get("key2"), clone.get("key2"));
        Assert.assertSame("The key pairs are not the same", attr.get("key3"), clone.get("key3"));
    }

    /**
     * Test the equals() method.
     */
	@Test
    public void testEquals() {
        Attributes expected = new Attributes();
        expected.put("key-e-1", "value-e-1");
        expected.put("key-e-4", "value-e-4");
        expected.put("key-e-int", new Integer(3));

        Attributes actual = new Attributes();
        actual.put("key-e-1", "value-e-1");
        actual.put("key-e-4", "value-e-4");
        actual.put("key-e-int", new Integer(3));

        Assert.assertTrue("The equals() method returned false for equal objects", actual.equals(expected));
        Assert.assertTrue("The equals() method returned false for equal objects", expected.equals(actual));
        Assert.assertFalse("The objects are equal though one is null", expected.equals(null));

        actual.put("key-e-int", new Integer(4));
        Assert.assertFalse("The equals() method returned true for non equal objects", actual.equals(expected));
        Assert.assertFalse("The equals() method returned true for non equal objects", expected.equals(actual));
        Assert.assertFalse("The equals() method returned true, when the objects were of different types", expected.equals(new Integer(4)));
    }

    /**
     * Test the hashcode() method.
     */
	@Test
    public void testHashCode() {
        Attributes expected = new Attributes();
        expected.put("key-e-1", "value-e-1");
        expected.put("key-e-4", "value-e-4");
        expected.put("key-e-int", new Integer(3));

        Attributes actual = new Attributes();
        actual.put("key-e-1", "value-e-1");
        actual.put("key-e-4", "value-e-4");
        actual.put("key-e-int", new Integer(3));

        Assert.assertEquals("The hashcode() returned different values for equal objects", actual.hashCode(), expected.hashCode());
        Assert.assertEquals("The hashcode() returned different values for equal objects", expected.hashCode(), actual.hashCode());

        actual.put("key-e-int", new Integer(3));
        Assert.assertTrue("The hashcode() returned same values for non equal objects", actual.hashCode() == expected.hashCode());
    }
}
