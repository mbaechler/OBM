package fr.aliasource.obm.items.manager;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.calendar.SyncRange;

import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.server.Sync4jDevice;

public class SyncSession {

	private final String userLogin;
	private final String userPassword;
	private final Sync4jDevice device;
	private final TimeZone deviceTimeZone;
	private final SyncRange syncRange;
	private boolean encode;
	private int restrictions;
	
	
	private AccessToken obmAccessToken;
	
	public SyncSession(SyncContext context, Sync4jDevice device) {
		this.userLogin = context.getPrincipal().getUser().getUsername();
		this.userPassword = context.getPrincipal().getUser().getPassword();
		this.device = device;
		this.deviceTimeZone = initDeviceTimeZone(device);
		this.syncRange = initSyncRange(context.getSourceQuery());
		this.encode = true;
		this.restrictions = 1;
	}

//	public void setDeviceTimeZone(TimeZone deviceTimeZone) {
//		this.deviceTimeZone = deviceTimeZone;
//		if (deviceTimeZone == null) {
//			this.deviceTimeZone = TimeZone.getTimeZone("Europe/Paris");
//		}
//		logger.info("device timezone set to: "+this.deviceTimeZone);
//	}
	private TimeZone initDeviceTimeZone(Sync4jDevice device) {
		String timezone = device.getTimeZone();
		if(StringUtils.isBlank(timezone)) {
			return TimeZone.getTimeZone("GMT");
		}
		return TimeZone.getTimeZone(timezone);
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

	public Sync4jDevice getDevice() {
		return device;
	}
	
	public String getDeviceCharset(){
		return device.getCharset();
	}

	public TimeZone getDeviceTimeZone() {
		return deviceTimeZone;
	}
	
	public boolean isEncode() {
		return encode;
	}

	public void setEncode(boolean encode) {
		this.encode = encode;
	}
	
	public int getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(int restrictions) {
		this.restrictions = restrictions;
	}
	
}
