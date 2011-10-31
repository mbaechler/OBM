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

import java.util.List;

import org.minig.imap.ListInfo;
import org.minig.imap.ListResult;
import org.minig.imap.impl.IMAPResponse;

public class AbstractListCommand extends SimpleCommand<ListResult> {

	protected boolean subscribedOnly;

	protected AbstractListCommand(boolean subscribedOnly) {
		super((subscribedOnly ? "LSUB " : "LIST ") + "\"\" \"*\"");
		this.subscribedOnly = subscribedOnly;
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) {
		ListResult lr = new ListResult(rs.size() - 1);
		for (int i = 0; i < rs.size() - 1; i++) {
			String p = rs.get(i).getPayload();
			if (!p.contains( subscribedOnly? "LSUB " : " LIST ")) {
				continue;
			}
			int oParen = p.indexOf('(', 5);
			int cPren = p.indexOf(')', oParen);
			String flags = p.substring(oParen + 1, cPren);
			if (i == 0) {
				char imapSep = p.charAt(cPren + 3);
				lr.setImapSeparator(imapSep);
			}
			String mbox = fromUtf7(p.substring(cPren + 7, p.length()-1));
			lr.add(new ListInfo(mbox, !flags.contains("\\Noselect"), !flags.contains("\\Noinferiors")));
		}
		data = lr;
	}

}
