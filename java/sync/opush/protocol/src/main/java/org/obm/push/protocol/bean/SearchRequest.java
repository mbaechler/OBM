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
package org.obm.push.protocol.bean;

import org.obm.push.bean.StoreName;

public class SearchRequest {
	
	private StoreName storeName;
	private String query;
	private Integer rangeLower;
	private Integer rangeUpper;
	
	public StoreName getStoreName() {
		return storeName;
	}

	public void setStoreName(StoreName storeName) {
		this.storeName = storeName;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Integer getRangeLower() {
		return rangeLower;
	}

	public void setRangeLower(Integer rangeLower) {
		this.rangeLower = rangeLower;
	}

	public Integer getRangeUpper() {
		return rangeUpper;
	}

	public void setRangeUpper(Integer rangeUpper) {
		this.rangeUpper = rangeUpper;
	}

}
