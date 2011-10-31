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
package org.obm.push.mail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.descriptor.BodyDescriptor;
import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MimeHeader;
import org.minig.mime.QuotedPrintableDecoderInputStream;
import org.obm.push.bean.MSEmail;
import org.obm.push.bean.MSEmailBodyType;
import org.obm.push.utils.FileUtils;

public class ReplyEmailHandler extends SendEmailHandler {

	private MSEmail originMail;

	public ReplyEmailHandler(String defaultFrom, MSEmail originMail) {
		super(defaultFrom);
		this.originMail = originMail;

	}

	@Override
	public void body(BodyDescriptor arg0, InputStream arg1)
			throws MimeException, IOException {
		MimeHeader mimeHeader = new MimeHeader(header);
		root = new LocalMimePart(mimeHeader);
		StringBuilder body = new StringBuilder();

		if ("QUOTED-PRINTABLE".equalsIgnoreCase(arg0.getTransferEncoding())) {
			arg1 = new QuotedPrintableDecoderInputStream(arg1);
		}

		if ("base64".equalsIgnoreCase(arg0.getTransferEncoding())) {
			Charset charset = null;
			try {
				charset = Charset.forName(arg0.getCharset());
			} catch (Throwable e) {
			}
			byte[] b = FileUtils.streamBytes(arg1, false);
			byte[] bb = Base64.decodeBase64(b);
			if (charset == null) {
				body.append(new String(bb));
			} else {
				body.append(new String(bb, charset));
			}
		} else {
			body.append(FileUtils.streamString(arg1, false));
		}

		String oldBody = this.originMail.getBody().getValue(
				MSEmailBodyType.PlainText);
		if (oldBody != null) {
			body.append("\n");
			for(String next : oldBody.split("\n")){
				body.append("> "+next);
			}
		}
		CharSequenceSource css = new CharSequenceSource(body.toString().trim());
		root.setBody(css);
	}
}
