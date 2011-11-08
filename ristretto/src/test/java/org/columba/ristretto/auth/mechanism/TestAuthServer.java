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
package org.columba.ristretto.auth.mechanism;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.columba.ristretto.auth.AuthenticationException;
import org.columba.ristretto.auth.AuthenticationServer;

public class TestAuthServer implements AuthenticationServer {
    
    private String result;
    private boolean ok = true;
    
    private List calls;
    private List responses;
    private int call_nr;
    private int response_nr;
    
    public TestAuthServer() {
        calls = new LinkedList();
        responses = new LinkedList();
        
        response_nr = call_nr = 0;
    }
    
    /**
     * @see org.columba.ristretto.auth.AuthenticationServer#authSend(byte[])
     */
    public void authSend(byte[] call) throws IOException {
    	if( calls.size() > call_nr) {
    		ok = ok && Arrays.equals(call, (byte[]) calls.get(call_nr++));
    	}
    }

    /**
     * @see org.columba.ristretto.auth.AuthenticationServer#authReceive()
     */
    public byte[] authReceive() throws AuthenticationException, IOException {
        if( !ok ) throw new AuthenticationException();
        if( response_nr < responses.size()) {
            return (byte[]) responses.get(response_nr++);
        } else {
            return new byte[0];
        }
    }
    
    public void addCall(byte[] call) {
        calls.add(call);
    }
    
    public void addResponse(byte[] response) {
        responses.add( response );
    }

    /**
     * @param result
     */
    public TestAuthServer(byte[] result) {
        this();
        
        calls.add(result);
    }
    /**
     * @see org.columba.ristretto.auth.AuthenticationServer#getHostName()
     */
    public String getHostName() {
        return "testserver";
    }
    /**
     * @see org.columba.ristretto.auth.AuthenticationServer#getService()
     */
    public String getService() {
        return "test";
    }
    /**
     * @return Returns the ok.
     */
    public boolean isOk() {
        return ok;
    }
}
