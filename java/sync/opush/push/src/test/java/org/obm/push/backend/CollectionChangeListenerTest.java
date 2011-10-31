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
package org.obm.push.backend;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import org.junit.Test;
import org.obm.push.bean.SyncCollection;
import org.obm.push.bean.ChangedCollections;
import com.google.common.collect.ImmutableSet;

public class CollectionChangeListenerTest {

	@Test
	public void testMonitorOf() {
		String matchString = "mypath";
		Set<SyncCollection> monitored = ImmutableSet.of(new SyncCollection(1, matchString), new SyncCollection(2, "dontmatch"));
		Set<SyncCollection> notify = ImmutableSet.of(new SyncCollection(0, matchString));
		CollectionChangeListener collectionChangeListener = new CollectionChangeListener(null, null, monitored);
		ChangedCollections changed = new ChangedCollections(new Date(), notify);
		boolean result = collectionChangeListener.monitorOneOf(changed);
		assertTrue(result);
	}

	@Test
	public void testMonitorOfDontMatch() {
		ImmutableSet<SyncCollection> monitored = ImmutableSet.of(new SyncCollection(1, "mypath"), new SyncCollection(2, "dontmatch"));
		ImmutableSet<SyncCollection> notify = ImmutableSet.of(new SyncCollection(0, "anotherpath"));
		CollectionChangeListener collectionChangeListener = new CollectionChangeListener(null, null, monitored);
		ChangedCollections changed = new ChangedCollections(new Date(), notify);
		boolean result = collectionChangeListener.monitorOneOf(changed);
		assertTrue(!result);
	}
}
