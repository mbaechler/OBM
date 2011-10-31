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
package org.obm.mail.message;

import java.io.IOException;
import java.io.InputStream;

import org.minig.imap.IMAPHeaders;
import org.minig.imap.mime.IMimePart;
import org.minig.imap.mime.MimeMessage;
import org.obm.mail.conversation.MailMessage;
import org.obm.mail.imap.StoreException;


public interface MessageFetcher {

	IMAPHeaders fetchPartHeaders(MimeMessage message, IMimePart mimePart) throws IOException, StoreException;

	InputStream fetchPart(MimeMessage message, IMimePart mimePart) throws IOException, StoreException;

	InputStream fetchPart(MailMessage message, IMimePart part) throws IOException, StoreException;

}
