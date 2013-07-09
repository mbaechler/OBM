/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2013  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.dav.hc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

public class Response {

	private final HttpResponse response;
	private boolean consumed;

	public Response(HttpResponse response) {
		this.response = response;
	}

	private void assertNotConsumed() {
		if (consumed) {
			throw new IllegalStateException( "Response content has been already consumed");
		}
	}

	private void dispose() {
		if (consumed) {
			return;
		}
		try {
			EntityUtils.consume(response.getEntity());
		} catch (Exception ignore) {
		} finally {
			consumed = true;
		}
	}

	/**
	 * Discards response content and deallocates all resources associated with
	 * it.
	 */
	public void discardContent() {
		dispose();
	}

	/**
	 * Handles the response using the specified {@link ResponseHandler}
	 */
	public <T> T handleResponse(ResponseHandler<T> handler) throws ClientProtocolException, IOException {
		assertNotConsumed();
		try {
			return handler.handleResponse(response);
		} finally {
			dispose();
		}
	}

	public Content returnContent() throws ClientProtocolException, IOException {
		return handleResponse(new ContentResponseHandler());
	}

	public HttpResponse returnResponse() throws IOException {
		assertNotConsumed();
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				response.setEntity(new ByteArrayEntity(EntityUtils.toByteArray(entity), 
						ContentType.getOrDefault(entity)));
			}
			return response;
		} finally {
			consumed = true;
		}
	}

	public void saveContent(File file) throws IOException {
		assertNotConsumed();
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() >= 300) {
			throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
		}
		FileOutputStream out = new FileOutputStream(file);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				entity.writeTo(out);
			}
		} finally {
			consumed = true;
			out.close();
		}
	}
}
