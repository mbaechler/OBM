package org.obm.push.exception;

import org.minig.imap.Envelope;

public class UnfetchableMailException extends Exception {

	private Envelope fetchingEnvelope;

	public UnfetchableMailException(Envelope fetchingEnvelope, String msg) {
		super(msg);
		this.fetchingEnvelope = fetchingEnvelope;
	}
	
	public UnfetchableMailException(Envelope fetchingEnvelope, String msg, Exception e) {
		super(msg, e);
		this.fetchingEnvelope = fetchingEnvelope;
	}

	public UnfetchableMailException(Throwable cause) {
		super(cause);
	}

	public Envelope getFetchingEnvelope() {
		return fetchingEnvelope;
	}

}
