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

public enum ClientIntent {
	ciManager, // 1
	ciDelegate,// 2
	ciDeletedWithNoResponse,// 4
	ciDeletedExceptionWithNoResponse,// 8
	ciRespondedTentative,// 10
	ciRespondedAccept,// 20
	ciRespondedDecline,// 40
	ciModifiedStartTime,// 80
	ciModifiedEndTime,// 100
	ciModifiedLocation,// 200
	ciRespondedExceptionDecline,// 400
	ciCanceled,// 800
	ciExceptionCanceled;// 1000
	
	public static ClientIntent getClientIntent(String val){
		if("1".equals(val)){
			return ciManager;
		} else if("2".equals(val)){
			return ciDelegate;
		} else if("4".equals(val)){
			return ciDeletedWithNoResponse;
		} else if("8".equals(val)){
			return ciDeletedExceptionWithNoResponse;
		} else if("10".equals(val)){
			return ciRespondedTentative;
		} else if("20".equals(val)){
			return ciRespondedAccept;
		} else if("40".equals(val)){
			return ciRespondedDecline;
		} else if("80".equals(val)){
			return ciModifiedStartTime;
		} else if("100".equals(val)){
			return ciModifiedEndTime;
		} else if("200".equals(val)){
			return ciModifiedLocation;
		} else if("400".equals(val)){
			return ciRespondedExceptionDecline;
		} else if("800".equals(val)){
			return ciCanceled;
		} else if("1000".equals(val)){
			return ciExceptionCanceled;
		} 
		return null;
	}

}
