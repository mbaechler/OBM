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
package org.obm.push.mail.smtp;

import org.columba.ristretto.smtp.SMTPProtocol;
import org.obm.locator.store.LocatorService;
import org.obm.push.bean.BackendSession;
import org.obm.push.exception.SmtpLocatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SmtpLocator {

	private static final Logger logger = LoggerFactory.getLogger(SmtpLocator.class);
	private final LocatorService locatorService;

	@Inject
	private SmtpLocator(LocatorService locatorService) {
		this.locatorService = locatorService;
	}
	
	public SMTPProtocol getSmtpClient(BackendSession bs)
			throws SmtpLocatorException {
		String smtpHost = locatorService.getServiceLocation("mail/smtp_out",
				bs.getLoginAtDomain());
		if (smtpHost == null) {
			throw new SmtpLocatorException("Smtp server cannot be discovered");
		}
		logger.info("Using " + smtpHost + " as smtp host.");
		SMTPProtocol proto = new SMTPProtocol(smtpHost);
		return proto;
	}

}
