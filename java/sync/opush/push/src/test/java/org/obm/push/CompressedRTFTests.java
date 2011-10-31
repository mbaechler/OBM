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
package org.obm.push;

import junit.framework.TestCase;

import org.obm.push.tnefconverter.RTFUtils;


public class CompressedRTFTests extends TestCase {

	public void testDecompress() {
		String rtf = "3gAAADkCAABMWkZ1lTR5wz8ACQMwAQMB9wKnAgBjaBEKw"
				+ "HNldALRcHJx4DAgVGFoA3ECgwBQ6wNUDzcyD9MyBgAGwwKDpxIBA+"
				+ "MReDA0EhUgAoArApEI5jsJbzAVwzEyvjgJtBdCCjIXQRb0ORIAHxeEGOEYExjgFcMyNTX/"
				+ "CbQaYgoyGmEaHBaKCaUa9v8c6woUG3YdTRt/Hwwabxbt/xyPF7gePxg4JY0YVyRMKR+"
				+ "dJfh9CoEBMAOyMTYDMYksgSc1AUAnNmYtQNY3GoAtkDktgTMtQAwBFy3QLX8KhX0wgA==";

		String txt = RTFUtils.extractB64CompressedRTF(rtf);
		assertEquals("Pouic pouic\n", txt);
	}

}
