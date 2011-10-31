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
package org.obm.push.bean;

import java.util.Date;
import java.util.Set;

import com.google.common.base.Objects;

public class ChangedCollections {

	private final Date lastSync;
	private final Set<SyncCollection> changes;
	
	public ChangedCollections(Date lastSync, Set<SyncCollection> changed) {
		this.lastSync = lastSync;
		this.changes = changed;
	}
	
	public Date getLastSync() {
		return lastSync;
	}

	public boolean hasChanges() {
		return !changes.isEmpty();
	}
	
	public Set<SyncCollection> getChanges() {
		return changes;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(lastSync, changes);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof ChangedCollections) {
			ChangedCollections that = (ChangedCollections) object;
			return Objects.equal(this.lastSync, that.lastSync)
				&& Objects.equal(this.changes, that.changes);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.toString();
	}

}
