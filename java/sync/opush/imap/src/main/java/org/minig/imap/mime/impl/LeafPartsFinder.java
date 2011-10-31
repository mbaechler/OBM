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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import org.minig.imap.mime.IMimePart;
import org.minig.imap.mime.MimeAddress;

public class LeafPartsFinder {

	private Collection<IMimePart> leaves;
	private final boolean filterNested;
	private final IMimePart root;
	
	public LeafPartsFinder(IMimePart root, boolean depthFirst, boolean filterNested) {
		this.root = root;
		this.filterNested = filterNested;
		if (depthFirst) {
			leaves = new ArrayList<IMimePart>();
		} else {
			leaves = new TreeSet<IMimePart>(new Comparator<IMimePart>() {
				@Override
				public int compare(IMimePart o1, IMimePart o2) {
					MimeAddress firstAddr = o1.getAddress();
					MimeAddress secondAddr = o2.getAddress();
					int diffLevel = firstAddr.compareNestLevel(secondAddr);
					if (diffLevel != 0) {
						return diffLevel;
					}
					return firstAddr.getLastIndex() - secondAddr.getLastIndex();
				}
			});
		}
		buildLeafList(root);
	}

	
	private void buildLeafList(IMimePart mp) {
		if (mp.getChildren().isEmpty()) {
			leaves.add(mp);
		} else {
			if (mp != root && mp.isNested() && filterNested) {
				return;
			}
			for (IMimePart m : mp.getChildren()) {
				buildLeafList(m);
			}
		}
	}

	public Collection<IMimePart> getLeaves() {
		return leaves;
	}
	
}
