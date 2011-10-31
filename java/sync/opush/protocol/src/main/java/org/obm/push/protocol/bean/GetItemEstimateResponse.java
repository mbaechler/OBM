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

import java.util.Collection;

import org.obm.push.bean.SyncCollection;

public class GetItemEstimateResponse {

	public static class Estimate {
		
		private final SyncCollection collection;
		private final int estimate;

		public Estimate(SyncCollection collection, int estimate) {
			this.collection = collection;
			this.estimate = estimate;
		}
		
		public SyncCollection getCollection() {
			return collection;
		}
		
		public int getEstimate() {
			return estimate;
		}
	}
	
	private final Collection<Estimate> estimates;
	
	public GetItemEstimateResponse(Collection<Estimate> estimates) {
		this.estimates = estimates;
	}
	
	public Collection<Estimate> getEstimates() {
		return estimates;
	}
}
