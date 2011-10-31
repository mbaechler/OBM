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

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;

import org.obm.configuration.ObmConfigurationService;
import org.obm.configuration.store.StoreNotFoundException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ObjectStoreManager {

	private final static int MAX_ELEMENT_IN_MEMORY = 5000;
	private final CacheManager singletonManager;

	@Inject ObjectStoreManager(ObmConfigurationService configurationService) throws StoreNotFoundException {
		InputStream storeConfiguration = configurationService.getStoreConfiguration();
		this.singletonManager = new CacheManager(storeConfiguration);
	}

	public void createNewStore(String storeName) {
		if (getStore(storeName) == null) {
			this.singletonManager.addCache(createStore(storeName));
		}
	}

	private Cache createStore(String storeName) {
		return new Cache(createStoreConfiguration(storeName));
	}

	private CacheConfiguration createStoreConfiguration(String storeName) {
		return new CacheConfiguration(storeName, MAX_ELEMENT_IN_MEMORY);
	}

	public Cache getStore(String storeName) {
		return this.singletonManager.getCache(storeName);
	}

	public void removeStore(String storeName) {
		this.singletonManager.removeCache(storeName);
	}

	public List<String> listStores() {
		return Arrays.asList(this.singletonManager.getCacheNames());
	}

}
