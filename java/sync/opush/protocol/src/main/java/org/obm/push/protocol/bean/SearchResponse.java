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

import java.util.List;

import org.obm.push.bean.SearchResult;

public class SearchResponse {

	private final List<SearchResult> results;
	private final int rangeLower;
	private final int rangeUpper;
	
	public SearchResponse(List<SearchResult> results, int rangeLower, int rangeUpper) {
		this.results = results;
		this.rangeLower = rangeLower;
		this.rangeUpper = rangeUpper;
	}

	public List<SearchResult> getResults() {
		return results;
	}
	
	public int getRangeLower() {
		return rangeLower;
	}
	
	public int getRangeUpper() {
		return rangeUpper;
	}
}
