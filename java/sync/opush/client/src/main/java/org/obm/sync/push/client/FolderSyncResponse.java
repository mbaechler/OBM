package org.obm.sync.push.client;

import java.util.Map;
import com.google.common.base.Objects;

public final class FolderSyncResponse implements IEasReponse {

	private FolderHierarchy fl;
	private String key;

	public FolderSyncResponse(String key, Map<FolderType, Folder> fl) {
		this.fl = new FolderHierarchy(fl);
		this.key = key;
	}
	
	@Override
	public String getReturnedSyncKey() {
		return key;
	}

	public FolderHierarchy getFolders() {
		return fl;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(fl, key);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof FolderSyncResponse) {
			FolderSyncResponse that = (FolderSyncResponse) object;
			return Objects.equal(this.fl, that.fl)
				&& Objects.equal(this.key, that.key);
		}
		return false;
	}
	
}
