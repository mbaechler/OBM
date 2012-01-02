package org.obm.sync.push.client;

import java.util.HashMap;
import java.util.Map;
import com.google.common.base.Objects;

public final class SyncResponse {

	private Map<String, Collection> cl;

	public SyncResponse(Map<String, Collection> cl) {
		this.cl = new HashMap<String, Collection>(cl);
	}

	public Map<String, Collection> getCollections() {
		return cl;
	}
	
	public Collection getCollection(String key) {
		return cl.get(key);
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(cl);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof SyncResponse) {
			SyncResponse that = (SyncResponse) object;
			return Objects.equal(this.cl, that.cl);
		}
		return false;
	}
	
}
