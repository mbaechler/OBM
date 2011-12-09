package org.obm.push.protocol;

import java.util.List;

import org.obm.push.exception.activesync.NoDocumentException;
import org.obm.push.protocol.bean.AutodiscoverProtocolException;
import org.obm.push.protocol.bean.AutodiscoverRequest;
import org.obm.push.protocol.bean.AutodiscoverResponse;
import org.obm.push.protocol.bean.AutodiscoverResponseError;
import org.obm.push.protocol.bean.AutodiscoverResponseServer;
import org.obm.push.protocol.bean.AutodiscoverResponseUser;
import org.obm.push.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

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
	
	public Document encodeResponse(AutodiscoverResponse autodiscoverResponse) throws AutodiscoverProtocolException {
		Document autodiscover = DOMUtils.createDoc(null, "Autodiscover");
		
		Element response = createAutodiscoverResponseElement(autodiscover);
		
		createAutodiscoverResponseCultureElement(response, autodiscoverResponse);
		createAutodiscoverResponseUserElement(response, autodiscoverResponse.getResponseUser());
		createAutodiscoverResponseActionElement(response, autodiscoverResponse);
		createAutodiscoverResponseErrorElement(response, autodiscoverResponse.getResponseError());
		
		return autodiscover;
	}

	public Document encodeErrorResponse(AutodiscoverResponseError error) {
		Document root = DOMUtils.createDoc(null, "Autodiscover");
		createAutodiscoverResponseErrorElement(root.getDocumentElement(), error);
		return root;
	}

	private Element createAutodiscoverResponseElement(Document autodiscover) {
		return DOMUtils.createElement(autodiscover.getDocumentElement(), "Response");
	}
	
	private void createAutodiscoverResponseCultureElement(Element response, AutodiscoverResponse autodiscoverResponse) {
		String responseCulture = autodiscoverResponse.getResponseCulture();
		if (responseCulture != null) {
			DOMUtils.createElementAndText(response, "Culture", responseCulture);
		}
	}
	
	private void createAutodiscoverResponseUserElement(Element response, AutodiscoverResponseUser user) 
			throws AutodiscoverProtocolException {
		
		if (user != null && !Strings.isNullOrEmpty(user.getEmailAddress())) {
			Element userElement = DOMUtils.createElement(response, "User");
			if (!Strings.isNullOrEmpty(user.getDisplayName())) {
				DOMUtils.createElementAndText(userElement, "DisplayName", user.getDisplayName());
			}
			DOMUtils.createElementAndText(userElement, "EMailAddress", user.getEmailAddress());
		} else {
			throw new AutodiscoverProtocolException("The user email address element is a required");
		}
	}
	
	private void createAutodiscoverResponseActionElement(Element response, AutodiscoverResponse autodiscoverResponse) {
		String actionRedirect = autodiscoverResponse.getActionRedirect();
		List<AutodiscoverResponseServer> listActionServer = autodiscoverResponse.getListActionServer();
		AutodiscoverResponseError actionError = autodiscoverResponse.getActionError();

		if (actionRedirect != null || listActionServer != null || actionError != null) {
			Element actionElement = DOMUtils.createElement(response, "Action");
			createAutodiscoverResponseRedirectElement(actionElement, actionRedirect);
			createAutodiscoverResponseServerSettingsElement(actionElement, listActionServer);
			createAutodiscoverResponseErrorActionElement(actionElement, actionError);
		}
		
	}

	private void createAutodiscoverResponseRedirectElement(Element actionElement, String actionRedirect) {
		if (!Strings.isNullOrEmpty(actionRedirect)) {
			DOMUtils.createElementAndText(actionElement, "Redirect", actionRedirect);
		}
	}
	
	private void createAutodiscoverResponseServerSettingsElement(Element actionElement, List<AutodiscoverResponseServer> listActionServer) {
		if (listActionServer != null && !listActionServer.isEmpty()) {
			Element settingsElement = DOMUtils.createElement(actionElement, "Settings");
			
			for (AutodiscoverResponseServer actionServer: listActionServer) {
				Element serverElement = DOMUtils.createElement(settingsElement, "Server");
				
				DOMUtils.createElementAndText(serverElement, "Type", actionServer.getType());
				DOMUtils.createElementAndText(serverElement, "Url", actionServer.getUrl());
				DOMUtils.createElementAndText(serverElement, "Name", actionServer.getName());
				DOMUtils.createElementAndText(serverElement, "ServerData", actionServer.getServerData());
			}
		}
	}
	
	private void createAutodiscoverResponseErrorActionElement(Element actionElement, AutodiscoverResponseError actionError) {
		if (actionError != null) {
			Element errorElement = DOMUtils.createElement(actionElement, "Error");
			DOMUtils.createElementAndText(errorElement, "Status", actionError.getStatus().asXmlValue());
			DOMUtils.createElementAndText(errorElement, "Message", actionError.getMessage());
			DOMUtils.createElementAndText(errorElement, "DebugData", actionError.getDebugData());
			DOMUtils.createElementAndText(errorElement, "ErrorCode", String.valueOf(actionError.getErrorCode()));
		}
	}
	
	private void createAutodiscoverResponseErrorElement(Element response, AutodiscoverResponseError responseError) {
		if (responseError != null) {
			Element errorElement = DOMUtils.createElement(response, "Error");
			DOMUtils.createElementAndText(errorElement, "ErrorCode", String.valueOf(responseError.getErrorCode()));
			DOMUtils.createElementAndText(errorElement, "Message", responseError.getMessage());
			DOMUtils.createElementAndText(errorElement, "DebugData", responseError.getDebugData());
		}
	}

}
