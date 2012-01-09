package org.obm.push.impl;

import org.obm.push.bean.BackendSession;
import org.obm.push.bean.PIMDataType;
import org.obm.push.service.impl.MappingService;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.auth.AuthFault;
import org.obm.sync.client.CalendarType;
import org.obm.sync.client.login.LoginService;
import org.obm.sync.services.IAddressBook;
import org.obm.sync.services.ICalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Named;

public class ObmSyncBackend {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected String obmSyncHost;

	private final IAddressBook bookClient;
	private final ICalendar calendarClient;
	private final ICalendar todoClient;
	private final LoginService login;
	protected final MappingService mappingService;

	protected ObmSyncBackend(MappingService mappingService, IAddressBook bookClient, 
			@Named(CalendarType.CALENDAR) ICalendar calendarClient, 
			@Named(CalendarType.TODO) ICalendar todoClient,
			LoginService login) {
		this.mappingService = mappingService;
		this.bookClient = bookClient;
		this.calendarClient = calendarClient;
		this.todoClient = todoClient;
		this.login = login;
	}

	protected AccessToken login(BackendSession session) {
		return login.login(session.getUser().getLoginAtDomain(), session.getPassword());
	}

	protected void logout(AccessToken at) {
		login.logout(at);
	}

	public AccessToken login(String loginAtDomain, String password) throws AuthFault {
		AccessToken token = login.login(loginAtDomain, password);
		try {
			if (token == null || token.getSessionId() == null) {
				throw new AuthFault(loginAtDomain + " can't log on obm-sync. The username or password isn't valid");
			}
		} finally {
			login.logout(token);
		}
		return token;
	}
	
	protected String getDefaultCalendarName(BackendSession bs) {
		return "obm:\\\\" + bs.getUser().getLoginAtDomain() + "\\calendar\\"
				+ bs.getUser().getLoginAtDomain();
	}
	
	protected IAddressBook getBookClient() {
		return bookClient;
	}
	
	protected ICalendar getCalendarClient() {
		return getEventSyncClient(PIMDataType.CALENDAR);
	}

	protected ICalendar getEventSyncClient(PIMDataType type) {
		if (PIMDataType.TASKS.equals(type)) {
			return todoClient;
		} else {
			return calendarClient;
		}
	}

	public LoginService getSyncClient() {
		return login;
	}
}
