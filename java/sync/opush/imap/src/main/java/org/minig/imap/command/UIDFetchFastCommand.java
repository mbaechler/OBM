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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.minig.imap.FastFetch;
import org.minig.imap.Flag;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.MessageSet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * FAST
 *        Macro equivalent to: (FLAGS INTERNALDATE RFC822.SIZE)
 * @author adrienp
 *
 */
public class UIDFetchFastCommand extends Command<Collection<FastFetch>> {

	private Collection<Long> uids;
	DateFormat df;

	public UIDFetchFastCommand(Collection<Long> uid) {
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
			sb.append(" FAST");
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
			data = ImmutableSet.of();
			return;
		}
		IMAPResponse ok = rs.get(rs.size() - 1);
		Builder<FastFetch> buildSet = ImmutableSet.builder();
		if (ok.isOk()) {
			Iterator<IMAPResponse> it = rs.iterator();
			for (int i = 0; it.hasNext() && i < uids.size(); ) {
				IMAPResponse r = it.next();
				String payload = r.getPayload();
				if (!payload.contains(" FETCH")) {
					if (logger.isDebugEnabled()) {
						logger.debug("not a fetch: "+payload);
					}
					continue;
				}
				
				long uid = getUid(payload);
				Date internalDate = getInternalDate(payload);
				Set<Flag> flags = getFlags(payload);
				buildSet.add(new FastFetch(uid, internalDate, flags));
			}
		} else {
			logger.warn("error on fetch: " + ok.getPayload());
		}
		
		data = buildSet.build();
	}

	private long getUid(String payload) {
		int uidIdx = payload.indexOf("UID ") + "UID ".length();
		int endUid = uidIdx;
		while (Character.isDigit(payload.charAt(endUid))) {
			endUid++;
		}
		return Long.parseLong(payload.substring(uidIdx, endUid));
	}

	private Date getInternalDate(String payload) {
		int fidx = payload.indexOf("INTERNALDATE \"") + "INTERNALDATE \"".length();
		
		if (fidx == -1 + "INTERNALDATE \"".length()) {
			return new Date(0);
		}
		int endDate = payload.indexOf("\"", fidx);
		String internalDate = "";
		if (fidx > 0 && endDate >= fidx) {
			internalDate = payload.substring(fidx, endDate);
		} else {
			logger.error("Failed to get flags in fetch response: "
					+ payload);
		}
		return parseDate(internalDate);
	}
	
	private Set<Flag> getFlags(String payload) {
		int fidx = payload.indexOf("FLAGS (") + "FLAGS (".length();
		if (fidx == -1 + "FLAGS (".length()) {
			return new HashSet<Flag>(0);
		}
		
		int endFlags = payload.indexOf(")", fidx);
		String flags = "";
		if (fidx > 0 && endFlags >= fidx) {
			flags = payload.substring(fidx, endFlags);
		} else {
			logger.error("Failed to get flags in fetch response: "
					+ payload);
		}
		return parseFlags(flags);
	}
	
	private Date parseDate(String date) {
		try {
			return df .parse(date);
		} catch (ParseException e) {
			logger.error("Can't parse internal date["+date+"]");
		}
		return new Date(0);
	}
	
	private Set<Flag> parseFlags(String flags) {
		Set<Flag> flagsList = new HashSet<Flag>();
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
		return flagsList;
	}
	
}
