package org.obm.push.service;

import org.obm.configuration.AbstractConfigurationService;
import org.obm.configuration.SyncPermsConfigurationService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class OpushSyncPermsConfigurationService extends AbstractConfigurationService 
	implements SyncPermsConfigurationService {

	private static final String BLACKLIST_USERS_PARAMS = "blacklist.users";
	private static final String ALLOW_UNKNOWN_PDA_PARAMS = "allow.unknown.pda";

	@Inject
	protected OpushSyncPermsConfigurationService() {
		super("/etc/opush/sync_perms.ini");
	}

	@Override
	public String getBlackListUser(){
		return getStringValue(BLACKLIST_USERS_PARAMS);
	}
	
	@Override
	public Boolean allowUnknownPdaToSync(){
		return getBooleanValue(ALLOW_UNKNOWN_PDA_PARAMS);
	}
}
