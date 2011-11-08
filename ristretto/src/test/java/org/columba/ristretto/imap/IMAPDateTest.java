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

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class IMAPDateTest {
    
	@Test
    public void test1() {
        TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");
        Calendar calendar = Calendar.getInstance(tz);
        calendar.set(2004,2,8,18,32,30);
        
        IMAPDateTime date = new IMAPDateTime( calendar.getTime(), tz );
        Assert.assertEquals("\"08-Mar-2004 18:32:30 +0100\"", date.toString());
        
    }
    
	@Test
    public void test2() {
        TimeZone tz = TimeZone.getTimeZone("Europe/London");
        Calendar calendar = Calendar.getInstance(tz);
        calendar.set(2004,2,16,8,2,3);
        
        IMAPDateTime date = new IMAPDateTime( calendar.getTime(), tz );
        Assert.assertEquals("\"16-Mar-2004 08:02:03 +0000\"", date.toString());
        
    }

	@Test
    public void test3() {
        TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
        Calendar calendar = Calendar.getInstance(tz);
        calendar.set(2004,2,16,8,12,3);
        
        IMAPDateTime date = new IMAPDateTime( calendar.getTime(), tz );
        Assert.assertEquals("\"16-Mar-2004 08:12:03 -0800\"", date.toString());
        
    }
}
