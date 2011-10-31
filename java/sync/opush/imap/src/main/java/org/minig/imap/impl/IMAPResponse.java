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

import java.io.InputStream;

public class IMAPResponse {

	private String status;
	private boolean clientDataExpected;
	private String payload;
	private String tag;
	private InputStream streamData;

	public IMAPResponse() {
	}

	public IMAPResponse(String status, String payload) {
		setStatus(status);
		setPayload(payload);
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isOk() {
		return "OK".equals(status);
	}

	public boolean isNo() {
		return "NO".equals(status);
	}

	public boolean isBad() {
		return "BAD".equals(status);
	}

	public boolean isClientDataExpected() {
		return clientDataExpected;
	}
	
	public boolean isContinuation() {
		return "+".equals(tag);
	}

	public void setClientDataExpected(boolean clientDataExpected) {
		this.clientDataExpected = clientDataExpected;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public InputStream getStreamData() {
		return streamData;
	}

	public void setStreamData(InputStream streamData) {
		this.streamData = streamData;
	}
}
