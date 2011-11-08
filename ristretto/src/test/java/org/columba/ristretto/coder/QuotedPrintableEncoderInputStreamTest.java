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
package org.columba.ristretto.coder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;


public class QuotedPrintableEncoderInputStreamTest {

	@Test
	public void testEncodedNone() throws Exception {
		String input = "This is a\tTest";
		Assert.assertTrue(runEncoder(input).equals("This is a\tTest"));
	}

	@Test
	public void testEncodedSimple() throws Exception {
		String input = "This is a \u00dcest";
		Assert.assertTrue(runEncoder(input).equals("This is a =DCest"));
	}

	@Test
	public void testEncodedWSBreak() throws Exception {
		String input = "This is a \n\u00dcest ";
		Assert.assertTrue(runEncoder(input).equals("This is a=20\r\n=DCest=20"));
	}

	@Test
	public void testEncodedLongline()  throws Exception {
		String input = "This is a very long line that has in total some f\u00fcnfundsiebzig +1 characters";
		Assert.assertTrue(runEncoder(input).equals("This is a very long line that has in total some f=FCnfundsiebzig +1 charac=\r\nters"));
	}
    
	@Test
    public void testVeryLongLine() throws Exception {
        String input = "\n"+
            "> Kann sie als Word unm\u00e4glich per Mail schicken. Die screenshots und Quellcodes m\u00e4ssen ja komplett angeglichen werden. Entweder dann bei Euch \u00e4ber Standleitung runterziehen oder Netzwerk???\n"+
            ">\n\n"+ 
            "Test";
        runEncoder(input);
    }
    
    private String runEncoder(String input) throws Exception {
        InputStream in = new QuotedPrintableEncoderInputStream( new ByteArrayInputStream( input.getBytes("ISO-8859-1")));
        
        StringBuffer result = new StringBuffer();
        int next = in.read();
        while( next != -1 ) {
            result.append((char) next);
            next = in.read();
        }
        in.close();
        return result.toString();
    }
}
