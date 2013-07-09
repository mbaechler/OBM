/* ***** BEGIN LICENSE BLOCK *****
 * 
 * Copyright (C) 2013  Linagora
 *
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version, provided you comply 
 * with the Additional Terms applicable for OBM connector by Linagora 
 * pursuant to Section 7 of the GNU Affero General Public License, 
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain 
 * the “Message sent thanks to OBM, Free Communication by Linagora” 
 * signature notice appended to any and all outbound messages 
 * (notably e-mail and meeting requests), (ii) retain all hypertext links between 
 * OBM and obm.org, as well as between Linagora and linagora.com, and (iii) refrain 
 * from infringing Linagora intellectual property rights over its trademarks 
 * and commercial brands. Other Additional Terms apply, 
 * see <http://www.linagora.com/licenses/> for more details. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License 
 * for more details. 
 *
 * You should have received a copy of the GNU Affero General Public License 
 * and its applicable Additional Terms for OBM along with this program. If not, 
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License version 3 
 * and <http://www.linagora.com/licenses/> for the Additional Terms applicable to 
 * OBM connectors. 
 * 
 * ***** END LICENSE BLOCK ***** */
package org.obm.dav.hc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

public class RespUtils {

    public static Namespace NS_DAV = Namespace.getNamespace("D", "DAV:");
    
    public Document getResponseAsDocument(InputStream in) {
        try {
            Document document = RespUtils.getJDomDocument(in);
            return document;
        } catch (JDOMException e) {
            throw Throwables.propagate(e);
        }
    }    
    
    public static QName davName(String localName) {
        return new QName(NS_DAV.getURI(), localName, NS_DAV.getPrefix());
    }    
    
    
    public static String asString(Element el, String name) {
        Element elChild = el.getChild(name, NS_DAV);
        return elementToString(elChild);
    }

    public static String asString(Element el, String name, Namespace ns) {
        Element elChild = el.getChild(name, ns);
        return elementToString(elChild);
    }    

	private static String elementToString(Element elChild) {
		if (elChild == null) {
            return null;
        }
        return elChild.getText();
	}
    
    public static Long asLong(Element el, String name) {
        return stringToLong(asString(el, name));
    }
    
    public static Long asLong(Element el, String name, Namespace ns) {
        return stringToLong(asString(el, name, ns));
    }    

	private static Long stringToLong(String s) {
		if (Strings.isNullOrEmpty(s)) {
        	return null;
        }
        return Long.parseLong(s);
	}

    public static boolean hasChild(Element el, String name) {
        if (el == null) {
        	return false;
        }
        return !getElements(el, name).isEmpty();
    }    
    

    public static  List<Element> getElements(Element root, String name) {
        List<Element> list = new ArrayList<Element>();
        Iterator<?> it = root.getDescendants(new ElementFilter(name));
        while (it.hasNext()) {
            Object o = it.next();
            if( o instanceof Element) {
                list.add((Element)o);
            }
        }
        return list;
    }    
    
    public static  org.jdom.Document getJDomDocument(InputStream in) throws JDOMException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			IOUtils.copy(in, outputStream);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
		
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        try {
            SAXBuilder builder = new SAXBuilder();
            builder.setExpandEntities(false);
            return builder.build(inputStream);
        } catch (IOException e) {
			throw Throwables.propagate(e);
        }
    }        
}
