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

import javax.servlet.http.HttpServletRequest;


public abstract class AbstractActiveSyncRequest implements ActiveSyncRequest {
	
	protected final HttpServletRequest request;
	
	protected AbstractActiveSyncRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	protected String p(String name) {
		String ret = getParameter(name);
		if (ret == null) {
			ret = getHeader(name);
		}
		return ret;
	}
	
	@Override
	public String getUserAgent() {
		return request.getHeader("User-Agent");
	}

	@Override
	public String getMsPolicyKey() {
		return request.getHeader("X-Ms-PolicyKey");
	}
	
	@Override
	public String getMSASProtocolVersion() {
		return request.getHeader("MS-ASProtocolVersion");
	}
}
