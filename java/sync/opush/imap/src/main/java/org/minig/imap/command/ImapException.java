package org.minig.imap.command;

public class ImapException extends Exception {

	public ImapException(String message) {
		super(message);
	}

	public ImapException(RuntimeException e) {
		super(e);
	}

}
