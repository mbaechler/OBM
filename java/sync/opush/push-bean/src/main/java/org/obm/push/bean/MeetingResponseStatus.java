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

public enum MeetingResponseStatus {

	SUCCESS, // 1
	INVALID_MEETING_RREQUEST, // 2
	SERVER_MAILBOX_ERROR, // 3
	SERVER_ERROR; // 4

	public String asXmlValue() {
		switch (this) {
		case INVALID_MEETING_RREQUEST:
			return "2";
		case SERVER_MAILBOX_ERROR:
			return "3";
		case SERVER_ERROR:
			return "4";
		case SUCCESS:
		default:
			return "1";
		}
	}

}
