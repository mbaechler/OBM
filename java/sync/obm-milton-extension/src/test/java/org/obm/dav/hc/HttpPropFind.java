/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2013  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.dav.hc;

import java.net.URI;

import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * HTTP DELETE method
 * <p>
 * The HTTP DELETE method is defined in section 9.7 of <a
 * href="http://www.ietf.org/rfc/rfc2616.txt">RFC2616</a>: <blockquote> The
 * DELETE method requests that the origin server delete the resource identified
 * by the Request-URI. [...] The client cannot be guaranteed that the operation
 * has been carried out, even if the status code returned from the origin server
 * indicates that the action has been completed successfully. </blockquote>
 * 
 * @since 4.0
 */
@NotThreadSafe
// HttpRequestBase is @NotThreadSafe
public class HttpPropFind extends HttpRequestBase {

	public final static String METHOD_NAME = "PROPFIND";

	public HttpPropFind() {
		super();
	}

	public HttpPropFind(final URI uri) {
		super();
		setURI(uri);
	}

	/**
	 * @throws IllegalArgumentException
	 *             if the uri is invalid.
	 */
	public HttpPropFind(String uri) {
		super();
		setURI(URI.create(uri));
	}

	@Override
	public String getMethod() {
		return METHOD_NAME;
	}
}
