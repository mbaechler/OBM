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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.minig.imap.impl.IMAPResponse;
import org.obm.push.utils.FileUtils;

public class UIDFetchMessageCommand extends Command<InputStream> {

	private long uid;

	public UIDFetchMessageCommand(long uid) {
		this.uid = uid;
	}

	@Override
	protected CommandArgument buildCommand() {
		String cmd = "UID FETCH " + uid + " (UID BODY.PEEK[])";
		CommandArgument args = new CommandArgument(cmd, null);
		return args;
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) {
		IMAPResponse stream = rs.get(0);
		IMAPResponse ok = rs.get(rs.size() - 1);
		if (ok.isOk() && stream.getStreamData() != null) {
			InputStream in = stream.getStreamData();
			
			// -1 pattern of the day to remove "\0" at end of stream
			byte[] dest = new byte[0];
			try {
				byte[] byteData = FileUtils.streamBytes(in, true);
				dest = new byte[byteData.length - 1];
				System.arraycopy(byteData, 0, dest, 0, dest.length);
			} catch (IOException e) {
			}
			data = new ByteArrayInputStream(dest);
		} else {
			if (ok.isOk()) {
				logger
						.warn("fetch is ok with no stream in response. Printing received responses :");
				for (IMAPResponse ir : rs) {
					logger.warn("    <= " + ir.getPayload());
				}
				data = new ByteArrayInputStream("".getBytes());
			} else {
				logger.error("UIDFetchMessage failed for uid " + uid + ": "
						+ ok.getPayload());
			}
		}
	}

}
