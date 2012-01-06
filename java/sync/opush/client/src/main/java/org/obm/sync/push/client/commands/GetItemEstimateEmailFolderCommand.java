package org.obm.sync.push.client.commands;

import java.io.IOException;

import javax.xml.parsers.FactoryConfigurationError;

import org.obm.push.utils.DOMUtils;
import org.obm.sync.push.client.AccountInfos;
import org.obm.sync.push.client.GetItemEstimateSingleFolderResponse;
import org.obm.sync.push.client.OPClient;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class GetItemEstimateEmailFolderCommand extends TemplateBasedCommand<GetItemEstimateSingleFolderResponse> {

	private final String syncKey;
	private final String collectionId;

	public GetItemEstimateEmailFolderCommand(String syncKey, String collectionId) throws SAXException, IOException, FactoryConfigurationError {
		super(NS.GetItemEstimate, "GetItemEstimate", "GetItemEstimateRequestEmail.xml");
		this.syncKey = syncKey;
		this.collectionId = collectionId;
	}

	@Override
	protected void customizeTemplate(AccountInfos ai, OPClient opc) {
		Element sk = DOMUtils.getUniqueElement(tpl.getDocumentElement(), "AirSync:SyncKey");
		sk.setTextContent(syncKey);
		Element collection = DOMUtils.getUniqueElement(tpl.getDocumentElement(), "CollectionId");
		collection.setTextContent(collectionId);
	}

	@Override
	protected GetItemEstimateSingleFolderResponse parseResponse(Element root) {
		int colId = Integer.parseInt(DOMUtils.getElementText(root, "CollectionId"));
		int estimate = Integer.parseInt(DOMUtils.getElementText(root, "Estimate"));
		int status = Integer.parseInt(DOMUtils.getElementText(root, "Status"));
		return new GetItemEstimateSingleFolderResponse(colId, estimate, status );
	}
}
