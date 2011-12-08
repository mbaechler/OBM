package org.obm.sync.client.calendar;

import org.obm.sync.client.impl.SyncClientException;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.locators.Locator;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class TodoClient extends AbstractEventSyncClient {
	
	@Inject
	private TodoClient(SyncClientException syncClientException, Locator locator, LoginService login) {
		super("/todo", syncClientException, locator, login);
	}
	
}
