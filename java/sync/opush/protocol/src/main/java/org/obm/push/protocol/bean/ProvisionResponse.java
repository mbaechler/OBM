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

import org.obm.push.bean.ProvisionStatus;
import org.obm.push.protocol.provisioning.Policy;

public class ProvisionResponse {

	private final String policyType;
	private Long policyKey;
	private Policy policy;
	private ProvisionStatus status;
	
	public ProvisionResponse(String policyType) {
		super();
		this.policyType = policyType;
	}
	
	public String getPolicyType() {
		return policyType;
	}

	public Long getPolicyKey() {
		return policyKey;
	}

	public Policy getPolicy() {
		return policy;	
	}

	public ProvisionStatus getStatus() {
		return status;
	}

	public void setStatus(ProvisionStatus status) {
		this.status = status;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	public void setPolicyKey(Long policyKey) {
		this.policyKey = policyKey;
	}
	
}
