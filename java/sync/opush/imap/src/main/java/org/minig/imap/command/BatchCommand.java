package org.minig.imap.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.minig.imap.impl.IMAPResponse;

import com.google.common.collect.Iterables;
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
	
	protected IMAPResponse checkStatusResponse(List<IMAPResponse> rs)
			throws UnexpectedImapErrorException {
		IMAPResponse ok = Iterables.getLast(rs, null);
		if (ok == null) {
			throw new UnexpectedImapErrorException(getClass().getName() + " failed : no response");
		}
		if (!ok.isOk()) {
			throw new UnexpectedImapErrorException(getClass().getName() + " failed : " + ok.getPayload());
		}
		return ok;
	}

}
