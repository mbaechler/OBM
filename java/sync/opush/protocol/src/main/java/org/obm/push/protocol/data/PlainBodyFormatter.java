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

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.ElementRemover;
import org.cyberneko.html.filters.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts HTML mail body to text
 */
public class PlainBodyFormatter {

	private static final Logger logger = LoggerFactory
			.getLogger(PlainBodyFormatter.class);

	public PlainBodyFormatter() {
	}

	public String convert(String html) {
		String retVal = "[html message]\n";

		String ret = html;
		ret = ret.replace("<br/>", "\n");
		ret = ret.replace("<BR/>", "\n");
		ret = ret.replace("<BR>", "\n");
		ret = ret.replace("<br>", "\n");

		ElementRemover er = new ElementRemover() {

			@Override
			public void comment(XMLString text, Augmentations augs)
					throws XNIException {
				// strip out comments, outlook loves comments in its html
			}

		};
		er.removeElement("script");
		er.removeElement("style");
		StringWriter sw = new StringWriter();

		XMLDocumentFilter[] filters = { er, new Writer(sw, "UTF-8") };

		XMLParserConfiguration xpc = new HTMLConfiguration();
		xpc
				.setProperty("http://cyberneko.org/html/properties/filters",
						filters);
		xpc.setProperty("http://cyberneko.org/html/properties/names/elems",
				"lower");

		XMLInputSource xis = new XMLInputSource(null, null, null,
				new StringReader(ret), null);

		try {
			xpc.parse(xis);
			retVal = sw.toString();
			retVal = StringEscapeUtils.unescapeHtml(retVal);
			retVal = retVal.replace("&apos;", "'");
		} catch (Exception e) {
			logger.error(e.getMessage() + ". HTML was: \n" + html);
		}

		return retVal;
	}
}
