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

import io.milton.http.DateUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.http.HttpResponse;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class PropFindResponse {

	private static final Logger log = LoggerFactory.getLogger(PropFindResponse.class);

	public static List<PropFindResponse> parse(HttpResponse response, int depth) throws IOException, JDOMException {
		ImmutableList.Builder<PropFindResponse> responses = ImmutableList.builder();
		boolean isFirst = true;
		for (Element el : responseElements(response)) {
			// if depth=0 must return first and
			// only result
			if (!isFirst || depth == 0) { 
				responses.add(new PropFindResponse(el));
			} else {
				isFirst = false;
			}
		}
		return responses.build();
	}

	private static List<Element> responseElements(HttpResponse response) throws JDOMException, IOException {
		Document document = RespUtils.getJDomDocument(response.getEntity().getContent());
		return RespUtils.getElements(document.getRootElement(), "response");
	}

	private final String name;
	private final String href;
	private final boolean collection;
	private Map<QName, Object> properties = new HashMap<QName, Object>();

	public PropFindResponse(Element elResponse) {
		href = RespUtils.asString(elResponse, "href").trim();
		if (href.contains("/")) {
			String[] arr = href.split("[/]");
			if (arr.length > 0) {
				name = arr[arr.length - 1];
			} else {
				name = "";
			}
		} else {
			name = href;
		}
		
		List<Element> propElements = getFoundProps(elResponse);
		Element colElement = null;
		for (Object oElProp : propElements) {
			if (oElProp instanceof Element) {
				Element elProp = (Element) oElProp;
				String localName = elProp.getName();
				Namespace ns = elProp.getNamespace();
				QName qn = new QName(ns.getURI(), localName, ns.getPrefix());
				if (localName.equals("resourcetype")) {
					colElement = elProp.getChild("collection", RespUtils.NS_DAV);
				} else if (localName.equals("lockdiscovery")) {
					putLockDiscoveryProperty(elProp, qn);
				} else {
					putQNameProperty(elProp, localName, ns, qn);
				}
			}
		}
		collection = (colElement != null);
	}

	private void putLockDiscoveryProperty(Element elProp, QName qn) {
		LockDiscovery.Builder lock = LockDiscovery.builder();
		Element elActiveLock = elProp.getChild("activelock", RespUtils.NS_DAV);
		if (elActiveLock != null) {
			lock.owner(RespUtils.asString(elActiveLock, "owner"));
			
			Element elToken = elActiveLock.getChild("locktoken", RespUtils.NS_DAV);
			if (elToken != null) {
				String href = RespUtils.asString(elToken, "href");
				if (href != null && href.contains(":")) {
					href = href.substring(href.indexOf(":"));
				}
				lock.token(href);
			}
		}
		properties.put(qn, lock.build());
	}

	private void putQNameProperty(Element elProp, String localName, Namespace ns, QName qn) {
		String value = elProp.getText();
		// Date properties should be adjusted for the difference
		// between server and local time
		if (localName.equals("creationdate") || localName.equals("getlastmodified")) {
			try {
				Date dt = DateUtils.parseWebDavDate(value);
				properties.put(qn, dt);
				
				QName qnRaw = new QName(ns.getURI(), localName + "-raw", ns.getPrefix());
				properties.put(qnRaw, value);
			} catch (DateUtils.DateParseException e) {
				log.warn("Couldnt parse date property: " + localName + " = " + value);
			}
		} else {
			properties.put(qn, value);
		}
	}

	// getters for common properties

	public boolean isCollection() {
		return collection;
	}

	public String getHref() {
		return href;
	}

	public LockDiscovery getLock() {
		return (LockDiscovery) getDavProperty("lockdiscovery");
	}

	public String getName() {
		return name;
	}

	public Map<QName, Object> getProperties() {
		return properties;
	}

	public Object getDavProperty(String name) {
		QName qn = RespUtils.davName(name);
		return properties.get(qn);
	}

	public String getDisplayName() {
		String dn = (String) getDavProperty("displayname");
		return Objects.firstNonNull(dn, name);
	}

	public Date getCreatedDate() {
		return (Date) getDavProperty("creationdate");
	}

	public Date getModifiedDate() {
		return (Date) getDavProperty("getlastmodified");
	}

	public String getEtag() {
		return (String) getDavProperty("getetag");
	}

	public String getContentType() {
		return (String) getDavProperty("getcontenttype");
	}

	public Long getContentLength() {
		String contentLength = (String) getDavProperty("getcontentlength");
		if (Strings.isNullOrEmpty(contentLength)) {
			return null;
		}
		return Long.parseLong(contentLength);
	}

	public Long getQuotaAvailableBytes() {
		String quotaAvailableBytes = (String) getDavProperty("quota-available-bytes");
		if (Strings.isNullOrEmpty(quotaAvailableBytes)) {
			return null;
		}
		return Long.parseLong(quotaAvailableBytes);
	}

	public Long getQuotaUsedBytes() {
		String quotaUsedBytes = (String) getDavProperty("quota-used-bytes");
		if (Strings.isNullOrEmpty(quotaUsedBytes)) {
			return null;
		}
		return Long.parseLong(quotaUsedBytes);
	}

	private List<Element> getFoundProps(Element elResponse) {
		for (Object olPropStat : elResponse.getChildren()) {
			if (olPropStat instanceof Element) {
				Element propStat = (Element) olPropStat;
				if (propStat.getName().equals("propstat")) {
					Element elStatus = propStat.getChild("status", RespUtils.NS_DAV);
					if (elStatus != null) {
						String status = elStatus.getText();
						if (status != null && status.contains("200")) {
							Element elProps = propStat.getChild("prop", RespUtils.NS_DAV);
							if (elProps != null) {
								ImmutableList.Builder<Element> props = ImmutableList.builder();
								for (Object oProp : elProps.getChildren()) {
									if (oProp instanceof Element) {
										props.add((Element) oProp);
									}
								}
								return props.build();
							}
						}
					}
				}
			}
		}
		return Collections.EMPTY_LIST;
	}
}
