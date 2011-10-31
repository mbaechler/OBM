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

import java.util.Date;
import java.util.Set;

public class FastFetch {

	private final long uid;
	private final Date internalDate;
	private final Set<Flag> flags;
	
	public FastFetch(long uid, Date internalDate, Set<Flag> flags){
		this.uid = uid;
		this.internalDate = internalDate;
		this.flags = flags;
	}

	public long getUid() {
		return uid;
	}

	public Date getInternalDate() {
		return internalDate;
	}

	public Set<Flag> getFlags() {
		return flags;
	}
	
	public boolean isRead(){
		return flags != null && flags.contains(Flag.SEEN);
	}
	
}
