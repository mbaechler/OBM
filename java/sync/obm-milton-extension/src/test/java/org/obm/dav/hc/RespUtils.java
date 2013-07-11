/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
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
