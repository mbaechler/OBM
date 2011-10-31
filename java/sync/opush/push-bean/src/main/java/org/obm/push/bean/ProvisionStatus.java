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
package org.obm.push.bean;

public enum ProvisionStatus {

	SUCCESS, // 1

	// if the parent element is the 'PROVISION' element
	PROTOCOL_ERROR, // 2
	GENERAL_SERVER_ERROR, // 3
	
	// if the parent element is the 'POLICY' element (the child of the POLICY is in the RESPONSE)

	POLICY_NOT_DEFINED, // 2
	UNKNOW_POLICY_TYPE_VALUE, // 3
	THE_CLIENT_IS_ACKNOWLEDGING_THE_WRONG_POLICY_KEY; // 5
	
	public String asXmlValue() {
		switch (this) {
		case PROTOCOL_ERROR:
		case POLICY_NOT_DEFINED:
			return "2";
		case GENERAL_SERVER_ERROR:
		case UNKNOW_POLICY_TYPE_VALUE:
				return "3";
		case THE_CLIENT_IS_ACKNOWLEDGING_THE_WRONG_POLICY_KEY:
			return "5";
		case SUCCESS:
		default:
			return "1";
		}
	}
}
