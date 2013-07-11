/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
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
