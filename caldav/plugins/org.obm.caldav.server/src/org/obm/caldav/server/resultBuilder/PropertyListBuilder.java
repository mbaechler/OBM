/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 2.0
 *
 * The contents of this file are subject to the GNU General Public
 * License Version 2 or later (the "GPL").
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is
 *   obm.org project members
 *
 * ***** END LICENSE BLOCK ***** */

package org.obm.caldav.server.resultBuilder;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.obm.caldav.server.IProxy;
import org.obm.caldav.server.NameSpaceConstant;
import org.obm.caldav.server.impl.DavRequest;
import org.obm.caldav.server.propertyHandler.PropfindPropertyHandler;
import org.obm.caldav.server.share.Token;
import org.obm.caldav.utils.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class PropertyListBuilder {
	
	private Log logger = LogFactory.getLog(PropertyListBuilder.class);
	
	public Document build(Token t, DavRequest req,Map<String, PropfindPropertyHandler> propertiesHandler, Set<String> toLoad, IProxy proxy) {
		try {
			Document ret = DOMUtils.createDoc(NameSpaceConstant.DAV_NAMESPACE, "D:multistatus");
			Element r = ret.getDocumentElement();
			r.setAttribute("xmlns:D", NameSpaceConstant.DAV_NAMESPACE);
			r.setAttribute("xmlns:CS", NameSpaceConstant.CALENDARSERVER_NAMESPACE);
			r.setAttribute("xmlns:C", NameSpaceConstant.CALDAV_NAMESPACE);
			r.setAttribute("xmlns", NameSpaceConstant.CALDAV_NAMESPACE);
			Element response = DOMUtils.createElement(r, "D:response");
			DOMUtils.createElementAndText(response, "D:href", req.getHref());
			Element pStat = DOMUtils.createElement(response, "D:propstat");
			Element p = DOMUtils.createElement(pStat, "D:prop");
			
			for (String s : toLoad) {
				Element val = DOMUtils.createElement(p, s);
				PropfindPropertyHandler dph = propertiesHandler.get(s);
				if(dph != null){
					dph.appendPropertyValue(val, t, req, proxy);
				} else {
					logger.warn("the Property ["+s+"] is not implemented");
				}
			}
			DOMUtils.createElementAndText(pStat, "D:status", "HTTP/1.1 200 OK");
			
			return ret;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}

	}

}
