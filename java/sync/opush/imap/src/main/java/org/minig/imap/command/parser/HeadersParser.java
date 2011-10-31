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
package org.minig.imap.command.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.obm.push.utils.DOMUtils;

public class HeadersParser {

	private Map<String, String> parsedHeaders;
	private String currentLine;
	private StringBuilder currentValue;
	private String currentKey;
	
	public HeadersParser() {
		parsedHeaders = new HashMap<String, String>();
	}
	
	public Map<String, String> parseRawHeaders(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);

		while ((currentLine = br.readLine()) != null) {
			
			// collapse rfc822 headers into one line
			if (currentLine.length() <= 1) {
				continue;
			}
			char first = currentLine.charAt(0);
			if (Character.isWhitespace(first)) {
				int nbSpaces = countLeadingWhiteSpaces();
				pushDataInValue(" ");
				pushDataInValue(currentLine.substring(nbSpaces));
			} else {
				saveCurrentAsParsed();
				prepareForNext();

				int split = currentLine.indexOf(':');
				if (split > 0) {
					currentKey = currentLine.substring(0, split).toLowerCase();
					String value = currentLine.substring(split + 1).trim();
					pushDataInValue(value);
				}

			}
		}
		saveCurrentAsParsed();
		return parsedHeaders;
	}

	private void prepareForNext() {
		currentKey = null;
		currentValue = new StringBuilder();
	}

	private void saveCurrentAsParsed() {
		if (currentKey != null) {
			parsedHeaders.put(currentKey, DOMUtils.stripNonValidXMLCharacters(currentValue.toString()));			
		}
	}

	private int countLeadingWhiteSpaces() {
		int nbSpaces = 1;
		while (nbSpaces<currentLine.length() && Character.isWhitespace(currentLine.charAt(nbSpaces))) {
			nbSpaces += 1;
		}
		return nbSpaces;
	}

	private void pushDataInValue(String part) {
		if (currentKey == null) {
			throw new IllegalStateException("value without a key");
		}
		if (currentValue == null) {
			currentValue = new StringBuilder();
		}
		currentValue.append(part);
	}
}
