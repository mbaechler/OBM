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

public enum FolderSyncStatus {
    OK,//1
    SERVER_ERROR, //6 An error occurred on the server.
    ACCESS_DENIED,//7 Access denied.
    TIMED_OUT,//8 The request timed out.                                      
    INVALID_SYNC_KEY,//9 Synchronization key mismatch or invalid synchronization key.
    INVALID_REQUEST,//10 Incorrectly formatted request.
    UNKNOW_ERROR;//11 An unknown error occurred.
    
    public String asXmlValue() {
		switch (this) {
		case SERVER_ERROR:
			return "6";
		case ACCESS_DENIED:
			return "7";
		case TIMED_OUT:
			return "8";
		case INVALID_SYNC_KEY:
			return "9";
		case INVALID_REQUEST:
			return "10";
		case UNKNOW_ERROR:
			return "11";
		case OK:
		default:
			return "1";
		}
	}
}
