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
package org.obm.push;

import java.math.BigDecimal;

import org.obm.push.bean.BackendSession;
import org.obm.push.bean.Credentials;
import org.obm.push.bean.Device;
import org.obm.push.exception.DaoException;
import org.obm.push.protocol.request.ActiveSyncRequest;
import org.obm.push.store.DeviceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SessionService {

	private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
	private final DeviceDao deviceDao;
	
	@Inject
	private SessionService(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
	
	public BackendSession getSession(
			Credentials credentials, String devId, ActiveSyncRequest request) throws DaoException {

		String sessionId = credentials.getUser().getLoginAtDomain() + "/" + devId;
		return createSession(credentials, request, sessionId);
	}

	private BackendSession createSession(Credentials credentials,
			ActiveSyncRequest r, String sessionId) throws DaoException {
		
		String userAgent = r.getUserAgent();
		String devId = r.getDeviceId();
		
		Device device = deviceDao.getDevice(credentials.getUser(), devId, userAgent);
		
		BackendSession bs = new BackendSession(credentials, 
				r.getCommand(), device, getProtocolVersion(r));
		
		
		logger.debug("New session = {}", sessionId);
		return bs;
	}

	private BigDecimal getProtocolVersion(ActiveSyncRequest request) {
		final String proto = request.getMSASProtocolVersion();
		if (proto != null) {
			try {
				BigDecimal protocolVersion = new BigDecimal(proto);
				logger.debug("Client supports protocol = {}", protocolVersion);
				return protocolVersion;
			} catch (NumberFormatException nfe) {
				logger.warn("invalid MS-ASProtocolVersion = {}", proto);
			}
		}
		return new BigDecimal("12.1");
	}

}
