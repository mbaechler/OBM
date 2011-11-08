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
package org.columba.ristretto.log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LogInputStreamTest {
    
    private byte[] buffer;
    
    @Test
    public void test1() throws IOException {
        byte[] result = "S: This is a simple line".getBytes();
        byte[] test = "This is a simple line".getBytes();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = new LogInputStream( new ByteArrayInputStream( test ), out);
        
        in.read(buffer);
        
        System.out.println( new String( out.toByteArray()));
        
        Assert.assertTrue( Arrays.equals( result, out.toByteArray()));
    }

    @Test
    public void test2() throws IOException {
        byte[] result = "S: This is a multiple\nS: line".getBytes();
        byte[] test = "This is a multiple\nline".getBytes();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = new LogInputStream( new ByteArrayInputStream( test ), out);
        
        in.read(buffer);
        
        Assert.assertTrue( Arrays.equals( result, out.toByteArray()));
    }

    @Test
    public void test3() throws IOException {
        byte[] result = "S: This is a multiple\nS: line\n".getBytes();
        byte[] test = "This is a multiple\nline\n".getBytes();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = new LogInputStream( new ByteArrayInputStream( test ), out);
        
        in.read(buffer);
        
        Assert.assertTrue( Arrays.equals( result, out.toByteArray()));
    }
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        buffer = new byte[1000];
    }

}
