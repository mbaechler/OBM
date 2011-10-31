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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FolderHierarchy implements Map<FolderType, Folder> {

	private Map<FolderType, Folder> folders;

	public FolderHierarchy(Map<FolderType, Folder> folders) {
		this.folders = new HashMap<FolderType, Folder>(folders.size()+1);
		this.folders.putAll(folders);
	}

	@Override
	public void clear() {
		folders.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return folders.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return folders.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<FolderType, Folder>> entrySet() {
		return folders.entrySet();
	}

	@Override
	public Folder get(Object key) {
		return folders.get(key);
	}

	@Override
	public boolean isEmpty() {
		return folders.isEmpty();
	}

	@Override
	public Set<FolderType> keySet() {
		return folders.keySet();
	}

	@Override
	public Folder put(FolderType key, Folder value) {
		return folders.put(key, value);
	}

	@Override
	public void putAll(Map<? extends FolderType, ? extends Folder> m) {
		folders.putAll(m);
	}

	@Override
	public Folder remove(Object key) {
		return folders.remove(key);
	}

	@Override
	public int size() {
		return folders.size();
	}

	@Override
	public Collection<Folder> values() {
		return folders.values();
	}

}
