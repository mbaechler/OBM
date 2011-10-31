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

import java.util.Collection;
import java.util.Set;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.obm.configuration.store.StoreNotFoundException;
import org.obm.push.bean.Credentials;
import org.obm.push.bean.Device;
import org.obm.push.bean.SyncCollection;

import bitronix.tm.TransactionManagerServices;

import com.google.common.collect.Sets;

public class MonitoredCollectionDaoEhcacheImplTest extends StoreManagerConfigurationTest {

	private ObjectStoreManager objectStoreManager;
	private MonitoredCollectionDaoEhcacheImpl monitoredCollectionStoreServiceImpl;
	private Credentials credentials;
	private TransactionManager transactionManager;
	
	@Before
	public void init() throws StoreNotFoundException, NotSupportedException, SystemException {
		this.transactionManager = TransactionManagerServices.getTransactionManager();
		transactionManager.begin();
		this.objectStoreManager = new ObjectStoreManager( super.initConfigurationServiceMock() );
		this.monitoredCollectionStoreServiceImpl = new MonitoredCollectionDaoEhcacheImpl(objectStoreManager);
		this.credentials = new Credentials("login@domain", "password");
	}
	
	@After
	public void cleanup() throws IllegalStateException, SecurityException, SystemException {
		transactionManager.rollback();
	}
	
	@Test
	public void testList() {
		Collection<SyncCollection> syncCollections = monitoredCollectionStoreServiceImpl.list(credentials, getFakeDeviceId());
		Assert.assertNotNull(syncCollections);
	}
	
	@Test
	public void testSimplePut() {
		monitoredCollectionStoreServiceImpl.put(credentials, getFakeDeviceId(), buildListCollection(1));
		Collection<SyncCollection> syncCollections = monitoredCollectionStoreServiceImpl.list(credentials, getFakeDeviceId());
		Assert.assertNotNull(syncCollections);
		Assert.assertEquals(1, syncCollections.size());
		containsCollectionWithId(syncCollections, 1);
	}
	
	@Test
	public void testPutNewItems() {
		monitoredCollectionStoreServiceImpl.put(credentials, getFakeDeviceId(), buildListCollection(1));
		monitoredCollectionStoreServiceImpl.put(credentials, getFakeDeviceId(), buildListCollection(2, 3));

		Collection<SyncCollection> syncCollections = monitoredCollectionStoreServiceImpl.list(credentials, getFakeDeviceId());
		Assert.assertNotNull(syncCollections);
		Assert.assertEquals(2, syncCollections.size());
		containsCollectionWithId(syncCollections, 2);
		containsCollectionWithId(syncCollections, 3);
		
	}
	
	private void containsCollectionWithId(
			Collection<SyncCollection> syncCollections, Integer id) {
		boolean find = false;
		for(SyncCollection col : syncCollections){
			if(col.getCollectionId().equals(id)){
				find = true;
			}
		}
		Assert.assertTrue(find);
	}

	private Set<SyncCollection> buildListCollection(Integer... ids) {
		Set<SyncCollection> cols = Sets.newHashSet();
		for(Integer id : ids){
			SyncCollection col = new SyncCollection();
			col.setCollectionId(id);
			cols.add(col);
		}
		return cols;
	}
	
	private Device getFakeDeviceId(){
		return new Device(1, "DevType", "DevId", null);
	}
}
