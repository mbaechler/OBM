package org.obm.push.protocol;

import org.obm.push.exception.activesync.NoDocumentException;
import org.obm.push.protocol.bean.AutodiscoverRequest;
import org.obm.push.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AutodiscoverProtocol {

	public AutodiscoverRequest getRequest(Document document) throws NoDocumentException {
		if (document == null) {
			throw new NoDocumentException("Document of Autodiscover request is null.");
		}
		
		Element root = document.getDocumentElement();
		
		Element emailAddressElement = DOMUtils.getUniqueElement(root, "EMailAddress");
		Element acceptableResponseSchElement = DOMUtils.getUniqueElement(root, "AcceptableResponseSchema");
		
		return new AutodiscoverRequest( emailAddressElement.getTextContent(),
										acceptableResponseSchElement.getTextContent());
	}

}
