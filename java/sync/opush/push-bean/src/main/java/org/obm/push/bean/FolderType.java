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

public enum FolderType {

	USER_FOLDER_GENERIC, // 1
	DEFAULT_INBOX_FOLDER, // 2
	DEFAULT_DRAFTS_FOLDERS, // 3
	DEFAULT_DELETED_ITEMS_FOLDERS, // 4
	DEFAULT_SENT_EMAIL_FOLDER, // 5
	DEFAULT_OUTBOX_FOLDER, // 6
	DEFAULT_TASKS_FOLDER, // 7
	DEFAULT_CALENDAR_FOLDER, // 8
	DEFAULT_CONTACTS_FOLDER, // 9
	DEFAULT_NOTES_FOLDER, // 10
	DEFAULT_JOURNAL_FOLDER, // 11
	USER_CREATED_EMAIL_FOLDER, // 12
	USER_CREATED_CALENDAR_FOLDER, // 13
	USER_CREATED_CONTACTS_FOLDER, // 14
	USER_CREATED_TASKS_FOLDER, // 15
	USER_CREATED_JOURNAL_FOLDER, // 16
	USER_CREATED_NOTES_FOLDER, // 17
	UNKNOWN_FOLDER_TYPE; // 18

	public String asIntString() {
		switch (this) {
		case USER_FOLDER_GENERIC:
			return "1";
		case DEFAULT_INBOX_FOLDER:
			return "2";
		case DEFAULT_DRAFTS_FOLDERS:
			return "3";
		case DEFAULT_DELETED_ITEMS_FOLDERS:
			return "4";
		case DEFAULT_SENT_EMAIL_FOLDER:
			return "5";
		case DEFAULT_OUTBOX_FOLDER:
			return "6";
		case DEFAULT_TASKS_FOLDER:
			return "7";
		case DEFAULT_CALENDAR_FOLDER:
			return "8";
		case DEFAULT_CONTACTS_FOLDER:
			return "9";
		case DEFAULT_NOTES_FOLDER:
			return "10";
		case DEFAULT_JOURNAL_FOLDER:
			return "11";
		case USER_CREATED_EMAIL_FOLDER:
			return "12";
		case USER_CREATED_CALENDAR_FOLDER:
			return "13";
		case USER_CREATED_CONTACTS_FOLDER:
			return "14";
		case USER_CREATED_TASKS_FOLDER:
			return "15";
		case USER_CREATED_JOURNAL_FOLDER:
			return "16";
		case USER_CREATED_NOTES_FOLDER:
			return "17";
		default:
			return "18";
		}
	}

}
