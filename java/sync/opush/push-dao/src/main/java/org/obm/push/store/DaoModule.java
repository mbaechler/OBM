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
package org.obm.push.store;

import org.obm.dbcp.DBCP;
import org.obm.dbcp.IDBCP;
import org.obm.push.store.ehcache.MonitoredCollectionDaoEhcacheImpl;
import org.obm.push.store.ehcache.SyncedCollectionDaoEhcacheImpl;
import org.obm.push.store.ehcache.UnsynchronizedItemDaoEhcacheImpl;
import org.obm.push.store.jdbc.CollectionDaoJdbcImpl;
import org.obm.push.store.jdbc.DeviceDaoJdbcImpl;
import org.obm.push.store.jdbc.EmailDaoJdbcImpl;
import org.obm.push.store.jdbc.FiltrageInvitationDaoJdbcImpl;
import org.obm.push.store.jdbc.HearbeatDaoJdbcDaoImpl;

import com.google.inject.AbstractModule;

public class DaoModule extends AbstractModule{

	@Override
	protected void configure() {

		bind(IDBCP.class).to(DBCP.class);
		bind(CollectionDao.class).to(CollectionDaoJdbcImpl.class);
		bind(DeviceDao.class).to(DeviceDaoJdbcImpl.class);
		bind(EmailDao.class).to(EmailDaoJdbcImpl.class);
		bind(FiltrageInvitationDao.class).to(FiltrageInvitationDaoJdbcImpl.class);
		bind(HearbeatDao.class).to(HearbeatDaoJdbcDaoImpl.class);
		bind(MonitoredCollectionDao.class).to(MonitoredCollectionDaoEhcacheImpl.class);
		bind(SyncedCollectionDao.class).to(SyncedCollectionDaoEhcacheImpl.class);
		bind(UnsynchronizedItemDao.class).to(UnsynchronizedItemDaoEhcacheImpl.class);
	}

}
