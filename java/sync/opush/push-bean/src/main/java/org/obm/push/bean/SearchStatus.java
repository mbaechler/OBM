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

public enum SearchStatus {
	
	SUCCESS, //1
	PROTOCOL_VIOLATION,// 2 XML validation error.
	SERVER_ERROR,// 3
	BAD_LINK, // 4
	ACCESS_DENIED, // 5
	NOT_FOUND, // 6
	CONNECTION_FAILED,// 7 
	QUERY_TOO_COMPLEX,// 8 The search query is too complex.
	INDEXING_NOT_LOADED,// 9 Unable to execute this query because Content Indexing is not loaded.
	TIME_OUT, // 10
	BAD_COLLECTION_ID, // 11 Bad CollectionId (the client MUST perform a FolderSync).
	END_OF_RANGE,// 12 Server reached the end of the range that is retrievable by synchronization.
	ACCESS_BLOCKED, // 13 Access Blocked (policy restriction)
	CREDENTIALS_REQUIRED; // 14 Credentials Required to Continue

	
	public String asXmlValue() {
		switch (this) {
		case PROTOCOL_VIOLATION:
			return "2";
		case SERVER_ERROR:
			return "3";
		case BAD_LINK:
			return "4";
		case ACCESS_DENIED:
			return "5";
		case NOT_FOUND:
			return "6";
		case CONNECTION_FAILED:
			return "7";
		case QUERY_TOO_COMPLEX:
			return "8";
		case INDEXING_NOT_LOADED:
			return "9";
		case TIME_OUT:
			return "10";
		case BAD_COLLECTION_ID:
			return "11";
		case END_OF_RANGE:
			return "12";
		case ACCESS_BLOCKED:
			return "13";
		case CREDENTIALS_REQUIRED:
			return "14";
		case SUCCESS:
		default:
			return "1";
		}
	}
}
