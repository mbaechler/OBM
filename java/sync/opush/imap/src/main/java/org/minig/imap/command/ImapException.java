package org.minig.imap.command;

public class ImapException extends Exception {

	public ImapException(String message) {
		super(message);
	}

	public ImapException(RuntimeException cause) {
		super(cause);
	}

	public ImapException(String message, RuntimeException cause) {
		super(message, cause);
	}

}
