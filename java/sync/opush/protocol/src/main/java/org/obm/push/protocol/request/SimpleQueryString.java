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
package org.obm.push.protocol.request;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;


public class SimpleQueryString extends AbstractActiveSyncRequest implements ActiveSyncRequest {

	public SimpleQueryString(HttpServletRequest r) {
		super(r);
	}

	@Override
	public String getParameter(String key) {
		return request.getParameter(key);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	@Override
	public String getHeader(String name) {
		return request.getHeader(name);
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return request;
	}

	@Override
	public String getDeviceId() {
		return p("DeviceId");
	}

	@Override
	public String getDeviceType() {
		String deviceType = p("DeviceType");
		if (deviceType.startsWith("IMEI")) {
			return p("User-Agent");
		}
		return deviceType;
	}
	
	@Override
	public String getCommand() {
		return p("Cmd");
	}

}
