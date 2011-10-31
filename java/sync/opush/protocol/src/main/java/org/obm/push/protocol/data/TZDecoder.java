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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;

public class TZDecoder {

	public TimeZone decode(String b64) {
		// Doc : [MS-ASDTYPE] 2.7 TimeZone
		// Doc about types :
		// http://msdn.microsoft.com/fr-fr/library/bb469811.aspx
		// 1 LONG = 4 bytes
		// 1 WCHAR = 2 bytes
		// 1 SYSTEMTIME = 8 SHORT = 8 X 2 bytes
		// TOTAL TIMEZONE STRUCT must be 172 bytes

		byte tzstruct[] = Base64.decodeBase64(b64);

		ByteBuffer bfBias = ByteBuffer.wrap(tzstruct, 0, 4);
		// NOT YET USED
		//
		// ByteBuffer bfStandardName = ByteBuffer.wrap(tzstruct, 4, 64);
		// ByteBuffer bfStandardDate = ByteBuffer.wrap(tzstruct, 68, 16);
		// ByteBuffer bfStandardBias = ByteBuffer.wrap(tzstruct, 84, 4);
		// ByteBuffer bfDaylightName = ByteBuffer.wrap(tzstruct, 88, 64);
		// ByteBuffer bfDaylightDate = ByteBuffer.wrap(tzstruct, 152, 16);
		// ByteBuffer bfDaylightBias = ByteBuffer.wrap(tzstruct, 168, 4);

		bfBias.order(ByteOrder.LITTLE_ENDIAN);
		int bias = bfBias.getInt(); // Java integer is 4-bytes-long

		// NOT YET USED
		//
		// bfStandardBias.order(ByteOrder.LITTLE_ENDIAN);
		// int standardBias = bfStandardBias.getInt();
		//		
		// bfDaylightBias.order(ByteOrder.LITTLE_ENDIAN);
		// int daylightBias = bfDaylightBias.getInt();

		TimeZone timezone = TimeZone.getDefault();
		timezone.setRawOffset(bias * 60 * 1000);

		String timezones[] = TimeZone.getAvailableIDs(bias * 60 * 1000);
		if (timezones.length > 0) {
			timezone = TimeZone.getTimeZone(timezones[0]);
		}

		// USEFUL DEBUG LINES
		//
		// StringBuffer sb = new StringBuffer();
		// for (int i = 0; i < 172; i+=1) {
		// sb.append(Byte.valueOf(tzstruct[i]).intValue());
		// }
		//		
		// logger.info("b64: " + b64);
		// logger.info("tzstruct: "+ sb.toString());
		// logger.info("bias: " + bias);
		// logger.info("standardbias: " + standardBias);
		// logger.info("standardname: " +
		// bfStandardName.asCharBuffer().toString());
		// logger.info("daylightBias: " + daylightBias);

		return timezone;
	}
	
}
