package org.obm.sync.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.obm.sync.calendar.CalendarInfo;

public class AccessToken {

	private String userLogin;
	private String userDisplayName;
	private String userEmail;
	
	private String sessionId;
	private String domain;
	private int obmId;
	private int domainId;
	private String origin;
	private boolean rootAccount;

	private Map<String, String> isoCodeToNameCache;

	private Map<String, String> serviceProps;

	private Collection<CalendarInfo> calendarRights;

	private MavenVersion version;
	private int conversationUid;

	public AccessToken(int obmId, int domainId, String origin) {
		this.obmId = obmId;
		this.domainId = domainId;
		this.origin = origin;
		this.isoCodeToNameCache = new HashMap<String, String>();
		this.serviceProps = new HashMap<String, String>();
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getObmId() {
		return obmId;
	}

	public int getDomainId() {
		return domainId;
	}

	public void setObmId(int obmId) {
		this.obmId = obmId;
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
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
		return userLogin + "@" + domain;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}
	
	public void setUserDisplayName(String displayName) {
		this.userDisplayName = displayName;
	}
	
}
