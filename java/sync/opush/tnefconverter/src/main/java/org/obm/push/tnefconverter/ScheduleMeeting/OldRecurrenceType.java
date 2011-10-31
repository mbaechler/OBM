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
package org.obm.push.tnefconverter.ScheduleMeeting;

//MS-XWDCAL
public enum OldRecurrenceType {
	
	NONE, // Not set
	DAILY,// 64 
	WEEKLY, // 48
	MONTHLY,// 12 
	MONTHLY_NDAY, // 56
	YEARLY, // 7
	YEARLY_NDAY; // 51
	
	public static OldRecurrenceType getRecurrenceType(String val) {
		if("64".equals(val)){
			return DAILY;
		} else if("48".equals(val)){
			return WEEKLY;
		} else if("12".equals(val)){
			return MONTHLY;
		} else if("56".equals(val)){
			return MONTHLY_NDAY;
		} else if("7".equals(val)){
			return YEARLY;
		} else if("51".equals(val)){
			return YEARLY_NDAY;
		} else {
			return NONE;
		}
	}
	
}
