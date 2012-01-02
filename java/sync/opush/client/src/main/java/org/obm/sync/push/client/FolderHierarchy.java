package org.obm.sync.push.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Objects;

public final class FolderHierarchy implements Map<FolderType, Folder> {

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

	@Override
	public int hashCode(){
		return Objects.hashCode(folders);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof FolderHierarchy) {
			FolderHierarchy that = (FolderHierarchy) object;
			return Objects.equal(this.folders, that.folders);
		}
		return false;
	}

}
