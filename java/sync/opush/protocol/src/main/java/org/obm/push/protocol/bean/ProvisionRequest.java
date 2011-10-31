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

import com.google.common.base.Objects;

public class ProvisionRequest {

	private final String policyType;
	private final long policyKey;

	public ProvisionRequest(String policyType, long policyKey) {
		this.policyType = policyType;
		this.policyKey = policyKey;
	}
	
	public long getPolicyKey() {
		return policyKey;
	}
	
	public String getPolicyType() {
		return policyType;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(getClass())
			.add("policyType", policyType)
			.add("policyKey", policyKey)
			.toString();
	}
	
}
