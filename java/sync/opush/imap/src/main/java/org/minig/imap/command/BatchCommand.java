package org.minig.imap.command;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Lists;

public abstract class BatchCommand<T> extends Command<Collection<ImapReturn<T>>> {
	
	protected Collection<ImapReturn<T>> wrapValues(Collection<T> values) {
		ArrayList<ImapReturn<T>> wrappedValues = Lists.newArrayListWithCapacity(values.size());
		for (T value: values) {
			wrappedValues.add(new ImapReturn<T>(value));
		}
		return wrappedValues;
	}
	
	protected ImapReturn<T> error(Exception e) {
		return new ImapReturn<T>(new ImapError(e));
	}

	protected ImapReturn<T> value(T value) {
		return new ImapReturn<T>(value);
	}

}
