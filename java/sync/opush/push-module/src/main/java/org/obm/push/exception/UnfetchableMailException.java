package org.obm.push.exception;

import org.minig.imap.Envelope;

public class UnfetchableMailException extends Exception {

	private final Envelope fetchingEnvelope;

	public UnfetchableMailException(Envelope fetchingEnvelope, String msg) {
		super(msg);
		this.fetchingEnvelope = fetchingEnvelope;
	}
	
	public UnfetchableMailException(Envelope fetchingEnvelope, String msg, Exception e) {
		super(msg, e);
		this.fetchingEnvelope = fetchingEnvelope;
	}

	public Envelope getFetchingEnvelope() {
		return fetchingEnvelope;
	}

	@Override
	public String getLocalizedMessage() {
		return getMessage() + " : " + getCause().getMessage();
	}
}
