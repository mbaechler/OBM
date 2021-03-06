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

package org.obm.caldav.server.propertyHandler.impl;

import org.obm.caldav.server.IBackend;
import org.obm.caldav.server.NameSpaceConstant;
import org.obm.caldav.server.impl.DavRequest;
import org.obm.caldav.server.propertyHandler.DavPropertyHandler;
import org.obm.caldav.server.propertyHandler.PropfindPropertyHandler;
import org.obm.caldav.server.share.DavComponent;
import org.obm.caldav.server.share.DavComponentType;
import org.obm.caldav.server.share.CalDavToken;
import org.w3c.dom.Element;

public class GetContentType extends DavPropertyHandler implements PropfindPropertyHandler {

	@Override
	public void appendPropertyValue(Element prop, CalDavToken t, DavRequest req,
			IBackend proxy, DavComponent comp) {
		if(DavComponentType.VEVENT.equals(comp.getType()) || DavComponentType.VTODO.equals(comp.getType())){
			appendElement(prop, "getcontenttype",
				NameSpaceConstant.DAV_NAMESPACE_PREFIX).setTextContent(
				"text/calendar");
		} else {
			appendElement(prop, "getcontenttype",
			NameSpaceConstant.DAV_NAMESPACE_PREFIX).setTextContent(
				"httpd/unix-directory");
		}
	}

	@Override
	public boolean isUsed() {
		return true;
	}

}
