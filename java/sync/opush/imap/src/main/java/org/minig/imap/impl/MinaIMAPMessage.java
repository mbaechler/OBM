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

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinaIMAPMessage {

	private final static Logger logger = LoggerFactory
			.getLogger(MessageSet.class);

	private List<byte[]> frags;
	private String messageLine;
	public MinaIMAPMessage(String line) {
		this.messageLine = line;
		this.frags = new LinkedList<byte[]>();
	}

	public void addLine(byte[] cur) {
		byte[] prev = new byte[0];
		if (frags.size() > 0) {
			prev = frags.get(frags.size() - 1);
		}

		int i = 0;
		for (; i < cur.length && i < prev.length; i++) {
			if (cur[i] != prev[i]) {
				break;
			}
		}
		byte[] newCur = new byte[cur.length - i];
		System.arraycopy(cur, i, newCur, 0, newCur.length);

		if (logger.isDebugEnabled()) {
			logger.debug("addline cur.len" + cur.length + " prev.len "
					+ prev.length + " cur: " + new String(cur) + " prev: "
					+ new String(prev));
		}

		frags.add(newCur);
	}

	public void addBuffer(ByteBuffer buffer) {
		frags.add(buffer.array());
	}

	public boolean hasFragments() {
		return !frags.isEmpty();
	}

	public String toString() {
		StringBuilder b = new StringBuilder("\nimap command:");
		b.append(messageLine);
		if (frags != null) {
			for (byte[] bu : frags) {
				b.append("[buf:").append(bu.length).append(']');
			}
		}
		return b.toString();
	}

	public List<byte[]> getFragments() {
		return frags;
	}

	public String getMessageLine() {
		return messageLine;
	}

}
