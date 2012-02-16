package org.obm.funambol.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.funambol.framework.engine.SyncItemKey;
import com.google.common.base.Preconditions;

public abstract class ObmService {

	protected final Logger logger = LoggerFactory.getLogger(ObmService.class);
	
	
	protected ObmService() {
	
	}

	protected String getCheckedSyncItemKeyAsString(SyncItemKey syncItemKey) {
		Preconditions.checkNotNull(syncItemKey);
		Preconditions.checkNotNull(syncItemKey.getKeyValue());
		return syncItemKey.getKeyAsString();
	}

}
