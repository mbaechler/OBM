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
package org.obm.push.protocol.data;

import org.obm.push.bean.IApplicationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncoderFactory {

	private static final Logger logger = LoggerFactory
			.getLogger(EncoderFactory.class);

	public IDataEncoder getEncoder(IApplicationData data) {
		if (data != null) {
			switch (data.getType()) {

			case CALENDAR:
				return new CalendarEncoder();

			case CONTACTS:
				return new ContactEncoder();

			case TASKS:
				return new TaskEncoder();

			case EMAIL:
			default:
				return new EmailEncoder();
			}
		} else {
			logger.warn("TRY TO ENCODE NULL OBJECT");
			return null;
		}
	}

}
