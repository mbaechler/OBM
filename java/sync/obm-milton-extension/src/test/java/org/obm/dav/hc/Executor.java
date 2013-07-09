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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLInitializationException;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;

/**
 * An Executor for fluent requests
 * <p/>
 * A {@link PoolingClientConnectionManager} with maximum 100 connections per
 * route and a total maximum of 200 connections is used internally.
 */
public class Executor {

	public final static PoolingClientConnectionManager CONNECTION_MANAGER;
	public final static DefaultHttpClient HTTP_CLIENT;

	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		SchemeSocketFactory plain = PlainSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("http", 80, plain));
		SchemeSocketFactory ssl = null;
		try {
			ssl = SSLSocketFactory.getSystemSocketFactory();
		} catch (SSLInitializationException ex) {
			SSLContext sslcontext;
			try {
				sslcontext = SSLContext.getInstance(SSLSocketFactory.TLS);
				sslcontext.init(null, null, null);
				ssl = new SSLSocketFactory(sslcontext);
			} catch (SecurityException ignore) {
			} catch (KeyManagementException ignore) {
			} catch (NoSuchAlgorithmException ignore) {
			}
		}
		if (ssl != null) {
			schemeRegistry.register(new Scheme("https", 443, ssl));
		}
		CONNECTION_MANAGER = new PoolingClientConnectionManager(schemeRegistry);
		CONNECTION_MANAGER.setDefaultMaxPerRoute(100);
		CONNECTION_MANAGER.setMaxTotal(200);
		HTTP_CLIENT = new DefaultHttpClient(CONNECTION_MANAGER);
	}

	public static Executor newInstance() {
		return new Executor(HTTP_CLIENT);
	}

	public static Executor newInstance(HttpClient httpclient) {
		return new Executor(httpclient != null ? httpclient : HTTP_CLIENT);
	}

	public static void registerScheme(Scheme scheme) {
		CONNECTION_MANAGER.getSchemeRegistry().register(scheme);
	}

	public static void unregisterScheme(String name) {
		CONNECTION_MANAGER.getSchemeRegistry().unregister(name);
	}

	private final HttpClient httpClient;
	private final BasicHttpContext localContext;
	private final AuthCache authCache;

	private CredentialsProvider credentialsProvider;
	private CookieStore cookieStore;

	private Executor(HttpClient httpClient) {
		this.httpClient = httpClient;
		this.localContext = new BasicHttpContext();
		this.authCache = new BasicAuthCache();
	}

	public Executor auth(AuthScope authScope, Credentials creds) {
		if (credentialsProvider == null) {
			credentialsProvider = new BasicCredentialsProvider();
		}
		credentialsProvider.setCredentials(authScope, creds);
		return this;
	}

	public Executor auth(HttpHost host, Credentials creds) {
		AuthScope authScope = host != null ? new AuthScope(host) : AuthScope.ANY;
		return auth(authScope, creds);
	}

	public Executor authPreemptive(HttpHost host) {
		authCache.put(host, new BasicScheme(ChallengeState.TARGET));
		return this;
	}

	public Executor authPreemptiveProxy(HttpHost host) {
		authCache.put(host, new BasicScheme(ChallengeState.PROXY));
		return this;
	}

	public Executor auth(Credentials cred) {
		return auth(AuthScope.ANY, cred);
	}

	public Executor auth(String username, String password) {
		return auth(new UsernamePasswordCredentials(username, password));
	}

	public Executor auth(String username, String password, String workstation, String domain) {
		return auth(new NTCredentials(username, password, workstation, domain));
	}

	public Executor auth(HttpHost host, String username, String password) {
		return auth(host, new UsernamePasswordCredentials(username, password));
	}

	public Executor auth(HttpHost host, String username, String password, String workstation, String domain) {
		return auth(host, new NTCredentials(username, password, workstation, domain));
	}

	public Executor clearAuth() {
		if (credentialsProvider != null) {
			credentialsProvider.clear();
		}
		return this;
	}

	public Executor cookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
		return this;
	}

	public Executor clearCookies() {
		if (cookieStore != null) {
			cookieStore.clear();
		}
		return this;
	}

	/**
	 * Executes the request. Please Note that response content must be processed
	 * or discarded using {@link Response#discardContent()}, otherwise the
	 * connection used for the request might not be released to the pool.
	 * 
	 * @see Response#handleResponse(org.apache.http.client.ResponseHandler)
	 * @see Response#discardContent()
	 */
	public Response execute(Request request) throws ClientProtocolException, IOException {
		localContext.setAttribute(ClientContext.CREDS_PROVIDER, credentialsProvider);
		localContext .setAttribute(ClientContext.AUTH_CACHE, authCache);
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		HttpRequestBase httpRequest = request.getHttpRequest();
		httpRequest.reset();
		return new Response(httpClient.execute(httpRequest, localContext));
	}
}
