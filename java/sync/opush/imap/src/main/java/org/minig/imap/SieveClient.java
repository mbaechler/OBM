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

package org.minig.imap;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import org.apache.mina.transport.socket.nio.SocketConnector;
import org.minig.imap.sieve.SieveClientHandler;
import org.minig.imap.sieve.SieveClientSupport;
import org.minig.imap.sieve.SieveScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client API to cyrus sieve server
 * 
 * <code>http://www.ietf.org/proceedings/06mar/slides/ilemonade-1.pdf</code>
 */
public class SieveClient {

	private static final Logger logger = LoggerFactory
			.getLogger(SieveClient.class);
	
	private String login;
	private SieveClientSupport cs;
	private String host;
	private int port;
	
	public SieveClient(String hostname, int port, String loginAtDomain,
			String password) {
		this.login = loginAtDomain;
		this.host = hostname;
		this.port = port;

		cs = new SieveClientSupport(login, password);
	}

	public boolean login() {
		if (logger.isDebugEnabled()) {
			logger.debug("login called");
		}
		SieveClientHandler handler = new SieveClientHandler(cs);
		SocketAddress sa = new InetSocketAddress(host, port);
		SocketConnector connector = new SocketConnector();
		boolean ret = false;
		if (cs.login(connector, sa, handler)) {
			ret = true;
		}
		return ret;
	}

	public List<SieveScript> listscripts() {
		return cs.listscripts();
	}

	public boolean putscript(String name, InputStream scriptContent) {
		return cs.putscript(name, scriptContent);
	}

	public void unauthenticate() {
		cs.unauthenticate();
	}
	
	public void logout() {
		cs.logout();
	}

	public boolean deletescript(String name) {
		return cs.deletescript(name);
	}

	public String getScript() {
		return "require [ \"fileinto\", \"imapflags\", \"vacation\" ];\n";
	}

	public void activate(String newName) {
		cs.activate(newName);
	}

}
