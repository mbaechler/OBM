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

public enum PIMDataType {

	EMAIL, CALENDAR, CONTACTS, TASKS, FOLDER;

	public String asXmlValue() {
		switch (this) {
		case CALENDAR:
			return "Calendar";
		case CONTACTS:
			return "Contacts";
		case TASKS:
			return "Tasks";
		case EMAIL:
			return "Email";
		default :
			return "";
		}
	}
	
	public static PIMDataType getPIMDataType(String collectionPath) {
		if (collectionPath.contains("\\calendar\\")) {
			return PIMDataType.CALENDAR;
		} else if (collectionPath.contains("\\contacts")) {
			return PIMDataType.CONTACTS;
		} else if (collectionPath.contains("\\email\\")) {
			return PIMDataType.EMAIL;
		} else if (collectionPath.contains("\\tasks\\")) {
			return PIMDataType.TASKS;
		} else {
			return PIMDataType.FOLDER;
		}
	}

}
