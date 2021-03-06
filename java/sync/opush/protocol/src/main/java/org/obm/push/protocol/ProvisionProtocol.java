package org.obm.push.protocol;

import javax.xml.parsers.FactoryConfigurationError;

import org.obm.push.bean.ProvisionStatus;
import org.obm.push.exception.InvalidPolicyKeyException;
import org.obm.push.protocol.bean.ProvisionRequest;
import org.obm.push.protocol.bean.ProvisionResponse;
import org.obm.push.protocol.provisioning.Policy;
import org.obm.push.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ProvisionProtocol {

	public ProvisionRequest getRequest(Document doc) throws InvalidPolicyKeyException {
		String policyType = DOMUtils.getUniqueElement(doc.getDocumentElement(),	"PolicyType").getTextContent();
		Element pKeyElem = DOMUtils.getUniqueElement(doc.getDocumentElement(), "PolicyKey");
		long policyKey = getPolicyKey(pKeyElem);
		return new ProvisionRequest(policyType, policyKey);
	}

	private long getPolicyKey(Element pKeyElem) throws InvalidPolicyKeyException {
		long policyKey = 0;
		if (pKeyElem != null) {
			try {
				policyKey = Long.valueOf( pKeyElem.getTextContent() );
			} catch (NumberFormatException e) {
				throw new InvalidPolicyKeyException(e);
			}
		}
		return policyKey;
	}

	public Document encodeResponse(ProvisionResponse provisionResponse) throws FactoryConfigurationError {
		Document ret = DOMUtils.createDoc(null, "Provision");
		Element root = ret.getDocumentElement();
		DOMUtils.createElementAndText(root, "Status", ProvisionStatus.SUCCESS.asXmlValue());
		Element policies = DOMUtils.createElement(root, "Policies");
		Element policyNode = DOMUtils.createElement(policies, "Policy");
		DOMUtils.createElementAndText(policyNode, "PolicyType", provisionResponse.getPolicyType());
		DOMUtils.createElementAndText(policyNode, "Status", provisionResponse.getStatus().asXmlValue());
		
		String policyKey = String.valueOf(provisionResponse.getPolicyKey());
		if (policyKey != null) {
			DOMUtils.createElementAndText(policyNode, "PolicyKey", policyKey);
		}
		
		Policy policy = provisionResponse.getPolicy();
		if (policy != null) {
			Element data = DOMUtils.createElement(policyNode, "Data");
			policy.serialize(data);
		}
		return ret;
	}
	
	public Document encodeErrorResponse(ProvisionStatus errorStatus) {
		Document document = DOMUtils.createDoc(null, "Provision");
		Element root = document.getDocumentElement();
		DOMUtils.createElementAndText(root, "Status", errorStatus.asXmlValue());
		return document;
	}
	
}
