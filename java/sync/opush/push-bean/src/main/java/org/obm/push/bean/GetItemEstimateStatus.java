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

public enum GetItemEstimateStatus {
	OK, // 1 Success.
	INVALID_COLLECTION, // 2 A collection was invalid or one of the specified
						// collection IDs was invalid.
	NEED_SYNC, // 3 Synchronization state has not been primed yet. The Sync
				// command MUST be performed first.
	INVALID_SYNC_KEY; // 4 The specified synchronization key was invalid
	
	public String asXmlValue() {
		switch (this) {
		case INVALID_COLLECTION:
			return "2";
		case NEED_SYNC:
			return "3";
		case INVALID_SYNC_KEY:
			return "4";
		case OK:
		default:
			return "1";
		}
	}

}
