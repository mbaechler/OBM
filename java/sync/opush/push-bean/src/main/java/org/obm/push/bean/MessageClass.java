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

public enum MessageClass {
	Note {
		@Override
		public String toString() {
			return "IPM.Note";
		}
	}, NoteRulesOofTemplateMicrosoft {
		@Override
		public String toString() {
			return "IPM.Note.Rules.OofTemplate.Microsoft";
		}
	}, NoteSMIME {
		@Override
		public String toString() {
			return "IPM.Note.SMIME";
		}
	}, NoteSMIMEMultipartSigned {
		@Override
		public String toString() {
			return "IPM.Note.SMIME.MultipartSigned";
		}
	}, ScheduleMeetingRequest {
		@Override
		public String toString() {
			return "IPM.Schedule.Meeting.Request";
		}
	}, ScheduleMeetingCanceled {
		@Override
		public String toString() {
			return "IPM.Schedule.Meeting.Canceled";
		}
	}, ScheduleMeetingRespPos {
		@Override
		public String toString() {
			return "IPM.Schedule.Meeting.Resp.Pos";
		}
	}, ScheduleMeetingRespTent {
		@Override
		public String toString() {
			return "IPM.Schedule.Meeting.Resp.Tent";
		}
	}, ScheduleMeetingRespNeg {
		@Override
		public String toString() {
			return "IPM.Schedule.Meeting.Resp.Neg";
		}
	}, Post {
		@Override
		public String toString() {
			return "IPM.Post";
		}
	};

	public abstract String toString();

}
