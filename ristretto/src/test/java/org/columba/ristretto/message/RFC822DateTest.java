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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class RFC822DateTest {

	@Test
	public void testToString1() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
		calendar.set( 2003, 1, 25, 16, 44, 33 );
		Date testDate = calendar.getTime();
		
		String dateString = MessageDate.toString( testDate, TimeZone.getTimeZone("Europe/Berlin") );
		Assert.assertEquals( "Tue, 25 Feb 2003 16:44:33 +0100", dateString);
	}

	@Test
	public void testToString2() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
		calendar.set( 2003, 1, 24, 16, 44, 33 );
		Date testDate = calendar.getTime();
		
		String dateString = MessageDate.toString( testDate, TimeZone.getTimeZone("America/Los_Angeles") );
		Assert.assertEquals( "Mon, 24 Feb 2003 16:44:33 -0800", dateString);
	}
	
	@Test
	public void testToStringLeapYear() {
	    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
	    calendar.set( 2004, 0, 5, 16, 44, 33 );
	    Date testDate = calendar.getTime();
	    
	    String dateString = MessageDate.toString( testDate, TimeZone.getTimeZone("America/Los_Angeles") );
	    Assert.assertTrue( dateString.equals("Mon, 5 Jan 2004 16:44:33 -0800"));
	}
	
	@Test
	public void testToStringLeapYear2() {
	    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
	    calendar.set( 2004, 2, 1, 16, 44, 33 );
	    Date testDate = calendar.getTime();
	    
	    String dateString = MessageDate.toString( testDate, TimeZone.getTimeZone("America/Los_Angeles") );
	    Assert.assertTrue( dateString.equals("Mon, 1 Mar 2004 16:44:33 -0800"));
	}
	
	@Test
	public void testToStringLeapYear3() {
	    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
	    calendar.set( 2004, 1, 29, 16, 44, 33 );
	    Date testDate = calendar.getTime();
	    
	    String dateString = MessageDate.toString( testDate, TimeZone.getTimeZone("America/Los_Angeles") );
	    Assert.assertTrue( dateString.equals("Sun, 29 Feb 2004 16:44:33 -0800"));
	}
	
	@Test
	public void testToString3() throws Exception {
		String testStr = "Thu, 26 Jun 2003 17:33:14 +0200";
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", new Locale("EN"));
		Date testDate = formatter.parse(testStr);
		String dateString = MessageDate.toString( testDate, TimeZone.getTimeZone("GMT+0200") );
		Assert.assertTrue( dateString.equals(testStr));
	}
}
