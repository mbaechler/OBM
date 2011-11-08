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
import org.columba.ristretto.imap.ListInfo;
import org.columba.ristretto.io.CharSequenceSource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author frd
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

public class ListInfoParserTest {

	@Test
	public void testParse1() throws Exception {
		IMAPResponse r = IMAPResponseParser.parse("* LIST (\\NoSelect) \"/\" \"\"\r\n");

		ListInfo listInfo = ListInfoParser.parse(r);
		
		Assert.assertTrue(listInfo.getParameter(ListInfo.NOSELECT));
		Assert.assertTrue(listInfo.getDelimiter().equals("/"));
		Assert.assertTrue(listInfo.getName().equals(""));
	}

	@Test
	public void testParse2() throws Exception {
		IMAPResponse r = IMAPResponseParser.parse("* LSUB () \".\" testbox\r\n");

		ListInfo listInfo = ListInfoParser.parse(r);
		
		Assert.assertTrue(!listInfo.getParameter(ListInfo.NOSELECT));
		Assert.assertTrue(listInfo.getDelimiter().equals(".") );
		Assert.assertTrue(listInfo.getName().equals("testbox"));
	}

	@Test
	public void testParse3() throws Exception {
		IMAPResponse r = IMAPResponseParser.parse("* LIST () \"/\" {0}\r\n");
		r.addLiteral( new CharSequenceSource("testbox/\r") );
		
		ListInfo listInfo = ListInfoParser.parse(r);
		
		Assert.assertTrue(!listInfo.getParameter(ListInfo.NOSELECT));
		Assert.assertTrue(listInfo.getDelimiter().equals("/") );
		Assert.assertTrue(listInfo.getName().equals("testbox/\r"));
	}

	@Test
	public void testParse4() throws Exception {
		IMAPResponse r = IMAPResponseParser.parse("* LSUB (\\UnMarked) \"/\" Trash\r\n");

		ListInfo listInfo = ListInfoParser.parse(r);
		
		Assert.assertTrue(listInfo.getParameter(ListInfo.UNMARKED));
		Assert.assertTrue(listInfo.getDelimiter().equals("/") );
		Assert.assertTrue(listInfo.getName().equals("Trash"));
	}
	
	@Test
	public void testParse5() throws Exception {
		IMAPResponse r = IMAPResponseParser.parse("* LSUB () \"\\\\\" Trash\r\n");

		ListInfo listInfo = ListInfoParser.parse(r);
		
		Assert.assertEquals("\\\\", listInfo.getDelimiter() );
		Assert.assertTrue(listInfo.getName().equals("Trash"));
	}
	
	@Test
	public void testParseHasNoChildren() throws Exception {
		IMAPResponse r = IMAPResponseParser.parse("* LIST (\\HasNoChildren) \"/\" Journal\r\n");

		ListInfo listInfo = ListInfoParser.parse(r);
		
		Assert.assertEquals("/", listInfo.getDelimiter() );
		Assert.assertEquals("Journal", listInfo.getName());
		Assert.assertTrue(listInfo.getParameter(ListInfo.HASNOCHILDREN));
	}

	@Test
	public void testParseHasChildren() throws Exception {
		IMAPResponse r = IMAPResponseParser.parse("* LIST (\\HasChildren) \"/\" INBOX\r\n");

		ListInfo listInfo = ListInfoParser.parse(r);
		
		Assert.assertEquals("/", listInfo.getDelimiter() );
		Assert.assertEquals("INBOX", listInfo.getName());
		Assert.assertTrue(listInfo.getParameter(ListInfo.HASCHILDREN));
	}
}
