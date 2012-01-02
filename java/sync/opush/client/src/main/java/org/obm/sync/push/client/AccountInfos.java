package org.obm.sync.push.client;

import org.apache.commons.codec.binary.Base64;
import com.google.common.base.Objects;

public final class AccountInfos {
	private String login;
	private String password;
	private String userId;
	private String devId;
	private String devType;
	private String url;
	private String userAgent;

	public AccountInfos(String login, String password, String devId,
			String devType, String url, String userAgent) {
		this.login = login;
		int idx = login.indexOf('@');
		if (idx > 0) {
			String d = login.substring(idx + 1);
			this.userId = d + "\\" + login.substring(0, idx);
		}

		this.password = password;
		this.devId = devId;
		this.devType = devType;
		this.url = url;
		this.userAgent = userAgent;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getUserId() {
		return userId;
	}

	public String getDevId() {
		return devId;
	}

	public String getDevType() {
		return devType;
	}

	public String getUrl() {
		return url;
	}

	public String getUserAgent() {
		return userAgent;
	}
	
	public String authValue() {
		StringBuilder sb = new StringBuilder();
		sb.append("Basic ");
		String unCodedString = userId + ":" + password;
		String encoded = new String(Base64.encodeBase64(unCodedString.getBytes()));
		sb.append(encoded);
		String ret = sb.toString();
		return ret;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(login, password, userId, devId, devType, url, userAgent);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof AccountInfos) {
			AccountInfos that = (AccountInfos) object;
			return Objects.equal(this.login, that.login)
				&& Objects.equal(this.password, that.password)
				&& Objects.equal(this.userId, that.userId)
				&& Objects.equal(this.devId, that.devId)
				&& Objects.equal(this.devType, that.devType)
				&& Objects.equal(this.url, that.url)
				&& Objects.equal(this.userAgent, that.userAgent);
		}
		return false;
	}

}
