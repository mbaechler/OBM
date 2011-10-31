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

import java.io.InputStream;

import com.google.common.base.Objects;

public class MSAttachementData {
	
	private final InputStream file;
	private final String contentType;
	
	public MSAttachementData(String contentType, InputStream file){
		this.contentType = contentType;
		this.file = file;
	}

	public InputStream getFile() {
		return file;
	}

	public String getContentType() {
		return contentType;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(file, contentType);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof MSAttachementData) {
			MSAttachementData that = (MSAttachementData) object;
			return Objects.equal(this.file, that.file)
				&& Objects.equal(this.contentType, that.contentType);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("file", file)
			.add("contentType", contentType)
			.toString();
	}
	
}
