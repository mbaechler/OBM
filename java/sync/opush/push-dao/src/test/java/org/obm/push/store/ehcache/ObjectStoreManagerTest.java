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

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.obm.configuration.store.StoreNotFoundException;
import org.obm.push.store.ehcache.ObjectStoreManager;
import org.obm.push.store.ehcache.StoreManagerConfigurationTest;

public class ObjectStoreManagerTest extends StoreManagerConfigurationTest {

	private ObjectStoreManager opushCacheManager;

	public ObjectStoreManagerTest() {
		super();
	}
	
	@Before
	public void init() throws StoreNotFoundException {
		// by default, loading one store in objectStoreManager.xml
		this.opushCacheManager = new ObjectStoreManager( super.initConfigurationServiceMock() );
	}

	@Test
	public void loadStores() {
		List<String> stores = opushCacheManager.listStores();
		Assert.assertNotNull(stores);
		Assert.assertEquals(3, stores.size());
	}
	
	@Test
	public void createNewThreeCachesAndRemoveOne() {
		opushCacheManager.createNewStore("test 1");
		opushCacheManager.createNewStore("test 2");
		opushCacheManager.createNewStore("test 3");
		
		opushCacheManager.removeStore("test 2");
		
		Assert.assertNotNull(opushCacheManager.getStore("test 1"));
		Assert.assertNotNull(opushCacheManager.getStore("test 3"));

		Assert.assertNull(opushCacheManager.getStore("test 2"));
		
		Assert.assertEquals(5, opushCacheManager.listStores().size());
	}
	
	@Test
	public void createAndRemoveCache() {
		opushCacheManager.createNewStore("test 1");
		opushCacheManager.removeStore("test 1");
		
		Assert.assertNull(opushCacheManager.getStore("test 1"));
	}

	@Test
	public void createTwoIdenticalCache() {
		opushCacheManager.createNewStore("test 1");
		opushCacheManager.createNewStore("test 1");
		Assert.assertNotNull(opushCacheManager.getStore("test 1"));

		Assert.assertEquals(4, opushCacheManager.listStores().size());
	}

}
