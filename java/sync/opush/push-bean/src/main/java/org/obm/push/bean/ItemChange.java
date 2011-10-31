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

import java.io.Serializable;

import com.google.common.base.Objects;

public class ItemChange implements Serializable {
	
	private String serverId;
	private String parentId;
	
	private String displayName;
	private FolderType itemType;
	private boolean isNew;
	
	private IApplicationData data;

	public ItemChange() {
		this(null);
	}
	
	public ItemChange(String serverId) {
		this.serverId = serverId;
	}
	
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public FolderType getItemType() {
		return itemType;
	}

	public void setItemType(FolderType itemType) {
		this.itemType = itemType;
	}

	public IApplicationData getData() {
		return data;
	}

	public void setData(IApplicationData data) {
		this.data = data;
	}

	public Boolean isNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isMSEmail() {
		if (getData() instanceof MSEmail) {
			return true;
		}
		return false;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(serverId, parentId);
	}

	@Override
	public final boolean equals(Object object){
		if (object instanceof ItemChange) {
			ItemChange that = (ItemChange) object;
			return Objects.equal(this.serverId, that.serverId)
				&& Objects.equal(this.parentId, that.parentId);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("serverId", serverId)
			.add("parentId", parentId)
			.add("displayName", displayName)
			.add("itemType", itemType)
			.add("isNew", isNew)
			.add("data", data)
			.toString();
	}

}