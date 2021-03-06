package org.obm.push.utils.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

/**
 * Abstract helper. It helps with inserting collections as parameters in a
 * {@link PreparedStatement}. Concrete subclasses should implement
 * {@link #insertValue(Object, PreparedStatement, int)}.
 * 
 * @param <V>
 *            the type of the collection elements to insert.
 */
public abstract class AbstractSQLCollectionHelper<V> {
	private Collection<V> values;

	public AbstractSQLCollectionHelper(Collection<V> values) {
		this.values = values;
	}

	/**
	 * Returns a string of placeholders for inserting into an SQL query.
	 * @return a {@link String} with the format "?, ?, ...".
	 */
	public String asPlaceHolders() {
		if (values.isEmpty()) {
			return "?";
		} else {
			List<String> questionMarks = Collections.nCopies(values.size(), "?");
			String placeHolders = Joiner.on(", ").join(questionMarks);
			return placeHolders;
		}
	}

	/**
	 * Inserts each value into a {@link PreparedStatement}.
	 */
	public int insertValues(PreparedStatement st, int parameterCount) throws SQLException {
		if (values.isEmpty()) {
			insertValue(getZeroValue(), st, parameterCount);
			parameterCount++;
		} else {
			for (V value : values) {
				insertValue(value, st, parameterCount);
				parameterCount++;
			}
		}
		return parameterCount;
	}

	protected abstract V getZeroValue();

	protected abstract void insertValue(V value, PreparedStatement statement,
			int parameterCount) throws SQLException;

	public Collection<V> getValues() {
		return values;
	}
}
