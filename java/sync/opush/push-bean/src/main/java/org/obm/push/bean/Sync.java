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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import com.google.common.collect.ImmutableSet;
import com.google.common.base.Objects;

public class Sync {
	
	private final Map<Integer, SyncCollection> collections;
	private Integer wait;
	
	public Sync() {
		super();
		this.collections = new HashMap<Integer, SyncCollection>();
	}
	
	public Integer getWaitInSecond() {
		Integer ret = 0;
		if(wait != null){
			ret = wait * 60;
		}
		return ret;
	}
	
	public void setWait(Integer wait) {
		this.wait = wait;
	}
	
	public Set<SyncCollection> getCollections() {
		return ImmutableSet.copyOf(collections.values());
	}
	
	public SyncCollection getCollection(Integer collectionId) {
		return collections.get(collectionId);
	}
	
	public void addCollection(SyncCollection collec) {
		collections.put(collec.getCollectionId(), collec);
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(collections, wait);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof Sync) {
			Sync that = (Sync) object;
			return Objects.equal(this.collections, that.collections)
				&& Objects.equal(this.wait, that.wait);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("collections", collections)
			.add("wait", wait)
			.toString();
	}
	
}
