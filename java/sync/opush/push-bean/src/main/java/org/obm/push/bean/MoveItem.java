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

import com.google.common.base.Objects;

public class MoveItem {

	private final String sourceMessageId;
	private final String sourceFolderId;
	private final String destinationFolderId;

	public MoveItem(String srcMsgId, String srcFldId, String dstFldId) {
		this.sourceMessageId = srcMsgId;
		this.sourceFolderId = srcFldId;
		this.destinationFolderId = dstFldId;
	}
	
	public String getSourceMessageId() {
		return sourceMessageId;
	}

	public String getSourceFolderId() {
		return sourceFolderId;
	}

	public String getDestinationFolderId() {
		return destinationFolderId;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(sourceMessageId, sourceFolderId, destinationFolderId);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof MoveItem) {
			MoveItem that = (MoveItem) object;
			return Objects.equal(this.sourceMessageId, that.sourceMessageId)
				&& Objects.equal(this.sourceFolderId, that.sourceFolderId)
				&& Objects.equal(this.destinationFolderId, that.destinationFolderId);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("sourceMessageId", sourceMessageId)
			.add("sourceFolderId", sourceFolderId)
			.add("destinationFolderId", destinationFolderId)
			.toString();
	}
	
}
