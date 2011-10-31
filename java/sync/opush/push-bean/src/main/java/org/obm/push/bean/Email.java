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

import java.util.Date;

import org.obm.push.utils.index.Indexed;

import com.google.common.base.Objects;

public class Email implements Indexed<Long> {

	private final long uid;
	private final boolean read;
	private final Date date;
	
	public Email(long uid, boolean read, Date date) {
		super();
		this.uid = uid;
		this.read = read;
		this.date = date;
	}

	public long getUid() {
		return uid;
	}

	@Override
	public Long getIndex() {
		return getUid();
	}
	
	public boolean isRead() {
		return read;
	}
	
	public Date getDate() {
		return date;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(uid, read, date);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof Email) {
			Email that = (Email) object;
			return Objects.equal(this.uid, that.uid)
				&& Objects.equal(this.read, that.read)
				&& Objects.equal(this.date, that.date);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("uid", uid)
			.add("read", read)
			.add("date", date)
			.toString();
	}
	
}
