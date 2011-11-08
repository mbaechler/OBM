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
import org.columba.ristretto.message.MailboxInfo;
import org.junit.Assert;
import org.junit.Test;

public class MailboxInfoParserTest{
    
	@Test
    public void testInfo1() throws Exception {
        IMAPResponse[] responses = new IMAPResponse[] {
                IMAPResponseParser.parse( "* 172 EXISTS\r\n"),
                IMAPResponseParser.parse( "* 1 RECENT\r\n"),
                IMAPResponseParser.parse( "* OK [UNSEEN 12] Message 12 is first unseen\r\n"),
                IMAPResponseParser.parse( "* OK [UIDVALIDITY 857529045] UIDs valid\r\n"),
                IMAPResponseParser.parse( "* OK [UIDNEXT 4392] Predicted next UID\r\n"),
                IMAPResponseParser.parse( "* FLAGS (\\Answered \\Flagged \\Deleted \\Seen \\Draft)\r\n"),
                IMAPResponseParser.parse( "* OK [PERMANENTFLAGS (\\Deleted \\Seen \\*)] Limited\r\n"),
                IMAPResponseParser.parse( "A142 OK [READ-WRITE] SELECT completed\r\n")                
        };
        
        MailboxInfo mailboxInfo = null;
        
        for( int i=0; i<responses.length; i++ ) {
        	mailboxInfo = MailboxInfoParser.parse( responses[i], mailboxInfo );
        }
        
        Assert.assertEquals( mailboxInfo.getRecent(), 1);
        Assert.assertEquals( mailboxInfo.getExists(), 172);
        Assert.assertEquals( mailboxInfo.getFirstUnseen(), 12);
        Assert.assertEquals( mailboxInfo.getUidValidity(), 857529045);
        Assert.assertEquals( mailboxInfo.getFirstUnseen(), 12);
        Assert.assertTrue( mailboxInfo.isWriteAccess());
        
        String[] flags = new String[] { "\\Answered", "\\Flagged", "\\Deleted", "\\Seen", "\\Draft" };
        for( int i=0; i<flags.length; i++) {
        	Assert.assertEquals( flags[i], mailboxInfo.getDefinedFlags()[i]);
        }
    }
    
	@Test
    public void testInfo2() throws Exception {
        IMAPResponse[] responses = new IMAPResponse[] {
                IMAPResponseParser.parse( "* 172 EXISTS\r\n"),
                IMAPResponseParser.parse( "* 1 RECENT\r\n"),
                IMAPResponseParser.parse( "* OK [UNSEEN 12] Message 12 is first unseen\r\n"),
                IMAPResponseParser.parse( "* OK [UIDNEXT 4392] Predicted next UID\r\n"),
                IMAPResponseParser.parse( "A142 OK [READ-ONLY] SELECT completed\r\n")                
        };
        
        MailboxInfo mailboxInfo = null;
        
        for( int i=0; i<responses.length; i++ ) {
        	mailboxInfo = MailboxInfoParser.parse( responses[i], mailboxInfo );
        }
        
        Assert.assertEquals( mailboxInfo.getRecent(), 1);
        Assert.assertEquals( mailboxInfo.getExists(), 172);
        Assert.assertEquals( mailboxInfo.getFirstUnseen(), 12);
        Assert.assertEquals( mailboxInfo.getUidValidity(), -1);
        Assert.assertTrue( !mailboxInfo.isWriteAccess());
        
    }

	@Test
    public void testInfo3() throws Exception {
        IMAPResponse[] responses = new IMAPResponse[] {
                IMAPResponseParser.parse( "* 217 EXISTS\r\n"),
                IMAPResponseParser.parse( "* 0 RECENT\r\n"),
                IMAPResponseParser.parse( "* OK [UNSEEN 217] Message 217 is first unseen\r\n"),
                IMAPResponseParser.parse( "* OK [UIDVALIDITY 396150377]\r\n"),
                IMAPResponseParser.parse( "* FLAGS (\\Answered \\Flagged \\Draft \\Deleted \\Seen)\r\n"),
                IMAPResponseParser.parse( "* OK [PERMANENTFLAGS (\\Answered \\Flagged \\Draft \\Deleted \\Seen)]\r\n"),
                IMAPResponseParser.parse( "3 OK [READ_WRITE] completed\r\n")                
        };
        
        MailboxInfo mailboxInfo = null;
        
        for( int i=0; i<responses.length; i++ ) {
        	mailboxInfo = MailboxInfoParser.parse( responses[i], mailboxInfo );
        }
        
        Assert.assertEquals( mailboxInfo.getRecent(), 0);
        Assert.assertEquals( mailboxInfo.getExists(), 217);
        Assert.assertEquals( mailboxInfo.getFirstUnseen(), 217);
        Assert.assertEquals( mailboxInfo.getUidValidity(), 396150377);
        Assert.assertTrue( mailboxInfo.isWriteAccess());
        
    }

}
