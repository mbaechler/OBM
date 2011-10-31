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

package org.minig.imap.command;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.minig.imap.QuotaInfo;
import org.minig.imap.impl.IMAPResponse;

public class QuotaRootCommand extends SimpleCommand<QuotaInfo> {

	public QuotaRootCommand(String mailbox) {
		super("GETQUOTAROOT " + toUtf7(mailbox));
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) {
		data = new QuotaInfo();
		if (isOk(rs)) {
			Pattern p = Pattern.compile("\\* QUOTA .* \\(STORAGE ");
			for (IMAPResponse imapr : rs) {
				if (logger.isDebugEnabled()) {
					logger.debug("Payload " + imapr.getPayload());
				}
				Matcher m = p.matcher(imapr.getPayload());
				if (m.find()) {
					String rep = m.replaceAll("").replaceAll("\\)", "");
					String[] tab = rep.split(" ");
					if (tab.length == 2) {
						data = new QuotaInfo(Integer.parseInt(tab[0]), Integer
								.parseInt(tab[1]));
					}
				}
			}
		}
	}

}
