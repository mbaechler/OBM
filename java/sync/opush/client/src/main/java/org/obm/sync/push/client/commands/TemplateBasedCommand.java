/* ***** BEGIN LICENSE BLOCK *****
 *
 * %%
 * Copyright (C) 2000 - 2011 Linagora
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK ***** */
package org.obm.sync.push.client.commands;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.obm.push.utils.DOMUtils;
import org.obm.sync.push.client.AccountInfos;
import org.obm.sync.push.client.IEasCommand;
import org.obm.sync.push.client.OPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class TemplateBasedCommand<T> implements IEasCommand<T> {

	protected Document tpl;
	protected Boolean fromTemplate;
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private String namespace;
	private String cmd;

	protected TemplateBasedCommand(NS namespace, String cmd, String templateName) {
		this.fromTemplate = true;
		this.namespace = namespace.toString();
		this.cmd = cmd;
		InputStream in = loadDataFile(templateName);
		if (in != null) {
			try {
				this.tpl = DOMUtils.parse(in);
			} catch (Exception e) {
				logger.error("error loading template " + templateName, e);
			}
		} else {
			logger.error("template " + templateName + " not found.");
		}
	}

	protected TemplateBasedCommand(NS namespace, String cmd, Document document) {
		this.fromTemplate = false;
		this.namespace = namespace.toString();
		this.cmd = cmd;
		this.tpl = document;
	}

	@Override
	public T run(AccountInfos ai, OPClient opc, HttpClient hc) throws Exception {
		if (fromTemplate) {
			customizeTemplate(ai, opc);
		}
		Document response = opc.postXml(namespace, tpl, cmd);
		T ret = null;
		if (response != null) {
			ret = parseResponse(response.getDocumentElement());
		}
		return ret;
	}

	protected abstract void customizeTemplate(AccountInfos ai, OPClient opc);

	protected abstract T parseResponse(Element responseRootElem);

	private InputStream loadDataFile(String name) {
		return TemplateBasedCommand.class.getClassLoader().getResourceAsStream(
				"data/" + name);
	}

}
