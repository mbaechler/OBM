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
package org.obm.push.store.ehcache;

import java.io.Serializable;
import java.util.Collection;

import net.sf.ehcache.Element;

import org.obm.push.bean.Credentials;
import org.obm.push.bean.Device;
import org.obm.push.bean.SyncCollection;
import org.obm.push.store.SyncedCollectionDao;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SyncedCollectionDaoEhcacheImpl extends AbstractEhcacheDao implements SyncedCollectionDao {

	private static final String STORE_NAME = "syncedCollectionStoreService";
	
	@Inject  SyncedCollectionDaoEhcacheImpl(
			ObjectStoreManager objectStoreManager) {
		super(objectStoreManager);
	}

	@Override
	protected String getStoreName() {
		return STORE_NAME;
	}

	@Override
	public void put(Credentials credentials, Device device,
			Collection<SyncCollection> collections) {
		for(SyncCollection collection : collections){
			put(credentials, device, collection);
		}
	}

	private void put(Credentials credentials, Device device, SyncCollection collection) {
		Key key = buildKey(credentials, device, collection.getCollectionId());
		store.put( new Element(key, collection) );
	}
	
	@Override
	public SyncCollection get(Credentials credentials, Device device, Integer collectionId) {
		Key key = buildKey(credentials, device, collectionId);
		Element element = store.get(key);
		if (element != null) {
			return (SyncCollection) element.getValue();
		} else {
			return null;
		}
	}
	
	private Key buildKey(Credentials credentials, Device device, Integer collectionId) {
		return new Key(credentials, device, collectionId);
	}
	
	private static class Key implements Serializable {

		private final Credentials credentials;
		private final int collectionId;
		private final Device device;

		public Key(Credentials credentials, Device device, int collectionId) {
			super();
			this.credentials = credentials;
			this.device = device;
			this.collectionId = collectionId;
		}

		@Override
		public int hashCode(){
			return Objects.hashCode(credentials, collectionId, device);
		}
		
		@Override
		public boolean equals(Object object){
			if (object instanceof Key) {
				Key that = (Key) object;
				return Objects.equal(this.credentials, that.credentials)
					&& Objects.equal(this.collectionId, that.collectionId)
					&& Objects.equal(this.device, that.device);
			}
			return false;
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this)
				.add("credentials", credentials)
				.add("collectionId", collectionId)
				.add("device", device)
				.toString();
		}
		
	}
}
