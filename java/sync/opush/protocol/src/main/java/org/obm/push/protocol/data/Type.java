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

public enum Type {

	PLAIN_TEXT, // 1
	HTML, // 2
	RTF, // 3
	MIME; // 4

	@Override
	public String toString() {
		switch (this) {
		case HTML:
			return "2";
		case RTF:
			return "3";
		case MIME:
			return "4";

		default:
		case PLAIN_TEXT:
			return "1";
		}
	}

	public static Type fromInt(int i) {
		switch (i) {
		case 2:
			return HTML;
		case 3:
			return RTF;
		case 4:
			return MIME;

		case 1:
		default:
			return PLAIN_TEXT;
		}
	}

}
