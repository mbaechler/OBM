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
package org.minig.imap.mime.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.obm.push.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AtomHelper {

	private final static Logger logger = LoggerFactory
			.getLogger(AtomHelper.class);

	public static final String getFullResponse(String resp, InputStream followUp) {
		String orig = resp;
		byte[] envelData = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		try {
			out.write(orig.getBytes());
			if (followUp != null) {
				FileUtils.transfer(followUp, out, true);
			}
		} catch (IOException e) {
			logger.error("error loading stream part of answer", e);
		}
		envelData = out.toByteArray();
		return new String(envelData, Charset.forName("ASCII"));
	}

}
