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

import org.obm.push.bean.MeetingResponseStatus;

public class MeetingHandlerResponse {

	public static class ItemChangeMeetingResponse {
		private String calId;
		private String reqId;
		private MeetingResponseStatus status;
		
		public ItemChangeMeetingResponse() {
		}
		
		public String getCalId() {
			return calId;
		}
		
		public void setCalId(String calId) {
			this.calId = calId;
		}

		public void setReqId(String reqId) {
			this.reqId = reqId;
		}
		
		public String getReqId() {
			return reqId;
		}

		public void setStatus(MeetingResponseStatus status) {
			this.status = status;
		}
		
		public MeetingResponseStatus getStatus() {
			return status;
		}
		
	}
	
	private List<ItemChangeMeetingResponse> itemChanges;
	
	public MeetingHandlerResponse(List<ItemChangeMeetingResponse> itemChanges) {
		this.itemChanges = itemChanges;
	}

	public List<ItemChangeMeetingResponse> getItemChanges() {
		return itemChanges;
	}

	public void setItemChangeMeetingResponse(List<ItemChangeMeetingResponse> itemChanges) {
		this.itemChanges = itemChanges;
	}
	
}
