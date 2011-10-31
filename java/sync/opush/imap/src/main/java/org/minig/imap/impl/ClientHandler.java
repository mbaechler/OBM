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


package org.minig.imap.impl;

import org.apache.mina.common.IoFilter;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.minig.imap.IMAPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends IoHandlerAdapter {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	private final IResponseCallback callback;

	private IoFilter getIoFilter() {
		ProtocolCodecFactory pcf = new IMAPCodecFactory();
		return new ProtocolCodecFilter(pcf);
	}

	public ClientHandler(IResponseCallback callback) {
		this.callback = callback;
	}

	public void sessionCreated(IoSession session) throws Exception {
		session.getFilterChain().addLast("codec", getIoFilter());
	}

	public void sessionOpened(IoSession session) throws Exception {
		callback.connected();
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		MinaIMAPMessage msg = (MinaIMAPMessage) message;
		callback.imapResponse(msg);
	}

	public void sessionClosed(IoSession session) throws Exception {
		callback.disconnected();
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error(cause.getMessage(),cause);
		callback.exceptionCaught(new IMAPException(cause.getMessage(),cause));
	}
	
	

}
