package org.obm.caldav.obmsync;

import org.obm.caldav.obmsync.provider.impl.AbstractObmSyncProvider;
import org.obm.caldav.obmsync.service.impl.CalendarService;
import org.obm.caldav.server.ICalendarService;
import org.obm.caldav.server.IProxy;
import org.obm.caldav.server.share.Token;
import org.obm.sync.auth.AccessToken;

public class ProxyImpl implements IProxy {

	private AccessToken token;
	private String userId;
	private String calendar;
	private ICalendarService calendarService;
	
	
	public ProxyImpl(){
	}
	
	private void initService() {
		calendarService = new CalendarService(token,calendar,userId);
	}
	
	@Override
	public void login(Token token) {
		
		this.userId = token.getLoginAtDomain();
		this.calendar = token.getLogin();
		this.token = AbstractObmSyncProvider.login(userId, token.getPassword());
		this.initService();
	}

	@Override
	public void logout() {
		AbstractObmSyncProvider.logout(token);
	}

	@Override
	public ICalendarService getCalendarService() {
		if(this.calendarService == null){
			throw new RuntimeException("You must be logged");
		}
		return calendarService;
	}

}
