/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.push.minig.imap.command;

import java.util.Iterator;
import java.util.List;

import org.obm.push.minig.imap.impl.IMAPParsingTools;
import org.obm.push.minig.imap.impl.IMAPResponse;
import org.obm.push.minig.imap.mime.impl.AtomHelper;

import com.google.common.base.Strings;

public class UIDNextCommand extends Command<Long> {

	private final static String UIDNEXT_ITEM = "UIDNEXT";
	private final static String STATUS_COMMAND = "STATUS";
	
	private String mailbox;

	public UIDNextCommand(String mailbox) {
		this.mailbox = mailbox;
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) {
		boolean isOK = isOk(rs);
		data = null;
		if (isOK) {
			Iterator<IMAPResponse> it = rs.iterator();
			int len = rs.size() - 1;
			for (int i = 0; i < len; i++) {
				IMAPResponse ir = it.next();
				String s = AtomHelper.getFullResponse(ir.getPayload(), ir.getStreamData());
					
				String uidNextHasString = getUIDNextHasString(s);
				if (Strings.isNullOrEmpty(uidNextHasString)) {
					continue;
				}
				
				data = Long.valueOf(uidNextHasString);
				break;
			}
		}
	}

	private String getUIDNextHasString(String fullPayload) {
		return IMAPParsingTools.getStringHasNumberForField(fullPayload, UIDNEXT_ITEM + " ");
	}

	@Override
	protected CommandArgument buildCommand() {
		String cmd = STATUS_COMMAND + " " 
				+ mailbox 
				+  " (" + UIDNEXT_ITEM + ")";
		return new CommandArgument(cmd, null);
	}

}