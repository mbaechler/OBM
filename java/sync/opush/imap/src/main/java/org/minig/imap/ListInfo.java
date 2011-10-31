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

package org.minig.imap;

import com.google.common.base.Objects;

public class ListInfo {

	private String name;

	private boolean selectable;
	private boolean createSubfolder;

	public ListInfo(String name, boolean selectable, boolean noInferiors) {
		super();
		this.name = name;
		this.selectable = selectable;
		this.createSubfolder = noInferiors;
	}

	public String getName() {
		return name;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public boolean canCreateSubfolder() {
		return createSubfolder;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(getClass())
				.add("name", name)
				.toString();
	}
	
}
