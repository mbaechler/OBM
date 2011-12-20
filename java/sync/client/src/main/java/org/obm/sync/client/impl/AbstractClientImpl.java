package org.obm.sync.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.obm.locator.LocatorClientException;
import org.obm.sync.XTrustProvider;
import org.obm.sync.auth.AccessToken;
import org.obm.sync.locators.Locator;
import org.obm.sync.utils.DOMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public abstract class AbstractClientImpl {

	static {
		XTrustProvider.install();
	}
	
	private static final int MAX_CONNECTIONS = 8;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final SyncClientException exceptionFactory;
	protected HttpClient hc;
	
	private static final HttpMethodRetryHandler retryH = new HttpMethodRetryHandler() {
			public boolean retryMethod(HttpMethod arg0, IOException arg1, int arg2) {
				return false;
			}
		};

	protected abstract Locator getLocator();

	protected static HttpClient createHttpClient() {
		MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = 
				new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setMaxTotalConnections(MAX_CONNECTIONS);
		params.setDefaultMaxConnectionsPerHost(MAX_CONNECTIONS);
		multiThreadedHttpConnectionManager.setParams(params);
		HttpClient ret = new HttpClient(multiThreadedHttpConnectionManager);
		ret.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryH);
		return ret;
	}

	public AbstractClientImpl(SyncClientException exceptionFactory) {
		super();
		this.exceptionFactory = exceptionFactory;
		this.hc = createHttpClient();
	}

	protected Document execute(AccessToken token, String action, Multimap<String, String> parameters) {
		PostMethod pm = null;
		try {
			pm = getPostMethod(token, action);
			InputStream is = executeStream(pm, parameters);
			if (is != null) {
				return DOMUtils.parse(is);
			}
		} catch (LocatorClientException e) {
			logger.error(e.getMessage(), e);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (FactoryConfigurationError e) {
			logger.error(e.getMessage(), e);
		} finally {
			releaseConnection(pm);
		}
		return null;
	}

	protected void setToken(Multimap<String, String> parameters, AccessToken token) {
		if (token != null) {
			parameters.put("sid", token.getSessionId());
		}
	}

	protected Multimap<String, String> initParams(AccessToken at) {
		Multimap<String, String> m = ArrayListMultimap.create();
		setToken(m, at);
		return m;
	}

	private InputStream executeStream(PostMethod pm, Multimap<String, String> parameters) {
		InputStream is = null;
		try {
			for (Entry<String, String> entry: parameters.entries()) {
				pm.setParameter(entry.getKey(), entry.getValue());
			}
			int ret = hc.executeMethod(pm);
			if (ret != HttpStatus.SC_OK) {
				logger.error("method failed:\n" + pm.getStatusLine() + "\n"
						+ pm.getResponseBodyAsString());
			} else {
				is = pm.getResponseBodyAsStream();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return is;
	}

	protected void executeVoid(AccessToken at, String action, Multimap<String, String> parameters) {
		PostMethod pm = null; 
		try {
			pm = getPostMethod(at, action);
			executeStream(pm, parameters);
		} catch (LocatorClientException e) {
			logger.error(e.getMessage(), e);
		} finally {
			releaseConnection(pm);
		}
	}

	private String getBackendUrl(String loginAtDomain) throws LocatorClientException {
		Locator locator = getLocator();
		return locator.backendUrl(loginAtDomain);
	}

	private PostMethod getPostMethod(AccessToken at, String action) throws LocatorClientException {
		String backendUrl = getBackendUrl(at.getUserWithDomain());
		PostMethod pm = new PostMethod(backendUrl + action);
		pm.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		return pm;
	}

	private void releaseConnection(PostMethod pm) {
		if (pm != null) {
			pm.releaseConnection();
		}
	}

}
