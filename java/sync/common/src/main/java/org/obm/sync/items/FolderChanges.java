package org.obm.sync.items;

import java.util.Date;
import java.util.Set;

import org.obm.push.utils.DateUtils;
import org.obm.sync.book.Folder;

import com.google.common.collect.Sets;

public class FolderChanges {

	private Set<Folder> updated;
	private Set<Folder> removed;
	private Date lastSync;

	public FolderChanges() {
		this(Sets.<Folder>newHashSet(), Sets.<Folder>newHashSet(), 
				DateUtils.getEpochPlusOneSecondCalendar().getTime()); 
	}
	
	public FolderChanges(Set<Folder> updated, Set<Folder> removed, Date lastSync) {
		this.updated = updated;
		this.removed = removed;
		this.lastSync = lastSync;
	}
	
	public Set<Folder> getUpdated() {
		return updated;
	}

	public void setUpdated(Set<Folder> updated) {
		this.updated = updated;
	}

	public Set<Folder> getRemoved() {
		return removed;
	}

	public void setRemoved(Set<Folder> removed) {
		this.removed = removed;
	}

	public Date getLastSync() {
		return lastSync;
	}
	
}
