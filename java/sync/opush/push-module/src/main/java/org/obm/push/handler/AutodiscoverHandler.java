package org.obm.push.handler;

import org.obm.push.backend.IContinuation;
import org.obm.push.bean.BackendSession;
import org.obm.push.impl.Responder;
import org.obm.push.protocol.request.ActiveSyncRequest;
import org.w3c.dom.Document;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AutodiscoverHandler extends XmlRequestHandler {

	@Inject
	private AutodiscoverHandler() { }

	@Override
	protected void process(IContinuation continuation, BackendSession bs,
			Document doc, ActiveSyncRequest request, Responder responder) {
		// TODO Auto-generated method stub
	}

}
