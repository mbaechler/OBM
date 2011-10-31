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

import java.util.List;

import org.minig.imap.impl.IMAPResponse;

public class StartIdleCommand extends Command<Boolean> {

	public StartIdleCommand() {
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) {
	}

	@Override
	protected CommandArgument buildCommand() {
		String cmd = "IDLE";
		if (logger.isDebugEnabled()) {
			logger.debug("cmd: " + cmd);
		}
		return new CommandArgument(cmd, null);
	}

}
