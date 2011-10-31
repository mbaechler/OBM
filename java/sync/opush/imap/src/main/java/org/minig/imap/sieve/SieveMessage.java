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

package org.minig.imap.sieve;

import java.util.LinkedList;
import java.util.List;

public class SieveMessage {
	
	private List<String> lines;
	
	public SieveMessage() {
		lines = new LinkedList<String>();
	}
	
	public void addLine(String s) {
		lines.add(s);
	}

	public List<String> getLines() {
		return lines;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(":\n");
		for (String l :lines) {
			sb.append(l);
			sb.append("\n");
		}
		return sb.toString();
	}

}
