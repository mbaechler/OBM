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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.minig.imap.InternalDate;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.MessageSet;

public class UIDFetchInternalDateCommand extends Command<InternalDate[]> {

	private Collection<Long> uids;
	DateFormat df;

	public UIDFetchInternalDateCommand(Collection<Long> uid) {
		this.uids = uid;
		//22-Mar-2010 14:26:18 +0100
		df = new SimpleDateFormat("d-MMM-yyyy HH:mm:ss Z", Locale.ENGLISH);
	}

	@Override
	protected CommandArgument buildCommand() {

		StringBuilder sb = new StringBuilder();
		if (!uids.isEmpty()) {
			sb.append("UID FETCH ");
			sb.append(MessageSet.asString(uids));
			sb.append(" (UID INTERNALDATE)");
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
			data = new InternalDate[0];
			return;
		}
		
		IMAPResponse ok = rs.get(rs.size() - 1);
		if (ok.isOk()) {
			data = new InternalDate[rs.size() - 1];
			Iterator<IMAPResponse> it = rs.iterator();
			for (int i = 0; i < rs.size() - 1; i++) {
				IMAPResponse r = it.next();
				String payload = r.getPayload();

				int fidx = payload.indexOf("INTERNALDATE \"") + "INTERNALDATE \"".length();
				
				if (fidx == -1 + "INTERNALDATE \"".length()) {
					continue;
				}
				
				int endDate = payload.indexOf("\"", fidx);
				String internalDate = "";
				if (fidx > 0 && endDate >= fidx) {
					internalDate = payload.substring(fidx, endDate);
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

				data[i] = new InternalDate(uid,parseDate(internalDate));
			}
		} else {
			logger.warn("error on fetch: " + ok.getPayload());
			data = new InternalDate[0];
		}
	}

	private Date parseDate(String date) {
		try {
			return df .parse(date);
		} catch (ParseException e) {
			logger.error("Can't parse "+date);
		}
		return new Date();
	}

}
