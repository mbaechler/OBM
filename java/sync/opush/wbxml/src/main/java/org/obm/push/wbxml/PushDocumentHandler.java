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
package org.obm.push.wbxml;

import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class PushDocumentHandler implements ContentHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(PushDocumentHandler.class);

	private Document doc;
	private Stack<Element> elems;

	public PushDocumentHandler() {
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			doc = db.newDocument();
			elems = new Stack<Element>();
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void characters(char[] data, int off, int count) throws SAXException {
		Element cur = elems.peek();
		cur.setTextContent(new String(data, off, count));
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		elems.pop();
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes arg1)
			throws SAXException {
		Element newE = doc.createElement(qName);

		Element parent = null;
		if (!elems.isEmpty()) {
			parent = elems.peek();
		}

		if (parent != null) {
			parent.appendChild(newE);
		} else {
			doc.appendChild(newE);
		}
		elems.add(newE);

		for (int i = 0; i < arg1.getLength(); i++) {
			String att = arg1.getQName(i);
			String val = arg1.getValue(i);
			newE.setAttribute(att, val);
		}
	}
	
	public Document getDocument() {
		return doc;
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

}
