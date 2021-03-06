package org.obm.push.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.obm.push.utils.DateUtils;

import com.google.common.base.Objects;



/**
 * Stores the last sync date for a given sync key & collection
 */
public class SyncState implements Serializable {

	private Date lastSync;
	private boolean lastSyncFiltred;
	private String key;
	private PIMDataType dataType;
	private int id;

	public SyncState(String path) {
		this(path, null, null);
	}
	
	public SyncState(String path, Date lastSync) {
		this(path, null, lastSync);
	}
	
	public SyncState(String path, String key) {
		this(path, key, null);
	}
	
	public SyncState(String path, String key, Date lastSync) {
		this.lastSync = Objects.firstNonNull(lastSync, DateUtils.getEpochPlusOneSecondCalendar().getTime());
		lastSyncFiltred = false;
		if (path.contains("\\calendar\\")) {
			this.dataType = PIMDataType.CALENDAR;
		} else if (path.endsWith("\\contacts")) {
			this.dataType = PIMDataType.CONTACTS;
		} else if (path.contains("\\tasks")) {
			this.dataType = PIMDataType.TASKS;
		} else if (path.contains("\\email")) {
			this.dataType = PIMDataType.EMAIL;
		} else {
			this.dataType = PIMDataType.FOLDER;
		}
		this.key = key;
	}

	public Date getLastSync() {
		return lastSync;
	}

	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Boolean isLastSyncFiltred() {
		return lastSyncFiltred;
	}

	public void setLastSyncFiltred(boolean lastSyncFiltred) {
		this.lastSyncFiltred = lastSyncFiltred;
	}

	public PIMDataType getDataType() {
		return dataType;
	}

	public void setDataType(PIMDataType dataType) {
		this.dataType = dataType;
	}
	
	public void updatingLastSync(FilterType filterType) {
		if (filterType != null) {
			Calendar calendar = filterType.getFilteredDate();
			if (getLastSync() != null && calendar.getTime().after(getLastSync())) {
				setLastSync(calendar.getTime());
				setLastSyncFiltred(true);
			}
		}
	}

	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(lastSync, lastSyncFiltred, key, dataType, id);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof SyncState) {
			SyncState that = (SyncState) object;
			return Objects.equal(this.lastSync, that.lastSync)
				&& Objects.equal(this.lastSyncFiltred, that.lastSyncFiltred)
				&& Objects.equal(this.key, that.key)
				&& Objects.equal(this.dataType, that.dataType)
				&& Objects.equal(this.id, that.id);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("lastSync", lastSync)
			.add("lastSyncFiltred", lastSyncFiltred)
			.add("key", key)
			.add("dataType", dataType)
			.add("id", id)
			.toString();
	}
	
}
