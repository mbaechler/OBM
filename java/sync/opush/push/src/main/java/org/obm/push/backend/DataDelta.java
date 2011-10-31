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
package org.obm.push.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.obm.push.bean.ItemChange;

public class DataDelta {
	
	private final List<ItemChange> changes;
	private final List<ItemChange> deletions;
	private final Date syncDate;
	
	public DataDelta(Collection<ItemChange> changes, List<ItemChange> deletions, Date syncDate) {
		this.syncDate = syncDate;
		this.changes = new ArrayList<ItemChange>(changes);
		this.deletions = new ArrayList<ItemChange>(deletions);
	}

	public List<ItemChange> getChanges() {
		return changes;
	}
	
	public List<ItemChange> getDeletions() {
		return deletions;
	}
	
	public Date getSyncDate() {
		return syncDate;
	}
	
	public int getItemEstimateSize() {
		int count = 0;
		if (changes != null) {
			count += changes.size();
		}
		if (deletions != null) {
			count += deletions.size();
		}
		return count;
	}

}
