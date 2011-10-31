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
package org.obm.push.mail;

import java.util.LinkedList;
import java.util.List;

import org.minig.imap.Address;
import org.obm.push.bean.MSAddress;

public class AddressConverter {
	
	private AddressConverter() {
	}
	
	public static MSAddress convertAddress(Address add){
		if(add == null){
			return null;
		}
		MSAddress msAdd = new MSAddress(add.getDisplayName(),add.getMail());

		return msAdd;
	}
	
	public static List<MSAddress> convertAddresses(List<Address> adds){
		List<MSAddress> ret = new LinkedList<MSAddress>();
		for(Address add : adds){
			ret.add(convertAddress(add));
		}	
		return ret;
	}
	
	
}
