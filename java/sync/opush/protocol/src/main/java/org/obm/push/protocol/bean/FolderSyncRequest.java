package org.obm.push.protocol.bean;

public class FolderSyncRequest {
	
	private final String syncKey;
	
	public FolderSyncRequest(String syncKey) {
		this.syncKey = syncKey;
	}
	
	public String getSyncKey() {
		return syncKey;
	}
}