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

public enum AttendeeStatus {
	RESPONSE_UNKNOWN, // 0
	TENTATIVE, // 2
	ACCEPT, // 3
	DECLINE, // 4
	NOT_RESPONDED; // 5

	public String asIntString() {
		switch (this) {
		case ACCEPT:
			return "3";
		case DECLINE:
			return "4";
		case NOT_RESPONDED:
			return "5";
		case TENTATIVE:
			return "2";
		default:
		case RESPONSE_UNKNOWN:
			return "0";

		}

	}
}
