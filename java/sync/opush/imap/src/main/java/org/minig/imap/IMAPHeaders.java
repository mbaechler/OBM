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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.minig.imap.impl.DateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMAPHeaders {

	private static final Logger logger = LoggerFactory
			.getLogger(IMAPHeaders.class);

	private long uid;
	private Map<String, String> raw;
	private Address from;
	private List<Address> to;
	private List<Address> cc;
	private List<Address> bcc;
	private List<Address> dispositionNotification;
	private String subject;
	private Date date;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public void setRawHeaders(Map<String, String> rawHeaders) {
		this.raw = rawHeaders;
	}

	public String getRawHeader(String header) {
		if (raw != null) {
			return raw.get(header.toLowerCase());
		}
		return null;
	}

	public Date getDate() {
		if (date == null) {
			try {
				String rawD = getRawHeader("date");
				if (rawD != null) {
					date = DateParser.parse(rawD);
				} else {
					date = new Date();
				}
			} catch (ParseException e) {
				date = new Date();
			}
		}
		return date;
	}

	public void setDate(Date d) {
		this.date = d;
	}

	public List<Address> getDispositionNotification() {
		if (dispositionNotification == null) {
			dispositionNotification = parseAddressList(getRawHeader("Disposition-Notification-To"));
		}
		return dispositionNotification;
	}
	
	public Address getFrom() {
		if (from == null) {
			from = parseAddress(getRawHeader("from"));
		}
		return from;
	}

	public void setFrom(Address a) {
		this.from = a;
	}

	private List<Address> parseAddressList(String header) {
		if (header != null) {
			String[] parts = header.split(",");
			ArrayList<Address> list = new ArrayList<Address>(parts.length);
			for (String part: parts) {
				Address tmp = parseAddress(part);
				if (tmp != null) {
					list.add(tmp);
				}
			}
			return list;
		} else {
			return Collections.emptyList();
		}
	}
	
	private final Address parseAddress(String raw) {
		if (raw == null) {
			return null;
		}
		String rawHeader = EncodedWord.decode(raw).toString();
		if (rawHeader.trim().length() == 0) {
			return null;
		}
		int idx = rawHeader.indexOf("<");
		if (idx < 0) {
			return new Address(rawHeader);
		} else {
			int end = rawHeader.lastIndexOf(">");
			if (end > idx + 1 && end <= rawHeader.length()) {
				String mail = rawHeader.substring(idx + 1, end);
				String dName = rawHeader.substring(0, idx).trim();
				return new Address(dName, mail);
			} else {
				logger.warn("invalid email: " + rawHeader + " subject: "
						+ getSubject());
				return new Address(rawHeader);
			}
		}
	}

	public List<Address> getTo() {
		if (to == null) {
			String toHead = getRawHeader("to");
			to = parseAddressList(toHead);
		}
		return to;
	}

	public void setTo(List<Address> value) {
		if (value != null) {
			this.to = value;
		}
	}

	public void setCc(List<Address> value) {
		if (value != null) {
			this.cc = value;
		}
	}

	public void setBcc(List<Address> value) {
		if (value != null) {
			this.bcc = value;
		}
	}

	public List<Address> getCc() {
		if (cc == null) {
			String ccHead = getRawHeader("cc");
			cc = parseAddressList(ccHead);
		}
		return cc;
	}

	public List<Address> getBcc() {
		if (bcc == null) {
			String ccHead = getRawHeader("bcc");
			bcc = parseAddressList(ccHead);
		}
		return bcc;
	}

	public List<Address> getRecipients() {
		List<Address> recipients = new ArrayList<Address>();
		recipients.addAll(getTo());
		recipients.addAll(getCc());
		return recipients;
	}

	public String getSubject() {
		if (subject == null) {
			String rs = getRawHeader("subject");
			if (rs != null) {
				subject = EncodedWord.decode(rs).toString();
			} else {
				subject = "[Empty Subject]";
			}
		}
		return subject;
	}

	public void setSubject(String s) {
		if (s != null) {
			this.subject = s;
		} else {
			this.subject = "[Empty Subject]";
		}
	}

	public Map<String, String> getRawHeaders() {
		return raw;
	}
	
}
