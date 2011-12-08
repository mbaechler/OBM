package org.obm.sync.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.obm.sync.calendar.CalendarInfo;

import fr.aliacom.obm.common.domain.ObmDomain;

public class AccessToken {

	private String user;
	private String sessionId;
	private int obmId;
	private ObmDomain domain;
	private String origin;
	private String email;
	private boolean rootAccount;

	private Map<String, String> isoCodeToNameCache;

	private Map<String, String> serviceProps;

	private Collection<CalendarInfo> calendarRights;

	private MavenVersion version;
	private int conversationUid;

	public AccessToken(int obmId, String origin) {
		this.obmId = obmId;
		this.origin = origin;
		this.isoCodeToNameCache = new HashMap<String, String>();
		this.serviceProps = new HashMap<String, String>();
	}
	
	public ObmDomain getDomain() {
		return domain;
	}

	public void setDomain(ObmDomain domain) {
		this.domain = domain;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public int getObmId() {
		return obmId;
	}

	public void setObmId(int obmId) {
		this.obmId = obmId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<String, String> getIsoCodeToNameCache() {
		return isoCodeToNameCache;
	}

	public void setIsoCodeToNameCache(Map<String, String> isoCodeToNameCache) {
		this.isoCodeToNameCache = isoCodeToNameCache;
	}

	public String getServiceProperty(String key) {
		return serviceProps.get(key);
	}

	public void addServiceProperty(String key, String value) {
		serviceProps.put(key, value);
	}

	public Map<String, String> getServiceProperties() {
		return serviceProps;
	}

	public void setServiceProperties(Map<String, String> props) {
		this.serviceProps = props;
	}

	public Collection<CalendarInfo> getCalendarRights() {
		return calendarRights;
	}

	public void setCalendarRights(Collection<CalendarInfo> calendarRights) {
		this.calendarRights = calendarRights;
	}

	public void setVersion(MavenVersion version) {
		this.version = version;
	}

	public MavenVersion getVersion() {
		return version;
	}

	public void setRootAccount(boolean rootAccount) {
		this.rootAccount = rootAccount;
	}

	public boolean isRootAccount() {
		return rootAccount;
	}

	public int getConversationUid() {
		return conversationUid;
	}

	public void setConversationUid(int conversationUid) {
		this.conversationUid = conversationUid;
	}
	
	public String getUserWithDomain() {
		return user + "@" + domain.getName();
	}
}
