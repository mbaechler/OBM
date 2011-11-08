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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.columba.ristretto.io.ByteBufferSource;
import org.columba.ristretto.io.Source;
import org.junit.Assert;
import org.junit.Test;

public class ByteArraySourceTest {

	@Test
	public void testFromActualPosition1() {
		try {
			Source source = new ByteBufferSource("This is a test".getBytes("ISO-8859-1"));
			source.seek(10);
			Source subsource = source.fromActualPosition();
			Assert.assertTrue(subsource.next() == 't');
			Assert.assertTrue(subsource.next() == 'e');
			Assert.assertTrue(subsource.next() == 's');
			Assert.assertTrue(subsource.next() == 't');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFromActualPosition2() {
		try {
			Source source = new ByteBufferSource("This is a test".getBytes("ISO-8859-1"));
			source.seek(5);
			Source subsource = source.fromActualPosition();
			Assert.assertTrue(subsource.toString().equals("is a test"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFromActualPosition3() {
		try {
			Source source = new ByteBufferSource("This is a test".getBytes("ISO-8859-1"));
			source.seek(5);
			Source subsource = source.fromActualPosition();
			subsource.seek(5);
			Source subsubsource = subsource.fromActualPosition();
			Assert.assertTrue(subsubsource.toString().equals("test"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNext() {
		try {
			Source source = new ByteBufferSource("test".getBytes("ISO-8859-1"));
			Assert.assertTrue(source.next() == 't');
			Assert.assertTrue(source.next() == 'e');
			Assert.assertTrue(source.next() == 's');
			Assert.assertTrue(source.next() == 't');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRegexp() throws Exception {
		Source test = new ByteBufferSource("This is a test\r\n--boundary\r\n".getBytes("ISO-8859-1"));

		Pattern pattern = Pattern.compile("\\r\\n--boundary\\r\\n");
		Matcher matcher = pattern.matcher(test);
		Assert.assertTrue(matcher.find());
	}

	@Test
	public void testSeek() {
		try {
			Source source = new ByteBufferSource("This is a test".getBytes("ISO-8859-1"));
			source.seek(10);
			Assert.assertTrue(source.next() == 't');
			Assert.assertTrue(source.next() == 'e');
			Assert.assertTrue(source.next() == 's');
			Assert.assertTrue(source.next() == 't');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSubSequence1() {
		try {
			Source source = new ByteBufferSource("This is a test".getBytes("ISO-8859-1"));
			Source subsource = (Source) source.subSequence(10, 13);
			Assert.assertTrue(subsource.next() == 't');
			Assert.assertTrue(subsource.next() == 'e');
			Assert.assertTrue(subsource.next() == 's');
			Assert.assertTrue(subsource.next() == 't');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSubSequence2() throws Exception {
		Source source = new ByteBufferSource("This is a test".getBytes("ISO-8859-1"));
		Source subsource = (Source) source.subSequence(5, 9);
		Assert.assertTrue(subsource.toString().equals("is a"));
	}

	@Test
	public void testSubSequence3() throws Exception {
		Source source = new ByteBufferSource("This is a test".getBytes("ISO-8859-1"));
		Source subsource = (Source) source.subSequence(5, 9);
		Source subsubsource = (Source) subsource.subSequence(0, 2);
		Assert.assertTrue(subsubsource.toString().equals("is"));
	}

}
