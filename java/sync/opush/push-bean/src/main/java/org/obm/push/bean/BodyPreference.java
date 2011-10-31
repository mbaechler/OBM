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

public class BodyPreference implements Serializable {

	private Integer truncationSize;
	private MSEmailBodyType type;
	
	public Integer getTruncationSize() {
		return this.truncationSize;
	}

	public void setTruncationSize(Integer truncationSize) {
		this.truncationSize = truncationSize;
	}
	
	public MSEmailBodyType getType() {
		return type;
	}

	public void setType(MSEmailBodyType type) {
		this.type = type;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(truncationSize, type);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof BodyPreference) {
			BodyPreference that = (BodyPreference) object;
			return Objects.equal(this.truncationSize, that.truncationSize)
				&& Objects.equal(this.type, that.type);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("super", super.toString())
			.add("truncationSize", truncationSize)
			.add("type", type)
			.toString();
	}
	
}
