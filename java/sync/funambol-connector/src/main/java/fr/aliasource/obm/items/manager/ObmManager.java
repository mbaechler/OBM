package fr.aliasource.obm.items.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.framework.engine.SyncItemKey;
import com.google.common.base.Preconditions;

public abstract class ObmManager {

	protected final Logger logger = LoggerFactory.getLogger(ObmManager.class);
	
	
	protected ObmManager() {
	
	}

	protected String getCheckedSyncItemKeyAsString(SyncItemKey syncItemKey) {
		Preconditions.checkNotNull(syncItemKey);
		Preconditions.checkNotNull(syncItemKey.getKeyValue());
		return syncItemKey.getKeyAsString();
	}

}
