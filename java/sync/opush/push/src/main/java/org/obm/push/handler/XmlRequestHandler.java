package org.obm.push.handler;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.FactoryConfigurationError;

import org.obm.push.backend.IContinuation;
import org.obm.push.bean.BackendSession;
import org.obm.push.impl.DOMDumper;
import org.obm.push.impl.Responder;
import org.obm.push.protocol.request.ActiveSyncRequest;
import org.obm.push.utils.DOMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class XmlRequestHandler implements IRequestHandler {

	protected Logger logger = LoggerFactory.getLogger(XmlRequestHandler.class);

	@Override
	public void process(IContinuation continuation, BackendSession bs,
			ActiveSyncRequest request, Responder responder) throws IOException {

		InputStream in = request.getInputStream();
		Document doc = null;
		if (in != null) {
			try {
				doc = DOMUtils.parse(in);
				DOMDumper.dumpXml(logger, doc);
			} catch (SAXException e) {
				logger.error("Error parsing command xml data.", e);
			} catch (FactoryConfigurationError e) {
				logger.error("Error parsing command xml data.", e);
			}
		}
		process(continuation, bs, doc, request, responder);
	}

	protected abstract void process(IContinuation continuation, BackendSession bs, 
			Document doc, ActiveSyncRequest request, Responder responder);

}
