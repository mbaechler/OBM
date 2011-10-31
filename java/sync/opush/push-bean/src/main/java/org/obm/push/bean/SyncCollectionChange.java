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


public class SyncCollectionChange implements Serializable {

	private String serverId;
	private String clientId;
	private String modType;
	private PIMDataType type;
	private IApplicationData data;
	
	public SyncCollectionChange(String serverId, String clientId,
			String modType, IApplicationData data, PIMDataType type) {
		super();
		this.serverId = serverId;
		this.clientId = clientId;
		this.modType = modType;
		this.data = data;
		this.type = type;
	}

	public String getServerId() {
		return serverId;
	}

	public String getClientId() {
		return clientId;
	}

	public String getModType() {
		return modType;
	}

	public IApplicationData getData() {
		return data;
	}

	public PIMDataType getType() {
		return type;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(serverId, clientId, modType, type, data);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof SyncCollectionChange) {
			SyncCollectionChange that = (SyncCollectionChange) object;
			return Objects.equal(this.serverId, that.serverId)
				&& Objects.equal(this.clientId, that.clientId)
				&& Objects.equal(this.modType, that.modType)
				&& Objects.equal(this.type, that.type)
				&& Objects.equal(this.data, that.data);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("serverId", serverId)
			.add("clientId", clientId)
			.add("modType", modType)
			.add("type", type)
			.add("data", data)
			.toString();
	}
	
}
