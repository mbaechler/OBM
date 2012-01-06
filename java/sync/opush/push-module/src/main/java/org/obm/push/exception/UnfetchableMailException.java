package org.obm.push.exception;

import org.minig.imap.Envelope;

public class UnfetchableMailException extends Exception {

	private Envelope fetchingEnvelope;
	private long uid;

	/*public UnfetchableMailException(Envelope fetchingEnvelope, String msg) {
		super(msg);
		this.fetchingEnvelope = fetchingEnvelope;
		this.uid = fetchingEnvelope.getUid();
	}
	
	public UnfetchableMailException(Envelope fetchingEnvelope, String msg, Exception e) {
		super(msg, e);
		this.fetchingEnvelope = fetchingEnvelope;
		this.uid = fetchingEnvelope.getUid();
	}*/

	public UnfetchableMailException(long uid, Throwable cause) {
		super(cause);
		this.uid = uid;
	}

	public Envelope getFetchingEnvelope() {
		return fetchingEnvelope;
	}
	
	public long getUid() {
		return uid;
	}

}
