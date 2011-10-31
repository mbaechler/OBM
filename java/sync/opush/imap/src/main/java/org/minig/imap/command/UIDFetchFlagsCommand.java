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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.minig.imap.Flag;
import org.minig.imap.FlagsList;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.MessageSet;

public class UIDFetchFlagsCommand extends Command<Collection<FlagsList>> {

	private Collection<Long> uids;

	public UIDFetchFlagsCommand(Collection<Long> uid) {
		this.uids = uid;
	}

	@Override
	protected CommandArgument buildCommand() {

		StringBuilder sb = new StringBuilder();
		if (!uids.isEmpty()) {
			sb.append("UID FETCH ");
			sb.append(MessageSet.asString(uids));
			sb.append(" (UID FLAGS)");
		} else {
			sb.append("NOOP");
		}
		String cmd = sb.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("cmd: " + cmd);
		}
		CommandArgument args = new CommandArgument(cmd, null);
		return args;
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) {
		if (uids.isEmpty()) {
			data = Collections.emptyList();
			return;
		}
		
		IMAPResponse ok = rs.get(rs.size() - 1);
		if (ok.isOk()) {
			ArrayList<FlagsList> list = new ArrayList<FlagsList>(rs.size() - 1);
			Iterator<IMAPResponse> it = rs.iterator();
			for (int i = 0; i < rs.size() - 1; i++) {
				IMAPResponse r = it.next();
				String payload = r.getPayload();

				int fidx = payload.indexOf("FLAGS (") + "FLAGS (".length();
				
				if (fidx == -1 + "FLAGS (".length()) {
					continue;
				}
				
				int endFlags = payload.indexOf(")", fidx);
				String flags = "";
				if (fidx > 0 && endFlags >= fidx) {
					flags = payload.substring(fidx, endFlags);
				} else {
					logger.error("Failed to get flags in fetch response: "
							+ payload);
				}

				int uidIdx = payload.indexOf("UID ") + "UID ".length();
				int endUid = uidIdx;
				while (Character.isDigit(payload.charAt(endUid))) {
					endUid++;
				}
				long uid = Long.parseLong(payload.substring(uidIdx, endUid));

				// logger.info("payload: " + r.getPayload()+" uid: "+uid);

				FlagsList flagsList = new FlagsList();
				parseFlags(flags, flagsList);
				flagsList.setUid(uid);
				list.add(flagsList);
			}
			data = list;
		} else {
			logger.warn("error on fetch: " + ok.getPayload());
			data = Collections.emptyList();
		}
	}

	private void parseFlags(String flags, FlagsList flagsList) {
		// TODO this is probably slow as hell
		if (flags.contains("\\Seen")) {
			flagsList.add(Flag.SEEN);
		}
		if (flags.contains("\\Flagged")) {
			flagsList.add(Flag.FLAGGED);
		}
		if (flags.contains("\\Deleted")) {
			flagsList.add(Flag.DELETED);
		}
		if (flags.contains("\\Answered")) {
			flagsList.add(Flag.ANSWERED);
		}
	}

}
