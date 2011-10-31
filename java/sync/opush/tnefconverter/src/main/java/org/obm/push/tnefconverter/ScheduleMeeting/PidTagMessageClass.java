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

public enum PidTagMessageClass {
	ScheduleMeetingRequest {
		@Override
		public String toString() {
			return "IPM.Microsoft Schedule.MtgReq";
		}
	},
	ScheduleMeetingCanceled {
		@Override
		public String toString() {
			return "IPM.Microsoft Schedule.MtgCncl";
		} 
	},
	ScheduleMeetingRespPos {
		@Override
		public String toString() {
			return "IPM.Microsoft Schedule.MtgRespP";
		}
	},
	ScheduleMeetingRespTent {
		@Override
		public String toString() {
			return "IPM.Microsoft Schedule.MtgRespA";
		}
	},
	ScheduleMeetingRespNeg {
		@Override
		public String toString() {
			return "IPM.Microsoft Schedule.MtgRespN";
		}
	};

	public abstract String toString();
	
	public static PidTagMessageClass getPidTagMessageClass(String val) {
		if ("IPM.Microsoft Schedule.MtgReq".equals(val)) {
			return ScheduleMeetingRequest;
		} else if ("IPM.Microsoft Schedule.MtgCncl".equals(val)) {
			return ScheduleMeetingCanceled;
		} else if ("IPM.Microsoft Schedule.MtgRespP".equals(val)) {
			return ScheduleMeetingRespPos;
		} else if ("IPM.Microsoft Schedule.MtgRespA".equals(val)) {
			return ScheduleMeetingRespTent;
		} else if ("IPM.Microsoft Schedule.MtgRespN".equals(val)) {
			return ScheduleMeetingRespNeg;
		} else {
			return null;
		}
	}
}
