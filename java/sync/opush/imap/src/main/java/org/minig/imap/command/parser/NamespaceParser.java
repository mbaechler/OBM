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
package org.minig.imap.command.parser;

import java.util.Collections;
import java.util.List;

import org.minig.imap.NameSpaceInfo;
import org.minig.imap.impl.MailboxNameUTF7Converter;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

import com.google.common.collect.Lists;

@BuildParseTree
public class NamespaceParser extends AbstractImapBaseParser {
	
	public static final String expectedResponseStart = "* NAMESPACE";
	
	public Rule rule() {
		return Sequence(namespaceCommand(), 
				namespace(), ACTION(setPersonalNamespaces()), 
				whitespaces(),
				namespace(), ACTION(setOtherUserNamespaces()),
				whitespaces(),
				namespace(), ACTION(setSharedFolderNamespaces()));
	}
	
	boolean setPersonalNamespaces() {
		swap();
		NameSpaceInfo nsi = (NameSpaceInfo) pop();
		nsi.setPersonal((List<String>) pop());
		push(nsi);
		return true;
	}

	boolean setOtherUserNamespaces() {
		swap();
		NameSpaceInfo nsi = (NameSpaceInfo) pop();
		nsi.setOtherUsers((List<String>) pop());
		push(nsi);
		return true;
	}

	boolean setSharedFolderNamespaces() {
		swap();
		NameSpaceInfo nsi = (NameSpaceInfo) pop();
		nsi.setMailShares((List<String>) pop());
		push(nsi);
		return true;
	}

	
	Rule namespace() {
		return FirstOf(
				Sequence(nilNoStack(), push(Collections.emptyList())),
				group());
	}
	
	Rule group() {
		return Sequence('(', 
				Sequence(push(Lists.newArrayList()),
						OneOrMore(entry())),
				')');
	}

	boolean addEntryToList() {
		String expression = (java.lang.String)pop();
		List<String> list = (List<String>)peek();
		list.add(MailboxNameUTF7Converter.decode(expression));
		return true;
	}
	
	Rule entry() {
		return Sequence('(', 
				string(), ACTION(addEntryToList()),
				whitespaces(), 
				stringNoStack(),
				ZeroOrMore(extension()),
				')',
				whitespaces());
	}
	
	Rule extension() {
		return Sequence(whitespaces(), 
				stringNoStack(),
				whitespaces(),
				'(', stringNoStack(),
				ZeroOrMore(whitespaces(), stringNoStack()),
				')'
			);
	}
	
	Rule namespaceCommand() {
		return Sequence(String(expectedResponseStart), whitespaces(), push(new NameSpaceInfo())); 
	}
}