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
package org.obm.push.tnefconverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;

import net.freeutils.tnef.CompressedRTFInputStream;

import org.apache.commons.codec.binary.Base64;
import org.obm.push.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTFUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(RTFUtils.class);

	public static String getFolderId(String devId, String dataClass) {
		return devId + "\\" + dataClass;
	}

	public static String extractB64CompressedRTF(String b64) {
		String ret = "";
		try {
			byte[] bin = Base64.decodeBase64(b64);
			if (bin.length > 0) {
				ByteArrayInputStream in = new ByteArrayInputStream(bin);
				CompressedRTFInputStream cin = new CompressedRTFInputStream(in);

				String rtfDecompressed = FileUtils.streamString(cin, true);
				ret = extractRtfText(new ByteArrayInputStream(rtfDecompressed
						.getBytes()));
			}
		} catch (Exception e) {
			logger.error("error extracting compressed rtf", e);
		}
		return ret;
	}

	public static String extractCompressedRTF(InputStream in) {
		String ret = "";
		try {
			CompressedRTFInputStream cin = new CompressedRTFInputStream(in);
			String rtfDecompressed = FileUtils.streamString(cin, true);
			ret = extractRtfText(new ByteArrayInputStream(rtfDecompressed
					.getBytes()));
		} catch (Exception e) {
			logger.error("error extracting compressed rtf", e);
		}
		return ret;
	}

	private static String extractRtfText(InputStream stream)
			throws IOException, BadLocationException {
		RTFEditorKit kit = new RTFEditorKit();
		Document doc = kit.createDefaultDocument();
		kit.read(stream, doc, 0);

		return doc.getText(0, doc.getLength());
	}

}
