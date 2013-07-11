/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.obm.dav.hc;

public class LockDiscovery {
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private String owner;
		private String token;
		
		private Builder() {
		}
		
		public Builder owner(String owner) {
			this.owner = owner;
			return this;
		}
		
		public Builder token(String token) {
			this.token = token;
			return this;
		}
		
		public LockDiscovery build() {
			return new LockDiscovery(owner, token);
		}
	}
	
	private final String owner;
	private final String token;
	
	private LockDiscovery(String owner, String token) {
		this.owner = owner;
		this.token = token;
	}

	public String getOwner() {
		return owner;
	}

	public String getToken() {
		return token;
	}
}