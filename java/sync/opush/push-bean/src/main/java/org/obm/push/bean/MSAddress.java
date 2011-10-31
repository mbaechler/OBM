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
package org.obm.push.bean;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class MSAddress implements Serializable {
	
	private static final Logger logger = LoggerFactory
			.getLogger(MSAddress.class);
	
	private String mail;
	private String displayName;

	public MSAddress(String mail) {
		this(null, mail);
	}

	public MSAddress(String displayName, String mail) {
		if (displayName != null) {
			this.displayName = displayName.replace("\"", "").replace("<", "")
					.replace(">", "");
		}
		if (mail != null && mail.contains("@")) {
			this.mail = mail.replace("\"", "").replace("<", "")
					.replace(">", "");
		} else {
			// FIXME ...
			if (logger.isDebugEnabled()) {
				logger
						.debug("mail: "
								+ mail
								+ " is not a valid email, building a john.doe@minig.org");
			}
			this.displayName = Strings.nullToEmpty(mail).replace("\"", "").replace("<", "").replace(
					">", "");
			this.mail = "john.doe@minig.org";
		}
	}

	public String getMail() {
		return mail;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public final int hashCode(){
		return Objects.hashCode(mail, displayName);
	}
	
	@Override
	public final boolean equals(Object object){
		if (object instanceof MSAddress) {
			MSAddress that = (MSAddress) object;
			return Objects.equal(this.mail, that.mail)
				&& Objects.equal(this.displayName, that.displayName);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("logger", logger)
			.add("mail", mail)
			.add("displayName", displayName)
			.toString();
	}
	
}
