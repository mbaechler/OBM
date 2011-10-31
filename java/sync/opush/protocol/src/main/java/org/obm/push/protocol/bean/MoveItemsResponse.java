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
package org.obm.push.protocol.bean;

import java.util.List;

import org.obm.push.bean.MoveItemsStatus;

public class MoveItemsResponse {

	public static class MoveItemsItem {
		private MoveItemsStatus itemStatus;
		private final String sourceMessageId;
		private String newDstId;

		public MoveItemsItem(MoveItemsStatus status, String sourceMessageId) {
			this.itemStatus = status;
			this.sourceMessageId = sourceMessageId;
		}
		public void setStatusForItem(MoveItemsStatus status) {
			this.itemStatus = status;
		}
		public MoveItemsStatus getItemStatus() {
			return itemStatus;
		}
		public String getSourceMessageId() {
			return sourceMessageId;
		}
		public void setDstMesgId(String newDstId) {
			this.newDstId = newDstId;
		}
		public String getNewDstId() {
			return newDstId;
		}
	}

	private final List<MoveItemsItem> moveItemsItem;
	
	public MoveItemsResponse(List<MoveItemsItem> moveItemsItem) {
		this.moveItemsItem = moveItemsItem;
	}
	
	public List<MoveItemsItem> getMoveItemsItem() {
		return moveItemsItem;
	}
	
}
