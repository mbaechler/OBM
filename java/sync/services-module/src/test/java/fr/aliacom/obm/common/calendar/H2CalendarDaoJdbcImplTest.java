/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2013  Linagora
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for OBM
 * software by Linagora pursuant to Section 7 of the GNU Affero General Public
 * License, subsections (b), (c), and (e), pursuant to which you must notably (i)
 * retain the displaying by the interactive user interfaces of the “OBM, Free
 * Communication by Linagora” Logo with the “You are using the Open Source and
 * free version of OBM developed and supported by Linagora. Contribute to OBM R&D
 * by subscribing to an Enterprise offer !” infobox, (ii) retain all hypertext
 * links between OBM and obm.org, between Linagora and linagora.com, as well as
 * between the expression “Enterprise offer” and pro.obm.org, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for OBM along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General   Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to the OBM software.
 * ***** END LICENSE BLOCK ***** */
package fr.aliacom.obm.common.calendar;

import static fr.aliacom.obm.ToolBox.getDefaultObmUser;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.fest.assertions.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;

import org.easymock.IMocksControl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.obm.dao.utils.H2ConnectionProvider;
import org.obm.dao.utils.H2InMemoryDatabase;
import org.obm.dbcp.DatabaseConnectionProvider;
import org.obm.filter.Slow;
import org.obm.guice.GuiceModule;
import org.obm.guice.SlowGuiceRunner;
import org.obm.icalendar.Ical4jHelper;
import org.obm.sync.calendar.EventExtId;
import org.obm.sync.solr.SolrHelper;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Names;
import com.mysql.jdbc.PreparedStatement;

import fr.aliacom.obm.common.user.ObmUser;
import fr.aliacom.obm.utils.ObmHelper;

@Slow
@RunWith(SlowGuiceRunner.class)
@GuiceModule(H2CalendarDaoJdbcImplTest.Env.class)
public class H2CalendarDaoJdbcImplTest {

	public static class Env extends AbstractModule {
		private IMocksControl mocksControl = createControl();

		@Override
		protected void configure() {
			bind(IMocksControl.class).toInstance(mocksControl);
			bindConstant().annotatedWith(Names.named("initialSchema")).to("sql/initial.sql");

			bindWithMock(SolrHelper.Factory.class);
			bindWithMock(ObmHelper.class);
			bindWithMock(Ical4jHelper.class);
			bind(DatabaseConnectionProvider.class).to(H2ConnectionProvider.class);
			bind(CalendarDao.class).to(CalendarDaoJdbcImpl.class);
		}

		private <T> void bindWithMock(Class<T> cls) {
			bind(cls).toInstance(mocksControl.createMock(cls));
		}
	}

	@Inject
	private IMocksControl mocksControl;
	@Inject
	private ObmHelper helper;
	@Inject
	private CalendarDao dao;

	@Rule
	@Inject
	public H2InMemoryDatabase db;
	
	@Test
	public void testDoesntEventExists() throws Exception {
		EventExtId extId = new EventExtId("123");
		
		Connection connection = db.getConnection();
		expect(helper.getConnection()).andReturn(connection).anyTimes();
		helper.cleanup(eq(connection), anyObject(PreparedStatement.class), anyObject(ResultSet.class));
		
		mocksControl.replay();
		boolean exists = dao.doesEventExist(getDefaultObmUser(), extId);
		
		mocksControl.verify();
		assertThat(exists).isFalse();
	}
	
	@Test
	public void testDoesEventExists() throws Exception {
		ObmUser defaultObmUser = getDefaultObmUser();
		db.executeUpdate("INSERT INTO entity (entity_mailing) VALUES (true), (true), (true)");
		
		db.executeUpdate("INSERT INTO domain (domain_name, domain_uuid, domain_label) " +
				"VALUES ('test.tlse.lng', 'ac21bc0c-f816-4c52-8bb9-e50cfbfec5b6', 'test.tlse.lng')");
		db.executeUpdate("INSERT INTO domainentity (domainentity_entity_id, domainentity_domain_id) VALUES (1, 1)");
		
		db.executeUpdate("INSERT INTO userobm (userobm_id, userobm_domain_id, userobm_archive, userobm_login, userobm_password_type, userobm_password) " +
				"VALUES (1, 1, 0, '" + defaultObmUser.getLogin() + "', 'PLAIN', 'usera')");
		db.executeUpdate("INSERT INTO userentity (userentity_entity_id, userentity_user_id) VALUES (2, 1)");
		
		db.executeUpdate("INSERT INTO event (event_id, event_domain_id, event_ext_id) " +
				"VALUES (1, 1, '123')");
		db.executeUpdate("INSERT INTO eventlink (eventlink_event_id, eventlink_entity_id) VALUES (1, 2)");
		
		EventExtId extId = new EventExtId("123");
		
		Connection connection = db.getConnection();
		expect(helper.getConnection()).andReturn(connection).anyTimes();
		helper.cleanup(eq(connection), anyObject(PreparedStatement.class), anyObject(ResultSet.class));
		
		mocksControl.replay();
		boolean exists = dao.doesEventExist(defaultObmUser, extId);
		
		mocksControl.verify();
		assertThat(exists).isTrue();
	}
}
