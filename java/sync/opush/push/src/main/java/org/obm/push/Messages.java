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
package org.obm.push;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.inject.Singleton;

@Singleton
public class Messages {

	private ResourceBundle bundle;
	private final Locale locale;

	public Messages(Locale locale) {
		this.locale = locale;
		bundle = ResourceBundle.getBundle("Messages", this.locale);
	}
	
	private String getString(String key, Object... arguments) {
		String isoEncodedString = bundle.getString(key);
		String string = new String(isoEncodedString.getBytes(Charsets.ISO_8859_1), Charsets.UTF_8);
		MessageFormat format = new MessageFormat(string, locale);
		return format.format(arguments);
	}
	
	public String mailTooLargeTitle() {
		return getString("MailTooLargeTitle");
	}
	
	public String mailTooLargeBodyStructure(int maxSize, String previousMessageReferenceText) {
		String humanReadableSize = FileUtils.byteCountToDisplaySize(maxSize);
		return getString("MailTooLargeBodyStructure", humanReadableSize, previousMessageReferenceText);
	}
	
	public String mailTooLargeHeaderFormat(String messageId, String subject, String to, String cc, String bcc) {
		return getString("MailTooLargeHeaderFormat", messageId, subject, 
				Strings.nullToEmpty(to), 
				Strings.nullToEmpty(cc), 
				Strings.nullToEmpty(bcc));
	}
}
