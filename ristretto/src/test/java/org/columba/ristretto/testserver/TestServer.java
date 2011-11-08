/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Ristretto Mail API.
 *
 * The Initial Developers of the Original Code are
 * Timo Stich and Frederik Dietz.
 * Portions created by the Initial Developers are Copyright (C) 2004
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package org.columba.ristretto.testserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer implements Runnable {

	protected ServerSocket serverSocket;
	protected Thread thread;
	protected StringBuffer lineBuffer;
	protected TestServerSession session;
	
	protected boolean closeNow;

	public TestServer(int port, TestServerSession session) throws IOException {
		serverSocket = new ServerSocket(port);
		closeNow = false;

		this.session = session;

		lineBuffer = new StringBuffer();
		
		thread = new Thread(this);
		thread.setDaemon(false);
		thread.start();
		
	}

	public synchronized void stop() {
		try {
			if (serverSocket != null)
				serverSocket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
			try {
				// does a client trying to connect to server ?
				Socket client = serverSocket.accept();
				if (client == null)
					return;

				// only accept client from local machine
				String host = client.getLocalAddress().getHostAddress();
				if (!(host.equals("127.0.0.1"))) {
					// client isn't from local machine
					client.close();
				}

				// respond with Hello Response
				send(client.getOutputStream(), session.serverHelloResponse());

				while( !session.closeConnection() && !closeNow ) {
					// read command
					String clientCommand = readLine(client.getInputStream());
				
					String response = session.next(clientCommand);
					if( response == null ) {
					    stop();
						throw new Exception("No response for " + clientCommand + "-" );
					}
					send(client.getOutputStream(), response);
				}
				
				client.close();

			} catch (Exception ex) {
				ex.printStackTrace();
				stop();
			}
	}

	private String readLine(InputStream in) throws IOException {
		// Clear the buffer
		lineBuffer.delete(0, lineBuffer.length());

		int read = in.read();
		// read until CRLF
		while (read != '\r' && read != -1) {
			lineBuffer.append((char) read);
			read = in.read();
		}
		lineBuffer.append((char) read);
		
		// read the LF
		read = in.read();
		lineBuffer.append((char) read);

		return lineBuffer.toString();
	}

	private void send(OutputStream out, String response) throws IOException {
		if( response == null || response.length() == 0) return;
		
		if( response.charAt(response.length()-1) == '\0') {
			response = response.substring(0,response.length()-1);
			closeNow = true;
		}
		out.write(response.getBytes());
		out.flush();
	}
}
