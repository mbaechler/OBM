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

package org.minig.imap.idle;

import java.util.ArrayList;
import java.util.LinkedList;

import org.minig.imap.IMAPException;
import org.minig.imap.impl.ClientSupport;
import org.minig.imap.impl.IMAPResponse;
import org.minig.imap.impl.IMAPResponseParser;
import org.minig.imap.impl.IResponseCallback;
import org.minig.imap.impl.MinaIMAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdleClientCallback implements IResponseCallback {

	private static final Logger logger = LoggerFactory
			.getLogger(IdleClientCallback.class);
	
	private LinkedList<IMAPResponse> responses;
	private IIdleCallback observer;
	private IdleResponseParser rParser;
	private IMAPResponseParser imrParser;
	private ClientSupport cs;
	private Boolean isStart;

	public IdleClientCallback() {
		this.rParser = new IdleResponseParser();
		this.imrParser = new IMAPResponseParser();
		isStart = false;
		this.responses = new LinkedList<IMAPResponse>();
	}

	@Override
	public void connected() {
		logger.debug("connected() callback called.");
		imrParser.setServerHelloReceived(false);
	}

	@Override
	public void disconnected() {
		logger.debug("disconnected() callback called.");
		if (observer != null) {
			observer.disconnectedCallBack();
		}
		this.isStart = false;
	}

	@Override
	public void imapResponse(MinaIMAPMessage imapResponse) {
		if (isStart) {
			if (observer != null) {
				IdleLine rp = null;
				try {
					rp = rParser.parse(imapResponse);
				} catch (RuntimeException re) {
					logger.warn("Runtime exception on: " + imapResponse);
					throw re;
				}
				observer.receive(rp);
			}
		} else {
			IMAPResponse rp = null;
			try {

				rp = imrParser.parse(imapResponse);
			} catch (RuntimeException re) {
				logger.warn("Runtime exception on: " + imapResponse);
				throw re;
			}
			responses.add(rp);
			if (rp.isClientDataExpected()) {
				ArrayList<IMAPResponse> rs = new ArrayList<IMAPResponse>(
						responses.size());
				rs.addAll(responses);
				responses.clear();
				cs.setResponses(rs);
			}
		}
		if (imapResponse.getMessageLine().toLowerCase().startsWith("+ idling")) {
			isStart = true;
		}
	}

	public void attachIdleCallback(IIdleCallback observer) {
		this.observer = observer;
	}

	public void setClient(ClientSupport cs) {
		this.cs = cs;
	}

	public boolean isStart() {
		return isStart;
	}

	public void stopIdle() {
		this.isStart = false;
	}

	@Override
	public void exceptionCaught(IMAPException cause) throws IMAPException {
		logger.error(cause.getMessage(), cause);
	}

	public void detachIdleCallback() {
		this.observer = null;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

}
