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

public enum CalendarMeetingStatus {

	IS_NOT_IN_MEETING, // 0
	IS_IN_MEETING, // 1
	MEETING_RECEIVED, // 3
	MEETING_IS_CANCELED, // 5
	MEETING_IS_CANCELED_AND_RECEIVED; // 7

	public String asIntString() {
		switch (this) {
		case MEETING_IS_CANCELED:
			return "5";
		case MEETING_IS_CANCELED_AND_RECEIVED:
			return "7";
		case MEETING_RECEIVED:
			return "3";
		case IS_IN_MEETING:
			return "1";

		default:
		case IS_NOT_IN_MEETING:
			return "0";

		}
	}
}
