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

package org.obm.sync.push.client;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;

public final class XTrustProvider extends java.security.Provider {

	/**
	 * 
	 */
	private static final long serialVersionUID = -818891961979231358L;
	
	
	private final static String NAME = "XTrustJSSE";
	private final static String INFO = "XTrust JSSE Provider (implements trust factory with truststore validation disabled)";
	private final static double VERSION = 1.0D;

	public XTrustProvider() {
		super(NAME, VERSION, INFO);

		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				put("TrustManagerFactory."
						+ TrustManagerFactoryImpl.getAlgorithm(),
						TrustManagerFactoryImpl.class.getName());
				return null;
			}
		});
	}

	public static void install() {
		if (Security.getProvider(NAME) == null) {
			Security.insertProviderAt(new XTrustProvider(), 2);
			Security.setProperty("ssl.TrustManagerFactory.algorithm",
					TrustManagerFactoryImpl.getAlgorithm());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
	}

	public final static class TrustManagerFactoryImpl extends
			TrustManagerFactorySpi {
		public TrustManagerFactoryImpl() {
		}

		public static String getAlgorithm() {
			return "XTrust509";
		}

		protected void engineInit(KeyStore keystore) throws KeyStoreException {
		}

		protected void engineInit(ManagerFactoryParameters mgrparams)
				throws InvalidAlgorithmParameterException {
			throw new InvalidAlgorithmParameterException(XTrustProvider.NAME
					+ " does not use ManagerFactoryParameters");
		}

		protected TrustManager[] engineGetTrustManagers() {
			return new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs,
						String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs,
						String authType) {
				}
			} };
		}
	}
}
