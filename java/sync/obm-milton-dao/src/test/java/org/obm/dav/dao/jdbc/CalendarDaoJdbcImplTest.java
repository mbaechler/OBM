/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2013  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.dav.dao.jdbc;

import static fr.aliacom.obm.ToolBox.getDefaultObmDomain;
import static fr.aliacom.obm.ToolBox.getDefaultObmUser;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.obm.dao.utils.DaoTestModule;
import org.obm.dao.utils.H2InMemoryDatabase;
import org.obm.dav.dao.CalendarDao;
import org.obm.dav.dao.exception.DaoException;
import org.obm.dav.dao.exception.MappingNotFoundException;
import org.obm.filter.Slow;
import org.obm.guice.GuiceModule;
import org.obm.guice.SlowGuiceRunner;
import org.obm.sync.calendar.EventExtId;

import com.google.inject.Inject;

import fr.aliacom.obm.common.user.ObmUser;

@Slow
@RunWith(SlowGuiceRunner.class)
@GuiceModule(CalendarDaoJdbcImplTest.Env.class)
public class CalendarDaoJdbcImplTest {

	public static class Env extends DaoTestModule {

		@Override protected String initialSqlSchemaPath() {
			return "sql/initialCalendarDao.sql";
		}

		@Override protected void configureImpl() {
			bind(CalendarDao.class).to(CalendarDaoJdbcImpl.class);
		}
	}

	@Inject @Rule public H2InMemoryDatabase db;
	@Inject CalendarDao dao;

	private ObmUser user;
	
	@Before
	public void setUp() throws Exception {
		user = getDefaultObmUser();
		createUser(user);
	}
	
	private void createUser(ObmUser obmUser) throws Exception {
		db.executeUpdate("INSERT INTO userobm (userobm_id, userobm_domain_id, userobm_archive, userobm_login, userobm_password_type, userobm_password) " +
				"VALUES ('" + obmUser.getUid() + "', 1, 0, '" + obmUser.getLogin() + "', 'PLAIN', '" + obmUser.getLogin() + "')");
	}

	@Test
	public void testOneInsert() throws Exception {
		EventExtId extId = new EventExtId("123");
		String clientEventId = "456";
		
		dao.insertMapping(user, extId, clientEventId);
		String foundClientEventId = dao.getClientEventId(user, extId);
		EventExtId foundExtId = dao.getEventExtId(user, clientEventId);
		
		assertThat(foundClientEventId).isEqualTo(clientEventId);
		assertThat(foundExtId).isEqualTo(extId);
	}

	@Test
	public void testTwoInsert() throws Exception {
		EventExtId extId = new EventExtId("123");
		String clientEventId = "456";
		EventExtId extId2 = new EventExtId("12345");
		String clientEventId2 = "45678";
		
		dao.insertMapping(user, extId, clientEventId);
		dao.insertMapping(user, extId2, clientEventId2);
		String foundClientEventId = dao.getClientEventId(user, extId);
		EventExtId foundExtId = dao.getEventExtId(user, clientEventId);
		String foundClientEventId2 = dao.getClientEventId(user, extId2);
		EventExtId foundExtId2 = dao.getEventExtId(user, clientEventId2);
		
		assertThat(foundClientEventId).isEqualTo(clientEventId);
		assertThat(foundExtId).isEqualTo(extId);
		assertThat(foundClientEventId2).isEqualTo(clientEventId2);
		assertThat(foundExtId2).isEqualTo(extId2);
	}

	@Test(expected=DaoException.class)
	public void testTwiceSameInserts() throws Exception {
		EventExtId extId = new EventExtId("123");
		String clientEventId = "456";
		
		dao.insertMapping(user, extId, clientEventId);
		dao.insertMapping(user, extId, clientEventId);
	}

	@Test(expected=DaoException.class)
	public void testTwoDistincInsertsButSameClientId() throws Exception {
		EventExtId extId1 = new EventExtId("123");
		EventExtId extId2 = new EventExtId("123");
		String clientEventId = "456";
		
		dao.insertMapping(user, extId1, clientEventId);
		dao.insertMapping(user, extId2, clientEventId);
	}
	
	@Test(expected=MappingNotFoundException.class)
	public void testGetExtIdOfNonExistentMapping() throws Exception {
		dao.getEventExtId(user, "456");
	}
	
	@Test(expected=MappingNotFoundException.class)
	public void testGetClientIdOfNonExistentMapping() throws Exception {
		dao.getClientEventId(user, new EventExtId("123"));
	}
	
	@Test(expected=MappingNotFoundException.class)
	public void testGetClientIdOfExistentMappingButOtherUser() throws Exception {
		EventExtId extId = new EventExtId("123");
		String clientEventId = "456";
		
		ObmUser otherUser = ObmUser.builder()
				.uid(50)
				.entityId(20)
				.login("otheruser")
				.domain(getDefaultObmDomain())
				.emailAndAliases("otheruser@test")
				.firstName("otheruser name")
				.lastName("otheruser lastname")
				.build();
		createUser(otherUser);

		dao.insertMapping(user, extId, clientEventId);
		dao.getClientEventId(otherUser, extId);
	}
	
	@Test(expected=MappingNotFoundException.class)
	public void testGetExtIdOfExistentMappingButOtherUser() throws Exception {
		EventExtId extId = new EventExtId("123");
		String clientEventId = "456";
		
		ObmUser otherUser = ObmUser.builder()
				.uid(50)
				.entityId(20)
				.login("otheruser")
				.domain(getDefaultObmDomain())
				.emailAndAliases("otheruser@test")
				.firstName("otheruser name")
				.lastName("otheruser lastname")
				.build();
		createUser(otherUser);

		dao.insertMapping(user, extId, clientEventId);
		dao.getEventExtId(otherUser, clientEventId);
	}

	@Ignore("H2 accepts bigger value than allowed by : character varying(300)")
	@Test(expected=DaoException.class)
	public void testTooBigExtId() throws Exception {
		UUID regularExtId = UUID.randomUUID();
		EventExtId tooBigExtId = new EventExtId(regularExtId.toString() + regularExtId.toString());
		String clientEventId = "456";
		
		dao.insertMapping(user, tooBigExtId, clientEventId);
	}
}
