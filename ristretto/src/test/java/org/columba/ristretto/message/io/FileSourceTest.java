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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.columba.ristretto.io.FileSource;
import org.columba.ristretto.io.Source;
import org.junit.Assert;
import org.junit.Test;

public class FileSourceTest {

    private static final String TEST_FILENAME_STR = "message/io/FileSourceTest.eml";

    protected URI getURIResource(String resource) throws URISyntaxException {
    	URL url = getClass().getClassLoader().getResource(resource);
    	return url.toURI();
    }
    
    @Test
    public void testLength() throws IOException, URISyntaxException {
    	File file = new File(getURIResource(TEST_FILENAME_STR));
        FileSource source = new FileSource(file);
        Assert.assertEquals( file.length(), source.length() );
    }
    
    @Test
    public void testFromActualPosition1() throws IOException, URISyntaxException {
    	File file = new File(getURIResource(TEST_FILENAME_STR));
        FileSource source = new FileSource(file);
        source.seek(10);
        Source subsource = source.fromActualPosition();
        Assert.assertTrue(subsource.next() == 't');
        Assert.assertTrue(subsource.next() == 'e');
        Assert.assertTrue(subsource.next() == 's');
        Assert.assertTrue(subsource.next() == 't');
    }

    @Test
    public void testFromActualPosition2() throws IOException, URISyntaxException {
    	File file = new File(getURIResource(TEST_FILENAME_STR));
    	FileSource source = new FileSource(file);
        source.seek(50);
        Source subsource = source.fromActualPosition();
        Assert.assertEquals("is a test", subsource.toString());
    }

    @Test
    public void testFromActualPosition3() throws IOException, URISyntaxException {
    	File file = new File(getURIResource(TEST_FILENAME_STR));
        FileSource source = new FileSource(file);
        source.seek(50);
        Source subsource = source.fromActualPosition();
        subsource.seek(5);
        Source subsubsource = subsource.fromActualPosition();
        Assert.assertTrue(subsubsource.toString().equals("test"));
    }

    @Test
    public void testNext() throws IOException, URISyntaxException {
    	File file = new File(getURIResource(TEST_FILENAME_STR));
    	FileSource source = new FileSource(file);
        Assert.assertTrue(source.next() == 'T');
        Assert.assertTrue(source.next() == 'h');
        Assert.assertTrue(source.next() == 'i');
        Assert.assertTrue(source.next() == 's');
    }

    @Test
    public void testRegexp() throws IOException, URISyntaxException {
    	File file = new File(getURIResource(TEST_FILENAME_STR));
    	FileSource source = new FileSource(file);
        Pattern pattern = Pattern.compile("test");
        Matcher matcher = pattern.matcher(source);
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void testSeek() throws IOException, URISyntaxException {
    	File file = new File(getURIResource(TEST_FILENAME_STR));
        FileSource source = new FileSource(file);
        source.seek(10);
        Assert.assertTrue(source.next() == 't');
        Assert.assertTrue(source.next() == 'e');
        Assert.assertTrue(source.next() == 's');
        Assert.assertTrue(source.next() == 't');
    }

    @Test
    public void testSubSequence1() throws IOException, URISyntaxException {
    	File file = new File(getURIResource(TEST_FILENAME_STR));
        FileSource source = new FileSource(file);
        Source subsource = source.subSource(10, 13);
        Assert.assertTrue(subsource.next() == 't');
        Assert.assertTrue(subsource.next() == 'e');
        Assert.assertTrue(subsource.next() == 's');
        Assert.assertTrue(subsource.next() == 't');
    }

    @Test
    public void testSubSequence2() throws IOException, URISyntaxException {
    	File file = new File(getURIResource(TEST_FILENAME_STR));
        FileSource source = new FileSource(file);
        Source subsource = source.subSource(5, 9);
        Assert.assertTrue(subsource.toString().equals("is a"));
    }

    @Test
    public void testSubSequence3() throws IOException, URISyntaxException {
    	File file = new File(getURIResource(TEST_FILENAME_STR));
        FileSource source = new FileSource(file);
        Source subsource = source.subSource(5, 9);
        Source subsubsource = subsource.subSource(0, 2);
        Assert.assertTrue(subsubsource.toString().equals("is"));
    }

}
