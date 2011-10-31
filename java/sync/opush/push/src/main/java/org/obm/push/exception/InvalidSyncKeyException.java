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
package org.obm.push.exception;

public class InvalidSyncKeyException extends Exception {

	private final Integer collectionId;
	private final String syncKey;

	public InvalidSyncKeyException(String syncKey) {
		this(null, syncKey);
	}
	
	public InvalidSyncKeyException(Integer collectionId, String syncKey) {
		super(String.format(
				"A client provided an unknown SyncKey (%s), may be expected after database migration", 
				syncKey));
		this.collectionId = collectionId;
		this.syncKey = syncKey;
	}
	
	public Integer getCollectionId() {
		return collectionId;
	}

	public Object getSyncKey() {
		return syncKey;
	}

}
