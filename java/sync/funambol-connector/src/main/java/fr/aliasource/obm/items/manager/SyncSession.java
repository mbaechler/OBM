package fr.aliasource.obm.items.manager;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.calendar.SyncRange;

import com.funambol.framework.engine.source.SyncContext;

public class SyncSession {

	private final String userLogin;
	private final String userPassword;
	private final SyncRange syncRange;
	
	private AccessToken obmAccessToken;
	
	public SyncSession(SyncContext context) {
		this.userLogin = context.getPrincipal().getUser().getUsername();
		this.userPassword = context.getPrincipal().getUser().getPassword();
		this.syncRange = initSyncRange(context.getSourceQuery());
	}

	private SyncRange initSyncRange(String sourceQuery) {
		//format:   /dr(-30,90)
		if (sourceQuery != null && !sourceQuery.equals("") && sourceQuery.contains("dr(")) {
			String min = sourceQuery.split(",")[0];
			String max = sourceQuery.split(",")[1];
			String rangeMin = min.replace("/dr(-", "");
			String rangeMax = max.replace(")", "");
			if(!StringUtils.isBlank(rangeMin) && !StringUtils.isBlank(rangeMax) ){
				int minDays = Integer.parseInt(rangeMin);
				int maxDays = Integer.parseInt(rangeMax);
				Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				now.add(Calendar.DAY_OF_MONTH, 0 - minDays);
				Timestamp before = new Timestamp(now.getTimeInMillis());
				now.add(Calendar.DAY_OF_MONTH, minDays + maxDays);
				Timestamp after = new Timestamp(now.getTimeInMillis());
				return new SyncRange(before, after);
			}
		}
		return null;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public SyncRange getSyncRange() {
		return syncRange;
	}

	public AccessToken getObmAccessToken() {
		return obmAccessToken;
	}

	public void setObmAccessToken(AccessToken obmAccessToken) {
		this.obmAccessToken = obmAccessToken;
	}
	
	

	
}
