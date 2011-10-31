package org.obm.push.utils.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Preconditions;

public class Sets {

	/**
	 * This methods use the given comparator to perform a difference between col1 and col2.
	 * It uses {@link com.google.common.collect.Sets.difference} after wrapping col1 and col2 into TreeSets.
	 */
	public static <E> Set<E> difference(Collection<E> col1, Collection<E> col2, Comparator<E> comparator) {
		Preconditions.checkNotNull(col1, "col1");
		Preconditions.checkNotNull(col2, "col2");
		Preconditions.checkNotNull(comparator, "comparator");
		
		TreeSet<E> set1 = com.google.common.collect.Sets.newTreeSet(comparator);
		set1.addAll(col1);
		checkCollectionNoDuplicateEntry(col1, set1, "col1");
		TreeSet<E> set2 = com.google.common.collect.Sets.newTreeSet(comparator);
		set2.addAll(col2);
		checkCollectionNoDuplicateEntry(col2, set2, "col2");
		return com.google.common.collect.Sets.difference(set1, set2);
	}

	private static <E> void checkCollectionNoDuplicateEntry(Collection<E> col1, TreeSet<E> set1, String name) {
		if (col1.size() != set1.size()) {
			throw new IllegalStateException(name + " must not contain duplicate elements as evaluated by comparator");
		}
	}
	
	public static <E> Set<E> symmetricDifference(Collection<E> col1, Collection<E> col2, 
			Comparator<E> comparator, SymmetricDifferenceComparator<E> symmetricDifferenceComparator) {
		
		Iterator<E> from = sortedCollection(col1, comparator);
		Iterator<E> to = sortedCollection(col2, comparator);
		
		Set<E> difference = new HashSet<E>();
		while (from.hasNext()) {
			if (to.hasNext()) {
				E e1 = from.next();
				E e2 = to.next();
				if (!symmetricDifferenceComparator.equal(e1, e2)) {
					difference.add(e1);
				}
			}
		}
		return difference;
	}

	private static <E> Iterator<E> sortedCollection(Collection<E> col, Comparator<E> comparator) {
		TreeSet<E> set = new TreeSet<E>(comparator);
		set.addAll(col);
		return set.iterator();
	}

}