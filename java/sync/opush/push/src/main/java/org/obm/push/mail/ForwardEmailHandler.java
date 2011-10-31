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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.descriptor.BodyDescriptor;
import org.columba.ristretto.io.ByteBufferSource;
import org.columba.ristretto.io.CharSequenceSource;
import org.columba.ristretto.io.Source;
import org.columba.ristretto.message.LocalMimePart;
import org.columba.ristretto.message.MimeHeader;
import org.columba.ristretto.message.MimePart;
import org.columba.ristretto.message.MimeType;
import org.minig.mime.QuotedPrintableDecoderInputStream;
import org.obm.push.utils.FileUtils;

public class ForwardEmailHandler extends SendEmailHandler {

	private InputStream originMail;

	public ForwardEmailHandler(String defaultFrom, InputStream originMail) {
		super(defaultFrom);
		this.originMail = originMail;

	}

	@Override
	public void body(BodyDescriptor arg0, InputStream arg1)
			throws MimeException, IOException {
		Charset charset = null;
		try {
			charset = Charset.forName(arg0.getCharset());
		} catch (Throwable e) {}
		
		
		MimeHeader mimeHeader = new MimeHeader(header);
		mimeHeader.setMimeType(new MimeType("multipart", "mixed"));
		byte[] bbody = null;
		if ("QUOTED-PRINTABLE".equalsIgnoreCase(arg0.getTransferEncoding())) {
			arg1 = new QuotedPrintableDecoderInputStream(arg1);
		}
		if ("base64".equalsIgnoreCase(arg0.getTransferEncoding())) {
			byte[] b = FileUtils.streamBytes(arg1, false);
			byte[] bb = Base64.decodeBase64(b);
			
			if(charset == null){
				bbody = bb;
			} else {
				bbody = new String(bb, charset).getBytes();
			}
			this.root.getHeader().setContentTransferEncoding("8bit");
		} else {
			if(charset == null){
				charset = Charset.forName("UTF-8");
			}
			bbody=  FileUtils.streamBytes(arg1, false);
		}
		
		
		
		root = new LocalMimePart(mimeHeader);
		
		MimeHeader textHeader = new MimeHeader();
		textHeader.set("Content-Type", "text/plain; charset=UTF-8");
		LocalMimePart textPart = new LocalMimePart(textHeader);
		String body = "";
		if(charset != null){
			body = new String(bbody, charset);
		} else {
			body = new String(bbody);
		}
		CharSequenceSource css = new CharSequenceSource(body.trim());
		textPart.setBody(css);
		
		root.addChild(textPart);

		MimePart attachmentPart = prepareAttachement();
		root.addChild(attachmentPart);
	}

	private MimePart prepareAttachement()
			throws IOException, FileNotFoundException {
		String filename = "forwarded_message.eml";
		MimeHeader attachmentHeader = new MimeHeader("message", "rfc822");

		attachmentHeader.putContentParameter("name", filename);
		attachmentHeader.setContentDisposition("attachment");
		attachmentHeader.putDispositionParameter("filename", filename);

		LocalMimePart attachmentPart = new LocalMimePart(attachmentHeader);
		Source css = new ByteBufferSource(FileUtils.streamBytes(
				originMail, false));
		attachmentPart.setBody(css);

		return attachmentPart;
	}
}
