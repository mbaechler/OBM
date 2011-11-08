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
package org.columba.ristretto.imap.parser;

import org.columba.ristretto.imap.IMAPResponse;
import org.columba.ristretto.message.MimePart;
import org.columba.ristretto.message.MimeTree;
import org.columba.ristretto.parser.ParserException;
import org.junit.Assert;
import org.junit.Test;

public class MimeTreeParserTest {
	
	@Test
	public void testNested() throws ParserException {
		String input = "* 1468 FETCH (UID 3159 BODYSTRUCTURE ((\"TEXT\" \"PLAIN\" (\"CHARSET\" \"UTF-8\") NIL NIL \"7BIT\" 503 9 NIL NIL NIL)(\"MESSAGE\" \"DELIVERY-STATUS\" NIL NIL NIL \"7BIT\" 186 NIL NIL NIL)(\"MESSAGE\" \"RFC822\" NIL NIL NIL \"7BIT\" 2690 (\"Sun, 13 Jun 2004 23:10:42 +0200\" \"Re[2]: [Columba-users] Columba RC1-test3 released\" ((\"Peter Karp\" NIL \"peter\" \"karpfenteich.net\")) ((NIL NIL \"columba-users-admin\" \"lists.sourceforge.net\")) ((\"Peter Karp\" NIL \"peter\" \"karpfenteich.net\")) ((\"Frederik Dietz\" NIL \"columba\" \"frederikdietz.de\")(NIL NIL \"columba-users\" \"lists.sourceforge.net\")) NIL NIL \"<dvfgqdpr.yvv1yprb24w8@frd.playboy.wg>\" \"<PM-EY.20040613231042.97DA0.3.1D@mail.karpfenteich.net>\") (\"TEXT\" \"PLAIN\" (\"CHARSET\" \"US-ASCII\") NIL NIL \"7BIT\" 37 1 NIL NIL NIL) 51 NIL NIL NIL) \"REPORT\" (\"REPORT-TYPE\" \"delivery-status\" \"BOUNDARY\" \"1087220410-439-HOME.SE\") NIL NIL))\r\n";
		IMAPResponse response = IMAPResponseParser.parse(input);
		
		MimeTree tree = MimeTreeParser.parse(response);
		MimePart mp = tree.getFirstTextPart("plain");
		Assert.assertEquals("UTF-8", mp.getHeader().getContentParameter("charset"));
	}
	
	@Test
	public void testVeryLong() throws ParserException {
		String input = "* 1 FETCH (UID 17 BODYSTRUCTURE ((\"TEXT\" \"HTML\" (\"CHARSET\" \"us-ascii\") NIL NIL \"7BIT\" 628 12 NIL NIL NIL)(\"MESSAGE\" \"RFC822\" (\"NAME\" \"columba Fanpost :)\") NIL NIL \"8BIT\" 1457 (\"Sun, 16 Nov 2003 12:42:38 +0100 (CET)\" \"columba Fanpost :)\" ((\"=?iso-8859-1?q?Becker=20Anja?=\" NIL \"peter_tosh\" \"yahoo.com\")) ((\"=?iso-8859-1?q?Tosh=20Peter?=\" NIL \"peter_tosh\" \"yahoo.com\")) ((\"=?iso-8859-1?q?Tosh=20Peter?=\" NIL \"peter_tosh\" \"yahoo.com\")) ((NIL NIL \"kost\" \"verachter.de\")) NIL NIL NIL \"<20031116114238.14814.qmail@web12308.mail.yahoo.com>\") (\"TEXT\" \"PLAIN\" (\"CHARSET\" \"iso-8859-1\") NIL NIL \"8BIT\" 801 29 NIL NIL NIL) 42 NIL (\"INLINE\" (\"FILENAME\" \"columba Fanpost :)\")) NIL) \"MIXED\" (\"BOUNDARY\" \"------------040505080406030107080404\") NIL NIL))\r\n";
		IMAPResponse response = IMAPResponseParser.parse(input);
		
		MimeTree tree = MimeTreeParser.parse(response);
		MimePart mp = tree.getFirstTextPart("html");
		Assert.assertEquals("us-ascii", mp.getHeader().getContentParameter("charset"));
	}

	@Test
	public void testDBug1() throws ParserException {
		String input = "* 1 FETCH (UID 17 BODYSTRUCTURE (((\"text\" \"plain\" (\"charset\" \"windows-1252\") NIL NIL \"quoted-printable\" 812 12 NIL NIL NIL)(\"text\" \"html\" (\"charset\" \"windows-1252\") NIL NIL \"quoted-printable\" 1416 26 NIL NIL NIL) \"alternative\" (\"boundary\" \"----=_NextPart_001_0010_01C7539D.DACFC990\") NIL NIL)(\"image\" \"gif\" (\"name\" \"qjzea.gif\") \"<006901c7538d$1746f990$6c822ecf@WM \\\"(S65>\\\">\" NIL \"base64\" 11974 NIL NIL NIL) \"related\" (\"type\" \"multipart/alternative\" \"boundary\" \"----=_NextPart_000_000F_01C7539D.DACFC990\") NIL NIL))\r\n";
		IMAPResponse response = IMAPResponseParser.parse(input);
		
		MimeTree structure = MimeTreeParser.parse(response);
		structure.count();
	}

	
}
