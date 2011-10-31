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
package org.minig.imap.mime.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

import org.minig.imap.EncodedWord;
import org.minig.imap.mime.BodyParam;

import com.google.common.base.Function;


public class BodyParamParser {
	
	public static BodyParam parse(String key, String value) {
		return new BodyParamParser(key, value, new Function<String, String>() {
			@Override
			public String apply(String input) {
				return input;
			}
		}).parse();
	}
	
	private final String key;
	private final String value;
	private String decodedKey;
	private String decodedValue;
	private final Function<String, String> keyRewriter;
	
	public BodyParamParser(String key, String value, Function<String, String> keyRewriter) {
		this.key = key;
		this.value = value;
		this.keyRewriter = keyRewriter;
	}
	
	public BodyParam parse() {
		if (key.endsWith("*")) {
			decodedKey = key.substring(0, key.length() - 1);
			decodedValue = decodeAsterixEncodedValue();
		} else {
			decodedKey = key;
			decodedValue = decodeQuotedPrintable();
		}
		return new BodyParam(keyRewriter.apply(decodedKey), decodedValue);
	}
	
	
	private String decodeAsterixEncodedValue() {
		final int firstQuote = value.indexOf("'");
		final int secondQuote = value.indexOf("'", firstQuote + 1);
		final String charsetName = value.substring(0, firstQuote);
		final String text = value.substring(secondQuote + 1);
		try {
			Charset charset = Charset.forName(charsetName);
			return URLDecoder.decode(text, charset.displayName());
		} catch (UnsupportedEncodingException e) {
		} catch (IllegalCharsetNameException e) {
		} catch (IllegalArgumentException e) {
		}
		return text;
	}

	private String decodeQuotedPrintable() {
		return EncodedWord.decode(value).toString();
	}

}
