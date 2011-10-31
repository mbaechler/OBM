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
package org.minig.imap.mime;

import java.util.Collection;
import java.util.List;

public interface IMimePart {

	void addPart(IMimePart child);

	String getMimeType();

	String getMimeSubtype();

	List<IMimePart> getChildren();

	List<IMimePart> getSibling();
	
	MimeAddress getAddress();
	
	MimeAddress getAddressInternal();

	Collection<BodyParam> getBodyParams();

	BodyParam getBodyParam(final String param);

	IMimePart getParent();

	Collection<IMimePart> listLeaves(boolean depthFirst, boolean filterNested);

	void defineParent(IMimePart parent, int index);

	String getFullMimeType();

	boolean isInvitation();

	String getContentTransfertEncoding();
	
	String getCharset();

	String getContentId();

	boolean isCancelInvitation();

	void setBodyParams(Collection<BodyParam> newParams);

	void setMimeType(MimeType mimetype);

	String getName();

	boolean isMultipart();

	String getMultipartSubtype();

	void setMultipartSubtype(String subtype);

	boolean isAttachment();

	boolean isNested();
	
}