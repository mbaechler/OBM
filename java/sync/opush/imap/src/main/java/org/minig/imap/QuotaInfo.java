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

import java.io.Serializable;

public class QuotaInfo implements Serializable{

	private static final long serialVersionUID = 7172033843599691627L;
	private boolean enable;
	private int usage;
	private int limit;
	
	public QuotaInfo(){
		this.enable = false;
		this.usage=0;
		this.limit=0;
	}
	
	public QuotaInfo(int usages, int limites){
		this.enable = true;
		this.usage=usages;
		this.limit=limites;
	}
	
	public boolean isEnable() {
		return enable;
	}

	public int getUsage() {
		return usage;
	}

	public int getLimit() {
		return limit;
	}
}
