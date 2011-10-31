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

package org.minig.imap.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSet {

	private final static Logger logger = LoggerFactory
			.getLogger(MessageSet.class);

	public static final String asString(Collection<Long> uids) {
		TreeSet<Long> sortedUids = new TreeSet<Long>(uids);
		StringBuilder sb = new StringBuilder(uids.size() * 7);
		long firstUid = 0;
		long lastUid = 0;
		boolean firstLoop = true;
		for (Long currentValue: sortedUids) {
			if (firstUid > 0 && currentValue == lastUid + 1) {
				lastUid = currentValue;
				firstLoop = false;
				continue;
			}
			if (firstUid > 0 && lastUid > 0 && lastUid > firstUid) {
				sb.append(':');
				sb.append(lastUid);
				firstUid = 0;
				lastUid = 0;
			}
			if (!firstLoop) {
				sb.append(',');
			}
			sb.append(currentValue);
			firstUid = currentValue;
			lastUid = currentValue;
			firstLoop = false;
		}
		if (firstUid > 0 && lastUid > 0 && lastUid > firstUid) {
			sb.append(':');
			sb.append(lastUid);
		}

		String ret = sb.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("computed set string: " + ret);
		}
		return ret;

	}

	public static ArrayList<Long> asLongCollection(String set, int sizeHint) {
		String[] parts = set.split(",");
		ArrayList<Long> ret = new ArrayList<Long>(sizeHint > 0 ? sizeHint
				: parts.length);
		for (String s : parts) {
			if (!s.contains(":")) {
				ret.add(Long.parseLong(s));
			} else {
				String[] p = s.split(":");
				long start = Long.parseLong(p[0]);
				long end = Long.parseLong(p[1]);
				for (long l = start; l <= end; l++) {
					ret.add(l);
				}
			}
		}
		return ret;
	}

}
