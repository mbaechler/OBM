/* ***** BEGIN LICENSE BLOCK *****
 *
 * %%
 * Copyright (C) 2000 - 2011 Linagora
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK ***** */
package org.minig.imap.command;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.IMAPResponseParser;
import org.minig.imap.impl.MinaIMAPMessage;

import com.google.common.collect.ImmutableList;


public class UIDFetchEnvelopeCommandTest {

	@Test
	public void testMoreThanOneLine() {
		String firstLine = 
			"* 20 FETCH (UID 20 ENVELOPE (\"Tue, 28 Apr 2009 17:10:03 +0200\" {43}";
		String continuation =
			"schema pour formation, \"redirec after post\" " +
			"((\"Olivier Boyer\" NIL \"olivier.boyer\" \"linagora.com\")) " +
			"((\"Olivier Boyer\" NIL \"olivier.boyer\" \"linagora.com\")) " +
			"((\"Olivier Boyer\" NIL \"olivier.boyer\" \"linagora.com\")) " +
			"((\"Patrick PAYSANT\" NIL \"patrick.paysant\" \"aliacom.fr\")(\"=?UTF-8?B?UmFwaGHDq2wgUg==?= =?UTF-8?B?b3VnZXJvbg==?=\" NIL \"raphael.rougeron\" \"aliasource.fr\")) NIL NIL NIL \"<49F71C4B.3040205@linagora.com>\"))";
		MinaIMAPMessage minaMsg = new MinaIMAPMessage(firstLine);
		minaMsg.addLine(continuation.getBytes());
		IMAPResponseParser imapResponseParser = new IMAPResponseParser();
		IMAPResponse response = imapResponseParser.parse(minaMsg);
		UIDFetchEnvelopeCommand command = new UIDFetchEnvelopeCommand(Arrays.asList(20l));
		command.responseReceived(ImmutableList.of(response, new IMAPResponse("OK", "")));
		Assert.assertNotNull(command.getReceivedData());
	}
}
