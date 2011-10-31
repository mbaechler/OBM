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
package org.obm.sync.push.client;

import java.util.LinkedList;
import java.util.List;

/**
 * <Collection> <SyncKey>f0e0ec53-40a6-432a-bfee-b8c1d391478c</SyncKey>
 * <CollectionId>179</CollectionId> <Status>1</Status> </Collection>
 */
public class Collection {

	private String syncKey;
	private String collectionId;
	private SyncStatus status;
	private List<Add> adds = new LinkedList<Add>();

	public String getSyncKey() {
		return syncKey;
	}

	public void setSyncKey(String syncKey) {
		this.syncKey = syncKey;
	}

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public SyncStatus getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = SyncStatus.getSyncStatus(status);
	}

	public List<Add> getAdds() {
		return adds;
	}

	public void addAdd(Add applicationData) {
		adds.add(applicationData);
	}

}
