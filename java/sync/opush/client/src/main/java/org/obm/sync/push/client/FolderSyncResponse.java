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
package org.obm.sync.push.client;

import java.util.Map;

public class FolderSyncResponse implements IEasReponse {

	private FolderHierarchy fl;
	private String key;

	public FolderSyncResponse(String key, Map<FolderType, Folder> fl) {
		this.fl = new FolderHierarchy(fl);
		this.key = key;
	}
	
	@Override
	public String getReturnedSyncKey() {
		return key;
	}

	public FolderHierarchy getFolders() {
		return fl;
	}
	
}
