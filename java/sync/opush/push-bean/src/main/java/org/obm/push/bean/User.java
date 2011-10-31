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

import java.security.InvalidParameterException;

import com.google.common.base.Objects;

public class User {

	private final String domainName;
	private final String userName;
	
	public User(String userName, String domainName) {
		this.userName = userName.toLowerCase();
		this.domainName = domainName.toLowerCase();
		checkUserName();
		checkDomainName();
	}
	
	private void checkUserName() {
		if (userName.contains("@") || userName.contains("\\")) {
			throw new InvalidParameterException("username is invalid : " + userName);
		}
	}

	private void checkDomainName() {
		if (domainName.contains("@") || domainName.contains("\\")) {
			throw new InvalidParameterException("domain is invalid : " + domainName);
		}
	}

	public String getLoginAtDomain() {
		return userName + "@" + domainName;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(domainName, userName);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof User) {
			User that = (User) object;
			return Objects.equal(this.domainName, that.domainName)
				&& Objects.equal(this.userName, that.userName);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("domainName", domainName)
			.add("userName", userName)
			.toString();
	}
	
}
