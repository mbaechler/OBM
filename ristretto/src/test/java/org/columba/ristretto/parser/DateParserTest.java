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
package org.columba.ristretto.parser;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class DateParserTest {

	@Test
	public void testParseString1() {
//		day, month, year, hour, minute
		String testData = "07 Mar 2003 19:20";

		Date date = null;
		try {
			date = DateParser.parse(testData);
		} catch (ParserException e) {
			Assert.assertTrue(false);
		}

		GregorianCalendar c = new GregorianCalendar();
		c.clear();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		// year, month, date, hour, second
		c.set(2003, 2, 7, 19, 20, 0);
		Date testDate = c.getTime();

		Assert.assertTrue( testDate.equals(date) );
	}

	@Test
	public void testParseString7() {
//		day, month, year, hour, minute
		String testData = "Wed May 25 12:59:16 2005 +0100";

		Date date = null;
		try {
			date = DateParser.parse(testData);
		} catch (ParserException e) {
			Assert.assertTrue(false);
		}

		GregorianCalendar c = new GregorianCalendar();
		c.clear();
		c.setTimeZone(TimeZone.getTimeZone("GMT+0100"));
		// year, month, date, hour, second
		c.set(2005, 4, 25, 12, 59, 16);
		Date testDate = c.getTime();

		Assert.assertEquals( testDate, date );
	}
	
	@Test
	public void testParseString2() {
		String testStr = "Thu, 6 Feb 2003 11:05:12 +0100";
		
		Date date = null;
		try {
			date = DateParser.parse(testStr);
		} catch (ParserException e) {
			Assert.assertTrue(false);
		}
		GregorianCalendar c = new GregorianCalendar();
		c.clear();
		c.setTimeZone(TimeZone.getTimeZone("GMT+0100"));
		// year, month, date, hour, second
		c.set(2003, 1, 6, 11,5,12);
		Date testDate = c.getTime();
		
		Assert.assertTrue(testDate.equals(date));
	}
	
	@Test
	public void testParseString3() {
		String testStr = "19 Jun 2003 09:46 PDT";
		
		Date date = null;
		try {
			date = DateParser.parse(testStr);
		} catch (ParserException e) {
			Assert.assertTrue(false);
		}
		GregorianCalendar c = new GregorianCalendar();
		c.clear();
		c.setTimeZone(TimeZone.getTimeZone("PST8PDT"));
		// year, month, date, hour, second
		c.set(2003, 5, 19, 9, 46, 0);
		Date testDate = c.getTime();
		Assert.assertTrue(testDate.equals(date));
	}
	
	@Test
	public void testParseString4() {
		String testStr = "Thu, 17 Apr 03 10:06 -0400";
		Date date = null;
		try {
			date = DateParser.parse(testStr);
		} catch (ParserException e) {
			Assert.assertTrue(false);
		}

		GregorianCalendar c = new GregorianCalendar();
		c.clear();
		c.setTimeZone(TimeZone.getTimeZone("GMT-0400"));

		// year, month, date, hour, second
		c.set(2003, 3, 17, 10, 6, 0);
		Date testDate = c.getTime();

		Assert.assertTrue(testDate.equals(date));
	}

	@Test
	public void testParseString5() {
		String testStr = "Wed, 2 July 2003 18:12:33 +0000";
		
		Date date = null;
		try {
			date = DateParser.parse(testStr);
		} catch (ParserException e) {
			Assert.assertTrue(false);
		}
		GregorianCalendar c = new GregorianCalendar();
		c.clear();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		//		year, month, date, hour, second
		c.set(2003, 6, 2, 18, 12, 33);
		Date testDate = c.getTime();
		Assert.assertTrue(testDate.equals(date));
	}
	
	@Test
	public void testParseString6() {
		String testStr = "  Wed,  2\tJuly  2003 18:12:33 +0000  ";
		
		Date date = null;
		try {
			date = DateParser.parse(testStr);
		} catch (ParserException e) {
			Assert.assertTrue(false);
		}
		GregorianCalendar c = new GregorianCalendar();
		c.clear();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		//		year, month, date, hour, second
		c.set(2003, 6, 2, 18, 12, 33);
		Date testDate = c.getTime();
		Assert.assertTrue(testDate.equals(date));
	}

	@Test
	public void testParseLeapYear1() {
		String testStr = "Tue,  2 Mar 2004 18:12:33 +0000  ";
		
		Date date = null;
		try {
			date = DateParser.parse(testStr);
		} catch (ParserException e) {
			Assert.assertTrue(false);
		}
		GregorianCalendar c = new GregorianCalendar();
		c.clear();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		//		year, month, date, hour, second
		c.set(2004, 2, 2, 18, 12, 33);
		Date testDate = c.getTime();
		Assert.assertEquals(testDate, date);
	}

	@Test
	public void testParseLeapYear2() {
		String testStr = "Tue,  29 Feb 2004 18:12:33 +0000  ";
		
		Date date = null;
		try {
			date = DateParser.parse(testStr);
		} catch (ParserException e) {
			Assert.assertTrue(false);
		}
		GregorianCalendar c = new GregorianCalendar();
		c.clear();
		c.setTimeZone(TimeZone.getTimeZone("GMT"));
		//		year, month, date, hour, second
		c.set(2004, 1, 29, 18, 12, 33);
		Date testDate = c.getTime();
		Assert.assertEquals(testDate, date);
	}
}
