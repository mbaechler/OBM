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


public enum MSEmailBodyType {
	
	PlainText, HTML, RTF, MIME;

	public String asIntString() {
		switch (this) {
		case PlainText:
			return "1";
		case HTML:
			return "2";
		case RTF:
			return "3";
		case MIME:
			return "4";
		default:
			return "0";
		}
	}

	public static final MSEmailBodyType getValueOf(String s) {
		if ("text/rtf".equals(s)) {
			return RTF;
		} else if ("text/html".equals(s)) {
			return HTML;
		} else {
			return PlainText;
		}
	}
	
	public static final MSEmailBodyType getValueOf(Integer s) {
		if(s==null){
			return null;
		}
		
		if (s.equals(1)) {
			return PlainText;
		} else if (s.equals(2)) {
			return HTML;
		} else if (s.equals(3)) {
			return RTF;
		} else if (s.equals(4)) {
			return MIME;
		} else {
			return null;
		}
	}
}
