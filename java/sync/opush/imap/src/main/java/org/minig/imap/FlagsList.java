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

package org.minig.imap;

import java.util.HashSet;
import java.util.Iterator;

public class FlagsList extends HashSet<Flag> {

	private long uid;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8557645090248136216L;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");

		Iterator<Flag> it = iterator();
		for (int i = size() - 1; i >= 0; i--) {
			if (i > 0) {
				sb.append(' ');
			}
			sb.append(it.next().toString());
		}

		sb.append(")");
		return sb.toString();
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

}
