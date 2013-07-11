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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;

public class Content {

	public static final Content NO_CONTENT = new Content(new byte[] {}, ContentType.DEFAULT_BINARY);

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private byte[] raw;
		private ContentType type;
	
		private Builder() {
		}
		
		public Builder raw(byte[] raw) {
			this.raw = raw;
			return this;
		}
		
		public Builder type(ContentType type) {
			this.type = type;
			return this;
		}
		
		public Content build() {
			return new Content(raw, type);
		}
	}
	
	private final byte[] raw;
	private final ContentType type;

	private Content(final byte[] raw, final ContentType type) {
		this.raw = raw;
		this.type = type;
	}

	public ContentType getType() {
		return type;
	}

	public byte[] asBytes() {
		return raw.clone();
	}

	public String asString() {
		try {
			Charset charset = type.getCharset();
			if (charset == null) {
				charset = HTTP.DEF_CONTENT_CHARSET;
			}
			return new String(raw, charset.name());
		} catch (UnsupportedEncodingException ex) {
			return new String(raw);
		}
	}

	public InputStream asStream() {
		return new ByteArrayInputStream(raw);
	}

	@Override
	public String toString() {
		return asString();
	}
}
