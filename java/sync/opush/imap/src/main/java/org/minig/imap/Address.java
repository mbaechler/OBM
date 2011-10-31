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

import org.obm.push.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Address {

	private static final Logger logger = LoggerFactory.getLogger(Address.class);
	
	private String mail;
	private String displayName;

	public Address(String mail) {
		this(null, mail);
	}

	public Address(String displayName, String mail) {
		if (displayName != null) {
			this.displayName = StringUtils.stripAddressForbiddenChars(displayName);
		}
		
		if (mail != null && mail.contains("@")) {
			this.mail = StringUtils.stripAddressForbiddenChars(mail);
		} else {
			// FIXME ...
			if (logger.isDebugEnabled()) {
				logger.debug("mail: {} is not a valid email, building a john.doe@minig.org", mail);
			}
			this.displayName = StringUtils.stripAddressForbiddenChars(mail);
			this.mail = "john.doe@minig.org";
		}
	}

	public String getMail() {
		return mail;
	}

	public String getDisplayName() {
		return displayName != null ? displayName : mail;
	}

	@Override
	public String toString() {
		return "" + displayName + " <" + mail + ">";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		return true;
	}

}
