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

import java.io.Serializable;

import com.google.common.base.Objects;

public class Credentials implements Serializable {

	private final String password;
	private final String loginAtDomain;

	public Credentials(String loginAtDomain, String password) {
		super();
		this.loginAtDomain = loginAtDomain;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getLoginAtDomain() {
		return loginAtDomain;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(password, loginAtDomain);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof Credentials) {
			Credentials that = (Credentials) object;
			return Objects.equal(this.password, that.password)
				&& Objects.equal(this.loginAtDomain, that.loginAtDomain);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("password", password)
			.add("loginAtDomain", loginAtDomain)
			.toString();
	}	
	
	
}
