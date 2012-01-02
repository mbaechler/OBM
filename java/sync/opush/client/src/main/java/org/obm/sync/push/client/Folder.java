package org.obm.sync.push.client;

import com.google.common.base.Objects;

public final class Folder {
	
	private String serverId;
	private String parentId;
	private String name;
	private FolderType type;
	
	public String getServerId() {
		return serverId;
	}
	
	public void setServerId(String exchangeId) {
		this.serverId = exchangeId;
	}
	
	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public FolderType getType() {
		return type;
	}
	
	public void setType(FolderType type) {
		this.type = type;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(serverId, parentId, name, type);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Folder) {
			Folder that = (Folder) object;
			return Objects.equal(this.serverId, that.serverId)
				&& Objects.equal(this.parentId, that.parentId)
				&& Objects.equal(this.name, that.name)
				&& Objects.equal(this.type, that.type);
		}
		return false;
	}
	
}
