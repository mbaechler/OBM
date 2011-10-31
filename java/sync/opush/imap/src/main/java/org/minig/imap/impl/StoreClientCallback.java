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

import java.util.ArrayList;
import java.util.LinkedList;

import org.minig.imap.IMAPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreClientCallback implements IResponseCallback {

	private final static Logger logger = LoggerFactory
			.getLogger(StoreClientCallback.class);

	IMAPResponseParser rParser;
	private LinkedList<IMAPResponse> responses;
	private ClientSupport client;

	public StoreClientCallback() {
		this.rParser = new IMAPResponseParser();
		this.responses = new LinkedList<IMAPResponse>();
	}

	@Override
	public void connected() {
		logger.debug("connected() callback called.");
		rParser.setServerHelloReceived(false);
	}

	@Override
	public void disconnected() {
		logger.debug("disconnected() callback called.");
	}

	@Override
	public void imapResponse(MinaIMAPMessage imapResponse) {
		IMAPResponse rp = null;
		try {
			rp = rParser.parse(imapResponse);
		} catch (RuntimeException re) {
			logger.error("Runtime exception on: " + imapResponse);
			throw re;
		}

		responses.add(rp);

		if (rp.isClientDataExpected()) {
			ArrayList<IMAPResponse> rs = new ArrayList<IMAPResponse>(responses.size());
			rs.addAll(responses);
			responses.clear();
			client.setResponses(rs);
		}
	}

	@Override
	public void setClient(ClientSupport cs) {
		this.client = cs;
	}

	@Override
	public void exceptionCaught(IMAPException cause) throws IMAPException {
		throw cause;
	}

}
