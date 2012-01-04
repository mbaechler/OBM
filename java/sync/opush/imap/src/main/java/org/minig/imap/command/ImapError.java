package org.minig.imap.command;

public class ImapError {

	private final Exception cause;

	public ImapError(Exception cause) {
		this.cause = cause;
	}
	
	public Exception getCause() {
		return cause;
	}

}
