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
package org.obm.push.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.transform.TransformerException;

import org.obm.push.utils.DOMUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMDumper {

	private static boolean withData = new File("/etc/opush/data.in.logs")
			.exists();

	/**
	 * Seeing email/cal/contact data is a security issue for some
	 * administrators. Remove data from a copy of the DOM before printing.
	 * 
	 * @param doc
	 */
	public static void dumpXml(Logger logger, Document doc) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Document c = DOMUtils.cloneDOM(doc);

			if (!withData) {
				NodeList nl = c.getElementsByTagName("ApplicationData");
				for (int i = 0; i < nl.getLength(); i++) {
					Node e = nl.item(i);
					NodeList children = e.getChildNodes();
					for (int j = 0; j < children.getLength(); j++) {
						Node child = children.item(j);
						e.removeChild(child);
					}

					e.setTextContent("[trimmed_output]");
				}
			}

			DOMUtils.serialise(c, out, true);
			logger.debug(out.toString());
		} catch (TransformerException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
