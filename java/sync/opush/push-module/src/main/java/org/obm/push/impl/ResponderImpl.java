package org.obm.push.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.obm.push.utils.DOMUtils;
import org.obm.push.utils.FileUtils;
import org.obm.push.wbxml.WBXMLTools;
import org.obm.push.wbxml.WBXmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class ResponderImpl implements Responder {

	private static final Logger logger = LoggerFactory.getLogger(ResponderImpl.class);

	private HttpServletResponse resp;

	public ResponderImpl(HttpServletResponse resp) {
		this.resp = resp;
	}

	@Override
	public void sendWBXMLResponse(String defaultNamespace, Document doc) {
		logger.debug("response: send response");
		if (logger.isDebugEnabled()) {
			DOMDumper.dumpXml(logger, doc);
		}
		
		try {
			byte[] wbxml = WBXMLTools.toWbxml(defaultNamespace, doc);
			writeData(wbxml, "application/vnd.ms-sync.wbxml");	
		} catch (WBXmlException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendXMLResponse(String defaultNamespace, Document doc) {
		DOMDumper.dumpXml(logger, doc);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DOMUtils.serialise(doc, out);
			byte[] ret = out.toByteArray();
			writeData(ret, "text/xml");	
		} catch (TransformerException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void writeData(byte[] data, String type) {
		resp.setContentType(type);
		resp.setContentLength(data.length);
		try {
			ServletOutputStream out = resp.getOutputStream();
			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendResponseFile(String contentType, InputStream file) {
		logger.debug("response: send file");
		try {
			byte[] b = FileUtils.streamBytes(file, false);
			resp.setContentType(contentType);
			resp.setContentLength(b.length);
			ServletOutputStream out = resp.getOutputStream();
			out.write(b);
			out.flush();
			out.close();
			resp.setStatus(HttpServletResponse.SC_OK);	
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendError(int statusCode) {
		logger.debug("response: send error");
		try {
			resp.sendError(statusCode);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendNoChangeResponse() {
		logger.debug("response: send no changes");
	}
	
}