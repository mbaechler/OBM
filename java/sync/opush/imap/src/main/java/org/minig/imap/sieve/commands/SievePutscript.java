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

package org.minig.imap.sieve.commands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.minig.imap.sieve.SieveArg;
import org.minig.imap.sieve.SieveCommand;
import org.minig.imap.sieve.SieveResponse;
import org.obm.push.utils.FileUtils;

public class SievePutscript extends SieveCommand<Boolean> {

	private String name;
	private byte[] data;

	public SievePutscript(String name, InputStream scriptContent) {
		retVal = false;
		this.name = name;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			FileUtils.transfer(scriptContent, out, true);
			this.data = out.toByteArray();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Override
	protected List<SieveArg> buildCommand() {
		List<SieveArg> args = new ArrayList<SieveArg>(1);
		args
				.add(new SieveArg(("PUTSCRIPT \"" + name + "\"").getBytes(),
						false));
		args.add(new SieveArg(data, true));
		return args;
	}

	@Override
	public void responseReceived(List<SieveResponse> rs) {
		logger.info("putscript response received.");
		if (commandSucceeded(rs)) {
			retVal = true;
		} else {
			for (SieveResponse sr : rs) {
				logger.error(sr.getData());
			}
		}
	}

}
