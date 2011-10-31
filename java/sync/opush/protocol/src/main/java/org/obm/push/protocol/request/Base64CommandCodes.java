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
package org.obm.push.protocol.request;

public class Base64CommandCodes {

	private final static String[] commands = { "Sync", // 0
			"SendMail", // 1
			"SmartForward", // 2
			"SmartReply", // 3
			"GetAttachment", // 4
			"GetHierarchy", // 5
			"CreateCollection", // 6
			"DeleteCollection", // 7
			"MoveCollection", // 8
			"FolderSync", // 9
			"FolderCreate", // 10
			"FolderDelete", // 11
			"FolderUpdate", // 12
			"MoveItems", // 13
			"GetItemEstimate", // 14
			"MeetingResponse", // 15
			"Search", // 16
			"Settings", // 17
			"Ping", // 18
			"ItemOperations", // 19
			"Provision", // 20
			"ResolveRecipients", // 21
			"ValidateCert" // 22
	};

	public static String getCmd(int value) {
		return commands[value];
	}

}
