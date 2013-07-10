/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2011-2012  Linagora
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.obm.dav.dao.CalendarDao;
import org.obm.dav.dao.exception.DaoException;
import org.obm.dav.dao.exception.MappingNotFoundException;
import org.obm.dbcp.DatabaseConnectionProvider;
import org.obm.push.utils.JDBCUtils;
import org.obm.sync.calendar.EventExtId;

import com.google.common.hash.Hashing;
import com.google.inject.Inject;

import fr.aliacom.obm.common.user.ObmUser;

public class CalendarDaoJdbcImpl implements CalendarDao {

	private final DatabaseConnectionProvider dbcp;

	@Inject
	private CalendarDaoJdbcImpl(DatabaseConnectionProvider dbcp) {
		this.dbcp = dbcp;
	}
	
	@Override
	public EventExtId getEventExtId(ObmUser user, String clientEventId) throws DaoException, MappingNotFoundException {
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		try{
			con = dbcp.getConnection();
			ps = con.prepareStatement(
					"    SELECT event_ext_id " +
					"      FROM dav_event_mapping " +
					"INNER JOIN UserObm ON userobm_id=owner_id " +
					"     WHERE event_client_id=?" +
					"       AND userobm_id=?");
			ps.setString(1, clientEventId);
			ps.setInt(2, user.getUid());
			rs = ps.executeQuery();
			if (rs.next()) {
				return new EventExtId(rs.getString(1));
			}
			throw new MappingNotFoundException(user, clientEventId);
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			JDBCUtils.cleanup(con, ps, rs);
		}
	}
	
	@Override
	public String getClientEventId(ObmUser user, EventExtId eventExtId) throws DaoException, MappingNotFoundException {
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		try{
			con = dbcp.getConnection();
			ps = con.prepareStatement(
					"    SELECT event_client_id " +
					"      FROM dav_event_mapping " +
					"INNER JOIN UserObm ON userobm_id=owner_id " +
					"     WHERE event_ext_id=?" +
					"       AND userobm_id=?");
			ps.setString(1, eventExtId.getExtId());
			ps.setInt(2, user.getUid());
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
			throw new MappingNotFoundException(user, eventExtId);
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			JDBCUtils.cleanup(con, ps, rs);
		}
	}
	
	@Override
	public void insertMapping(ObmUser user, EventExtId eventExtId, String clientEventId) throws DaoException {
		
		Connection con = null;
		PreparedStatement ps = null;
		try{
			con = dbcp.getConnection();
			ps = con.prepareStatement(
					"INSERT INTO dav_event_mapping (" +
					"owner_id, " +
					"event_ext_id, " +
					"event_ext_id_hash, " +
					"event_client_id, " +
					"event_client_id_hash)" +
					" VALUES (?, ?, ?, ?, ?)");
			int i = 1;
			ps.setInt(i++, user.getUid());
			ps.setString(i++, eventExtId.getExtId());
			ps.setBytes(i++, Hashing.sha1().hashString(eventExtId.getExtId()).asBytes());
			ps.setString(i++, clientEventId);
			ps.setBytes(i++, Hashing.sha1().hashString(clientEventId).asBytes());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			JDBCUtils.cleanup(con, ps, null);
		}
	}
}
