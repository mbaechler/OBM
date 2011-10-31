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
import java.util.List;

import org.minig.imap.sieve.SieveArg;
import org.minig.imap.sieve.SieveCommand;
import org.minig.imap.sieve.SieveResponse;

public class SieveDeleteScript extends SieveCommand<Boolean> {

	private String name;

	public SieveDeleteScript(String name) {
		this.name = name;
		retVal = false;
	}

	@Override
	protected List<SieveArg> buildCommand() {
		List<SieveArg> args = new ArrayList<SieveArg>(1);
		args.add(new SieveArg(("DELETESCRIPT \""+name+"\"").getBytes(), false));
		return args;
	}

	@Override
	public void responseReceived(List<SieveResponse> rs) {
		logger.info("listscripts response received.");
		if (commandSucceeded(rs)) {
			retVal = true;
		} else {
			for (SieveResponse sr : rs) {
				logger.error(sr.getData());
			}
		}
	}

}
