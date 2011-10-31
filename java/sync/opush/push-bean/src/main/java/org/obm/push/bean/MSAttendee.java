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

public class MSAttendee implements Serializable {
	
	private String email;
	private String name;
	private AttendeeStatus attendeeStatus;
	private AttendeeType attendeeType;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public AttendeeStatus getAttendeeStatus() {
		return attendeeStatus;
	}
	
	public void setAttendeeStatus(AttendeeStatus attendeeStatus) {
		this.attendeeStatus = attendeeStatus;
	}
	
	public AttendeeType getAttendeeType() {
		return attendeeType;
	}
	
	public void setAttendeeType(AttendeeType attendeeType) {
		this.attendeeType = attendeeType;
	}
	
	@Override
	public final int hashCode(){
		return Objects.hashCode(email, name, attendeeStatus, attendeeType);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof MSAttendee) {
			MSAttendee that = (MSAttendee) object;
			return Objects.equal(this.email, that.email)
				&& Objects.equal(this.name, that.name)
				&& Objects.equal(this.attendeeStatus, that.attendeeStatus)
				&& Objects.equal(this.attendeeType, that.attendeeType);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("email", email)
			.add("name", name)
			.add("attendeeStatus", attendeeStatus)
			.add("attendeeType", attendeeType)
			.toString();
	}
	
}
