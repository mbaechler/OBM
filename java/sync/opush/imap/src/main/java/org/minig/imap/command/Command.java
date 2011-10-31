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
import java.util.concurrent.Semaphore;

import org.apache.mina.common.IoSession;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.MailboxNameUTF7Converter;
import org.minig.imap.impl.TagProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Command<T> implements ICommand<T> {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected T data;

	@Override
	public void execute(IoSession session, TagProducer tp, Semaphore lock,
			List<IMAPResponse> lastResponses) {
		CommandArgument args = buildCommand();

		String cmd = args.getCommandString();
		StringBuilder sb = new StringBuilder(10 + cmd.length());
		sb.append(tp.nextTag());
		sb.append(' ');
		sb.append(cmd);
		String sent = sb.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("=> '" + sent + "'");
		}
		session.write(sent);
		if (args.hasLiteralData()) {
			lock(lock);
			session.write(args.getLiteralData());
		}

	}

	private void lock(Semaphore lock) {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public T getReceivedData() {
		return data;
	}

	protected abstract CommandArgument buildCommand();

	protected static String toUtf7(String mailbox) {
		String ret = MailboxNameUTF7Converter.encode(mailbox);
		StringBuilder b = new StringBuilder(ret.length() + 2);
		b.append("\"");
		b.append(ret);
		b.append("\"");
		return b.toString();
	}

	protected boolean isOk(List<IMAPResponse> rs) {
		return rs.get(rs.size() - 1).isOk();
	}

	protected static String fromUtf7(String mailbox) {
		return MailboxNameUTF7Converter.decode(mailbox);
	}
}
