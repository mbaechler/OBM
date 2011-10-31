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
package org.minig.imap.tls;

import javax.net.ssl.SSLContext;

import org.apache.mina.filter.SSLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinigTLSFilter extends SSLFilter {

	private static final Logger logger = LoggerFactory
			.getLogger(MinigTLSFilter.class);
	
	private static SSLContext CTX;

	static {
		try {
			CTX = BogusSSLContextFactory.getInstance(false);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	public MinigTLSFilter() {
		super(CTX);
	}

}
