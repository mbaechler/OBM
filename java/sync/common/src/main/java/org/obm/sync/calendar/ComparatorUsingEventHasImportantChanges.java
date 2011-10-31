package org.obm.sync.calendar;

import org.obm.push.utils.collection.SymmetricDifferenceComparator;

public class ComparatorUsingEventHasImportantChanges implements SymmetricDifferenceComparator<Event> {

	@Override
	public boolean equal(Event o1, Event o2) {
		return !o1.hasImportantChanges(o2);
	}

}
