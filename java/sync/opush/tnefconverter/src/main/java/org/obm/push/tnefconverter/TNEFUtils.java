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
package org.obm.push.tnefconverter;

import java.io.InputStream;

import net.freeutils.tnef.Message;

import org.apache.james.mime4j.parser.MimeStreamParser;
import org.obm.push.tnefconverter.ScheduleMeeting.TNEFExtractorUtils;

public class TNEFUtils {

	public static Boolean isScheduleMeetingRequest(InputStream email)
			throws TNEFConverterException {
		try {
			MimeStreamParser parser = new MimeStreamParser();
			EmailTnefHandler handler = new EmailTnefHandler();
			parser.setContentHandler(handler);
			parser.parse(email);
			Message message = handler.getTNEFMsg();
			return (message != null && TNEFExtractorUtils
					.isScheduleMeetingRequest(message));
		} catch (Throwable e) {
			throw new TNEFConverterException(e);
		}
	}

	public static Boolean containsTNEFAttchment(InputStream email)
			throws TNEFConverterException {
		try {
			MimeStreamParser parser = new MimeStreamParser();
			EmailTnefHandler handler = new EmailTnefHandler();
			parser.setContentHandler(handler);
			parser.parse(email);
			Message message = handler.getTNEFMsg();
			return message != null;
		} catch (Throwable e) {
			throw new TNEFConverterException(e);
		}
	}
}
