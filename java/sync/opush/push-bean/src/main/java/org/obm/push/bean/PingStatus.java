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

public enum PingStatus {

	NO_CHANGES, // 1
	CHANGES_OCCURED, // 2
	MISSING_REQUEST_PARAMS, // 3
	SYNTAX_ERROR_IN_REQUEST, // 4
	INVALID_HEARTBEAT_INTERVAL, // 5
	TOO_MANY_FOLDERS, // 6
	FOLDER_SYNC_REQUIRED, // 7
	SERVER_ERROR; // 8

	public String asXmlValue() {
		switch (this) {
		case CHANGES_OCCURED:
			return "2";
		case MISSING_REQUEST_PARAMS:
			return "3";
		case SYNTAX_ERROR_IN_REQUEST:
			return "4";
		case INVALID_HEARTBEAT_INTERVAL:
			return "5";
		case TOO_MANY_FOLDERS:
			return "6";
		case FOLDER_SYNC_REQUIRED:
			return "7";
		case SERVER_ERROR:
			return "8";

		case NO_CHANGES:
		default:
			return "1";
		}
	}

}
