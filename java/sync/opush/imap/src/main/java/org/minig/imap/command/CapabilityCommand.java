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

package org.minig.imap.command;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.minig.imap.impl.IMAPResponse;

public class CapabilityCommand extends SimpleCommand<Set<String>> {

	public CapabilityCommand() {
		super("CAPABILITY");
	}


	@Override
	public void responseReceived(List<IMAPResponse> rs) {
		data = new HashSet<String>();
		String[] parts = rs.get(0).getPayload().split(" ");
		for (int i = 2; i < parts.length; i++) {
			data.add(parts[i]);
		}
	}

}
