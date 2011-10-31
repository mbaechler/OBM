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
package org.obm.push.store;

import java.util.Collection;
import java.util.Set;

import org.obm.push.bean.Credentials;
import org.obm.push.bean.Device;
import org.obm.push.bean.ItemChange;

public interface UnsynchronizedItemDao {
	
	void storeItemsToAdd(Credentials credentials, Device device, int collectionId, Collection<ItemChange> ic);

	Set<ItemChange> listItemsToAdd(Credentials credentials, Device device, int collectionId);

	void clearItemsToAdd(Credentials credentials, Device device, int collectionId);
	
	void storeItemsToRemove(Credentials credentials, Device device, int collectionId, Collection<ItemChange> ic);

	Set<ItemChange> listItemsToRemove(Credentials credentials, Device device, int collectionId);

	void clearItemsToRemove(Credentials credentials, Device device, int collectionId);

}
