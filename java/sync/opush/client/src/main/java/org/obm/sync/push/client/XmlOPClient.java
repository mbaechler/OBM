package org.obm.sync.push.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerException;

import org.obm.push.utils.DOMUtils;
import org.obm.push.wbxml.WBXmlException;
import org.w3c.dom.Document;


public class XmlOPClient extends OPClient {

	public XmlOPClient(String loginAtDomain, String password, String devId,
			String devType, String userAgent, String url) {
		super(loginAtDomain, password, devId, devType, userAgent, url);
	}

	@Override
	protected String getRequestContentType() {
		return "text/xml";
	}
	
	@Override
	protected InputStream transformRequestDataToInputStream(String namespace, Document doc)
			throws WBXmlException, IOException {
		try {
			String xmlData = DOMUtils.serialise(doc);
			return new ByteArrayInputStream(xmlData.getBytes("UTF8"));
		} catch (TransformerException e) {
			throw new WBXmlException("Cannot serialize data to xml", e);
		}
	}
}
