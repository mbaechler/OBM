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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.minig.imap.impl.IMAPResponse;

public class UIDFetchPartCommand extends Command<InputStream> {

	private long uid;
	private String section;

	public UIDFetchPartCommand(long uid, String section) {
		this.uid = uid;
		this.section = section;
	}

	@Override
	protected CommandArgument buildCommand() {
		String cmd = "UID FETCH " + uid + " (UID BODY.PEEK[" + section + "])";
		CommandArgument args = new CommandArgument(cmd, null);
		return args;
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) {
		if (logger.isDebugEnabled()) {
			for (IMAPResponse r : rs) {
				logger.debug("r: " + r.getPayload() + " [stream:"
						+ (r.getStreamData() != null) + "]");
			}
		}
		IMAPResponse stream = rs.get(0);
		IMAPResponse ok = rs.get(rs.size() - 1);
		if (ok.isOk() && stream.getStreamData() != null) {
			data = stream.getStreamData();
		} else {
			if (ok.isOk()) {
				data = new ByteArrayInputStream("[empty]".getBytes());
			} else {
				logger.warn("Fetch of part " + section + " in uid " + uid
						+ " failed: " + ok.getPayload());
			}
		}
	}

}
