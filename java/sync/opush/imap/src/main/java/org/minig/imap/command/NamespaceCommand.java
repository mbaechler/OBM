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

import org.minig.imap.NameSpaceInfo;
import org.minig.imap.command.parser.NamespaceParser;
import org.minig.imap.impl.IMAPResponse;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

public class NamespaceCommand extends SimpleCommand<NameSpaceInfo> {

	private static final NamespaceParser parser = Parboiled.createParser(NamespaceParser.class);
	
	public NamespaceCommand() {
		super("NAMESPACE");
	}

	@Override
	public void responseReceived(List<IMAPResponse> rs) {
		if (isOk(rs)) {
			IMAPResponse nsr = lookForResponse(rs);
			NamespaceParser parserInstance = parser.newInstance();
			RecoveringParseRunner<NameSpaceInfo> runner = new RecoveringParseRunner<NameSpaceInfo>(parserInstance.rule());
			ParsingResult<NameSpaceInfo> result = runner.run(nsr.getPayload());
			data = result.resultValue;
		}
	}

	private IMAPResponse lookForResponse(List<IMAPResponse> rs) {
		for (IMAPResponse ir : rs) {
			if (ir.getPayload().startsWith(NamespaceParser.expectedResponseStart)) {
				return ir;
			}
		}
		return null;
	}

}
