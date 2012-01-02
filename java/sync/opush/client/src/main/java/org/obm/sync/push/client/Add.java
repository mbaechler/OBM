package org.obm.sync.push.client;

import com.google.common.base.Objects;

public final class Add {
	
	private String serverId;

	public Add() {
	}
	
	public Add(String serverId) {
		setServerId(serverId);
	}
	
	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(serverId);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Add) {
			Add that = (Add) object;
			return Objects.equal(this.serverId, that.serverId);
		}
		return false;
	}
	
}
