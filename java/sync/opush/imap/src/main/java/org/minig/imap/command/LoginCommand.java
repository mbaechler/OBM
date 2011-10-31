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

public class LoginCommand extends SimpleCommand<Boolean> {

	public LoginCommand(String login, String password) {
		super("LOGIN \"" + escapeString(login) + "\" \"" + escapeString(password)+"\"");
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) {
		data = rs.get(0).isOk();
	}

	private static String escapeString(String s) {
		StringBuilder ret = new StringBuilder(48);
		char[] toEsc = s.toCharArray();
		for (char c : toEsc) {
			if (c == '\\' || c == '"' ) {
				ret.append('\\');
			}
			ret.append(c);
		}
		return ret.toString();
	}
	
	
}
