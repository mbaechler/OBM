package org.obm.sync.push.client.commands;

import java.util.Locale;

import org.obm.push.utils.DOMUtils;
import org.obm.sync.push.client.AccountInfos;
import org.obm.sync.push.client.AutodiscoverResponse;
import org.obm.sync.push.client.OPClient;
import org.w3c.dom.Element;

public class AutodiscoverCommand extends TemplateBasedCommand<AutodiscoverResponse> {

	private final String email;

	public AutodiscoverCommand(String email) {
		super(NS.Autodiscover, "Autodiscover", "AutodiscoverRequest.xml");
		this.email = email;
	}
	
	@Override
	protected void customizeTemplate(AccountInfos ai, OPClient opc) {
		setRequestEmail();
	}

	private void setRequestEmail() {
		Element sk = DOMUtils.getUniqueElement(tpl.getDocumentElement(), "EMailAddress");
		sk.setTextContent(email);
	}

	@Override
	protected AutodiscoverResponse parseResponse(Element root) {
		AutodiscoverResponse response = new AutodiscoverResponse();
		
		String[] culture = DOMUtils.getElementText(root, "A:Culture").split(":");
		response.setCulture(new Locale(culture[0], culture[1]));
		response.setUserDisplayName(DOMUtils.getElementText(root, "DisplayName"));
		response.setUserEMailAddress(DOMUtils.getElementText(root, "EMailAddress"));
		response.setServerType(DOMUtils.getElementText(root, "Type"));
		response.setServerUrl(DOMUtils.getElementText(root, "Url"));
		response.setServerName(DOMUtils.getElementText(root, "Name"));
		
		return response;
	}

}
