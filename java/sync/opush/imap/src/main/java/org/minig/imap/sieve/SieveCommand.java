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

import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SieveCommand<T> {

	protected static final Logger logger = LoggerFactory
			.getLogger(SieveCommand.class);
	
	protected T retVal;
	private static final byte[] CRLF = "\r\n".getBytes();

	public void execute(IoSession session) {

		List<SieveArg> cmd = buildCommand();

		for (int i = 0; i < cmd.size(); i++) {
			SieveArg arg = cmd.get(i);
			if (!arg.isLiteral()) {
				StringBuilder sb = new StringBuilder(new String(arg.getRaw()));
				if (i < cmd.size() - 1 && cmd.get(i + 1).isLiteral()) {
					SieveArg next = cmd.get(i + 1);
					sb.append(" {");
					sb.append(next.getRaw().length);
					sb.append("+}");
				}

				session.write(sb.toString().getBytes());
			} else {
				session.write(arg.getRaw());
			}
			session.write(CRLF);
		}
	}

	public abstract void responseReceived(List<SieveResponse> rs);

	protected abstract List<SieveArg> buildCommand();

	protected boolean commandSucceeded(List<SieveResponse> rs) {
		return rs.size() > 0 && rs.get(0).getData().endsWith("OK");
	}

	protected void reportErrors(List<SieveResponse> rs) {
		for (SieveResponse sr : rs) {
			logger.error(sr.getData());
		}
	}

	public T getReceivedData() {
		return retVal;
	}

}
