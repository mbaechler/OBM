package org.obm.sync.push.client;

import java.util.LinkedList;
import java.util.List;
import com.google.common.base.Objects;

/**
 * <Collection> <SyncKey>f0e0ec53-40a6-432a-bfee-b8c1d391478c</SyncKey>
 * <CollectionId>179</CollectionId> <Status>1</Status> </Collection>
 * 
 * @author adrienp
 * 
 */
public final class Collection {

	private String syncKey;
	private String collectionId;
	private SyncStatus status;
	private List<Add> adds = new LinkedList<Add>();
	private List<Delete> deletes = new LinkedList<Delete>();

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

	public List<Delete> getDeletes() {
		return deletes;
	}
	
	public void addDelete(Delete data) {
		deletes.add(data);
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(syncKey, collectionId, status, adds, deletes);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Collection) {
			Collection that = (Collection) object;
			return Objects.equal(this.syncKey, that.syncKey)
				&& Objects.equal(this.collectionId, that.collectionId)
				&& Objects.equal(this.status, that.status)
				&& Objects.equal(this.adds, that.adds)
				&& Objects.equal(this.deletes, that.deletes);
		}
		return false;
	}
}
