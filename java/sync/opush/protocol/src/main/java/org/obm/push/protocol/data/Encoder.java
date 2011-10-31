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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.obm.push.utils.DOMUtils;
import org.w3c.dom.Element;

public class Encoder {

	
	protected Encoder(){
	}
	
	protected void s(Element p, String name, String val) {
		if (val != null && val.length() > 0) {
			DOMUtils.createElementAndText(p, name, val);
		}
	}

	protected void s(Element p, String name, Integer val) {
		if (val != null) {
			DOMUtils.createElementAndText(p, name, val.toString());
		}
	}

	protected void s(Element p, String name, Date val, SimpleDateFormat sdf) {
		if (val != null) {
			DOMUtils.createElementAndText(p, name, sdf.format(val));
		}
	}

	protected void s(Element p, String name, Boolean val) {
		if (val == null) {
			val = false;
		}
		DOMUtils.createElementAndText(p, name, val ? "1" : "0");
	}
	
}
