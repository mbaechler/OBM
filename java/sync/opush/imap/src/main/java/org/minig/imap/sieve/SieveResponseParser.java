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

package org.minig.imap.sieve;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SieveResponseParser {

	private static final Logger logger = LoggerFactory
			.getLogger(SieveResponseParser.class);

	public void parse(List<SieveResponse> toFill, SieveMessage sm) {
		for (String l : sm.getLines()) {
			int idx = l.lastIndexOf("\r\n");
			String data = l.substring(0, idx);
			if (logger.isDebugEnabled()) {
				logger.debug("parsed: '" + data + "' len: " + data.length());
			}
			toFill.add(new SieveResponse(data));
		}
	}

}
