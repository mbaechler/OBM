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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.google.common.base.Preconditions;

public class Request {

	public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	public static final Locale DATE_LOCALE = Locale.US;
	public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT");

	private final HttpRequestBase request;
	private final HttpParams localParams;

	public static Request Get(URI uri) {
		return new Request(new HttpGet(uri));
	}

	public static Request Get(String uri) {
		return new Request(new HttpGet(uri));
	}

	public static Request Head(URI uri) {
		return new Request(new HttpHead(uri));
	}

	public static Request Head(String uri) {
		return new Request(new HttpHead(uri));
	}

	public static Request Post(URI uri) {
		return new Request(new HttpPost(uri));
	}

	public static Request Post(String uri) {
		return new Request(new HttpPost(uri));
	}

	public static Request Put(URI uri) {
		return new Request(new HttpPut(uri));
	}

	public static Request Put(String uri) {
		return new Request(new HttpPut(uri));
	}

	public static Request Trace(URI uri) {
		return new Request(new HttpTrace(uri));
	}

	public static Request Trace(String uri) {
		return new Request(new HttpTrace(uri));
	}

	public static Request Delete(URI uri) {
		return new Request(new HttpDelete(uri));
	}

	public static Request Delete(String uri) {
		return new Request(new HttpDelete(uri));
	}

	public static Request Options(URI uri) {
		return new Request(new HttpOptions(uri));
	}

	public static Request Options(String uri) {
		return new Request(new HttpOptions(uri));
	}

	public static Request Propfind(String uri) {
		return new Request(new HttpPropFind(uri));
	}

	private Request(HttpRequestBase request) {
		this.request = request;
		localParams = request.getParams();
	}

	public HttpRequestBase getHttpRequest() {
		return request;
	}

	public Response execute() throws ClientProtocolException, IOException {
		return new Response(Executor.HTTP_CLIENT.execute(request));
	}

	public void abort() throws UnsupportedOperationException {
		request.abort();
	}

	// // HTTP header operations

	public Request addHeader(Header header) {
		request.addHeader(header);
		return this;
	}

	public Request addHeader(String name, String value) {
		request.addHeader(name, value);
		return this;
	}

	public Request removeHeader(Header header) {
		request.removeHeader(header);
		return this;
	}

	public Request removeHeaders(String name) {
		request.removeHeaders(name);
		return this;
	}

	public Request setHeaders(Header[] headers) {
		request.setHeaders(headers);
		return this;
	}

	public Request setCacheControl(String cacheControl) {
		request.setHeader(HttpHeader.CACHE_CONTROL, cacheControl);
		return this;
	}

	private SimpleDateFormat getDateFormat() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT, DATE_LOCALE);
		dateFormatter.setTimeZone(TIME_ZONE);
		return dateFormatter;
	}

	public Request setDate(Date date) {
		request.setHeader(HttpHeader.DATE, getDateFormat().format(date));
		return this;
	}

	public Request setIfModifiedSince(Date date) {
		request.setHeader(HttpHeader.IF_MODIFIED_SINCE, getDateFormat().format(date));
		return this;
	}

	public Request setIfUnmodifiedSince(Date date) {
		request.setHeader(HttpHeader.IF_UNMODIFIED_SINCE, getDateFormat().format(date));
		return this;
	}

	// // HTTP config parameter operations

	public Request config(String param, Object object) {
		localParams.setParameter(param, object);
		return this;
	}

	public Request removeConfig(String param) {
		localParams.removeParameter(param);
		return this;
	}

	// // HTTP protocol parameter operations

	public Request version(HttpVersion version) {
		return config(CoreProtocolPNames.PROTOCOL_VERSION, version);
	}

	public Request elementCharset(String charset) {
		return config(CoreProtocolPNames.HTTP_ELEMENT_CHARSET, charset);
	}

	public Request useExpectContinue() {
		return config(CoreProtocolPNames.USE_EXPECT_CONTINUE, true);
	}

	public Request userAgent(String agent) {
		return config(CoreProtocolPNames.USER_AGENT, agent);
	}

	// // HTTP connection parameter operations

	public Request socketTimeout(int timeout) {
		return config(CoreConnectionPNames.SO_TIMEOUT, timeout);
	}

	public Request connectTimeout(int timeout) {
		return config(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
	}

	public Request staleConnectionCheck(boolean b) {
		return config(CoreConnectionPNames.STALE_CONNECTION_CHECK, b);
	}

	// // HTTP connection route operations

	public Request viaProxy(HttpHost proxy) {
		return config(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	// // HTTP entity operations

	public Request body(HttpEntity entity) {
		Preconditions.checkState(request instanceof HttpEntityEnclosingRequest, request.getMethod() + " request cannot enclose an entity");
		((HttpEntityEnclosingRequest) request).setEntity(entity);
		return this;
	}

	public Request bodyForm(Iterable<? extends NameValuePair> formParams, Charset charset) {
		return body(new UrlEncodedFormEntity(formParams, charset));
	}

	public Request bodyForm(Iterable<? extends NameValuePair> formParams) {
		return bodyForm(formParams, HTTP.DEF_CONTENT_CHARSET);
	}

	public Request bodyForm(NameValuePair... formParams) {
		return bodyForm(Arrays.asList(formParams), HTTP.DEF_CONTENT_CHARSET);
	}

	public Request bodyString(String s, ContentType contentType) {
		return body(new StringEntity(s, contentType));
	}

	public Request bodyFile(File file, ContentType contentType) {
		return body(new FileEntity(file, contentType));
	}

	public Request bodyByteArray(byte[] b) {
		return body(new ByteArrayEntity(b));
	}

	public Request bodyByteArray(byte[] b, int off, int len) {
		return body(new ByteArrayEntity(b, off, len));
	}

	public Request bodyStream(InputStream instream) {
		return body(new InputStreamEntity(instream, -1));
	}

	public Request bodyStream(InputStream instream, ContentType contentType) {
		return body(new InputStreamEntity(instream, -1, contentType));
	}

	@Override
	public String toString() {
		return request.getRequestLine().toString();
	}
}
