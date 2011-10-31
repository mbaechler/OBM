/* ***** BEGIN LICENSE BLOCK *****
 *
 * %%
 * Copyright (C) 2000 - 2011 Linagora
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK ***** */
package org.obm.push.bean;

import java.math.BigDecimal;

import com.google.common.base.Objects;

public class BackendSession {

	private final Credentials credentials;
	private final Device device;
	private final String command;
	private final BigDecimal protocolVersion;

	public BackendSession(Credentials credentials, String command, Device device, BigDecimal protocolVersion) {
		super();
		this.credentials = credentials;
		this.command = command;
		this.device = device;
		this.protocolVersion = protocolVersion;
	}

	public boolean checkHint(String key, boolean defaultValue) {
		return device.checkHint(key, defaultValue);
	}

	public String getLoginAtDomain() {
		return credentials.getLoginAtDomain();
	}

	public String getPassword() {
		return credentials.getPassword();
	}

	public String getDevId() {
		return device.getDevId();
	}

	public String getDevType() {
		return device.getDevType();
	}

	public String getCommand() {
		return command;
	}

	public BigDecimal getProtocolVersion() {
		return this.protocolVersion;
	}
	
	public Credentials getCredentials() {
		return credentials;
	}
	
	public Device getDevice(){
		return device;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(credentials, device, command, protocolVersion);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof BackendSession) {
			BackendSession that = (BackendSession) object;
			return Objects.equal(this.credentials, that.credentials)
				&& Objects.equal(this.device, that.device)
				&& Objects.equal(this.command, that.command)
				&& Objects.equal(this.protocolVersion, that.protocolVersion);
		}
		return false;
	}

	@Override
	public final String toString() {
		return Objects.toStringHelper(this)
			.add("credentials", credentials)
			.add("device", device)
			.add("command", command)
			.add("protocolVersion", protocolVersion)
			.toString();
	}
	
}
