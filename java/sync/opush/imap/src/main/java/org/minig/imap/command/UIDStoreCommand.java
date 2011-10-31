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

import java.util.Collection;
import java.util.List;

import org.minig.imap.FlagsList;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.MessageSet;

public class UIDStoreCommand extends Command<Boolean> {

	private Collection<Long> uids;
	private FlagsList fl;
	private boolean set;

	public UIDStoreCommand(Collection<Long> uids, FlagsList fl, boolean set) {
		this.uids = uids;
		this.fl = fl;
		this.set = set;
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) {
		IMAPResponse ok = rs.get(rs.size() - 1);
		data = ok.isOk();
		if (logger.isDebugEnabled()) {
			logger.debug(ok.getPayload());
		}
	}

	@Override
	protected CommandArgument buildCommand() {
		String cmd = "UID STORE " + MessageSet.asString(uids) + " "
				+ (set ? "+" : "-") + "FLAGS.SILENT " + fl.toString();

		if (logger.isDebugEnabled()) {
			logger.debug("cmd: " + cmd);
		}

		return new CommandArgument(cmd, null);
	}

}
