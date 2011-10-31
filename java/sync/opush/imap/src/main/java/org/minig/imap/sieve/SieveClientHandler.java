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

import java.util.ArrayList;

import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SieveClientHandler extends IoHandlerAdapter {

	private static final Logger logger = LoggerFactory
			.getLogger(SieveClientHandler.class);

	private SieveResponseParser srp = new SieveResponseParser();

	private SieveClientSupport cs;

	private IoFilter getSieveFilter() {
		ProtocolCodecFactory pcf = new SieveCodecFactory();
		return new ProtocolCodecFilter(pcf);
	}

	public SieveClientHandler(SieveClientSupport cb) {
		// TODO Auto-generated constructor stub
		this.cs = cb;
	}

	public void sessionCreated(IoSession session) throws Exception {
		session.getFilterChain().addLast("codec", getSieveFilter());
	}

	public void sessionOpened(IoSession session) throws Exception {
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		SieveMessage msg = (SieveMessage) message;
		ArrayList<SieveResponse> copy = new ArrayList<SieveResponse>(10);
		srp.parse(copy, msg);
		cs.setResponses(copy);
	}

	public void sessionClosed(IoSession session) throws Exception {
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error(cause.getMessage(), cause);
		throw new SieveException(cause.getMessage(), cause);
	}

}
