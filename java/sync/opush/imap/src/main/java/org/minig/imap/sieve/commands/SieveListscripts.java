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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.minig.imap.sieve.SieveArg;
import org.minig.imap.sieve.SieveCommand;
import org.minig.imap.sieve.SieveResponse;
import org.minig.imap.sieve.SieveScript;

public class SieveListscripts extends SieveCommand<List<SieveScript>> {

	public SieveListscripts() {
		retVal = new LinkedList<SieveScript>();
	}

	@Override
	protected List<SieveArg> buildCommand() {
		List<SieveArg> args = new ArrayList<SieveArg>(1);
		args.add(new SieveArg("LISTSCRIPTS".getBytes(), false));
		return args;
	}

	@Override
	public void responseReceived(List<SieveResponse> rs) {
		if (commandSucceeded(rs)) {
			String[] list = rs.get(0).getData().split("\r\n");
			for (int i = 0; i < list.length - 1; i++) {
				boolean active = list[i].endsWith("ACTIVE");
				int idx = list[i].lastIndexOf("\"");
				if (idx > 0) {
					String name = list[i].substring(1, idx);
					retVal.add(new SieveScript(name, active));
				} else {
					logger.warn("receveid from listscripts: '" + list[i] + "'");
				}
			}
		} else {
			reportErrors(rs);
		}
		logger
				.info("returning a list of " + retVal.size()
						+ " sieve script(s)");
	}

}
