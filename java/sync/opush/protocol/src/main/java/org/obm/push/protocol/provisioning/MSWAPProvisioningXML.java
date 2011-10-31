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
package org.obm.push.protocol.provisioning;

import org.w3c.dom.Element;

/**
 * Policy type used in EAS protocol 2.5
 * 
 * http://social.msdn.microsoft.com/Forums/en-US/os_exchangeprotocols/thread/243320fa-89cb-4af0-934d-438aae5a8277
 */
public class MSWAPProvisioningXML extends Policy {

	@Override
	public void serialize(Element data) {
		// copied from exchange 2007 protocol 2.5 response
		data
				.setTextContent("<wap-provisioningdoc>"
						+ "<characteristic "
						+ "type=\"SecurityPolicy\"><parm name=\"4131\" value=\"1\"/><parm name=\"4133\" value=\"1\"/>"
						+ "</characteristic></wap-provisioningdoc>");
	}

}
