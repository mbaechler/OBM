package org.obm.push.backend;

import java.util.Date;

import org.obm.push.bean.BackendSession;
import org.obm.push.bean.HierarchyItemsChanges;
import org.obm.push.bean.SyncState;
import org.obm.push.exception.DaoException;
import org.obm.push.exception.UnknownObmSyncServerException;
import org.obm.push.exception.activesync.CollectionNotFoundException;

/**
 * The exporter API fetches data from the backend store and returns it to the
 * mobile device
 * 
 */
public interface IHierarchyExporter {

	void configure(SyncState state, String dataClass, Integer filterType,
			int i, int j);

	String getRootFolderUrl(BackendSession bs);

	HierarchyItemsChanges getChanged(BackendSession bs, Date lastSync) throws DaoException, CollectionNotFoundException, UnknownObmSyncServerException;

	int getRootFolderId(BackendSession bs) throws DaoException, CollectionNotFoundException;

	HierarchyItemsChanges getContactsChanged(BackendSession bs, Date lastSync) throws DaoException, CollectionNotFoundException, UnknownObmSyncServerException;

}
