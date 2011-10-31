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
package org.minig.imap;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.minig.imap.mime.BodyParam;
import org.minig.imap.mime.MimeMessage;
import org.minig.imap.mime.MimePart;
import org.minig.imap.mime.MimeType;

import com.google.common.collect.Sets;

public class MimeMessageFactory {

	private static <T extends MimePart> T fillSimpleMimePart(T mimePart, String mimeType, String mimeSubtype, String contentId, String encoding, Map<String, String> bodyParams, MimePart... parts) {
		mimePart.setMimeType(new MimeType(mimeType, mimeSubtype));
		HashSet<BodyParam> params = Sets.newHashSet();
		for (Entry<String, String> entry: bodyParams.entrySet()) {
			params.add(new BodyParam(entry.getKey(), entry.getValue()));
		}
		mimePart.setBodyParams(params);
		for (MimePart part: parts) {
			mimePart.addPart(part);
		}
		mimePart.setContentId(contentId);
		mimePart.setContentTransfertEncoding(encoding);
		return mimePart;
	}
	
	public static MimePart createSimpleMimePart(String mimeType, String mimeSubtype, String contentId, String encoding, Map<String, String> bodyParams, MimePart... parts) {
		MimePart tree = new MimePart();
		fillSimpleMimePart(tree, mimeType, mimeSubtype, contentId, encoding, bodyParams, parts);
		return tree;
	}
	
	public static MimeMessage createSimpleMimeTree(String mimeType, String mimeSubtype, String contentId, String encoding, Map<String, String> bodyParams, MimePart... parts) {
		MimeMessage tree = new MimeMessage(createSimpleMimePart(mimeType, mimeSubtype, contentId, encoding, bodyParams, parts));
		return tree;
	}

	
}
