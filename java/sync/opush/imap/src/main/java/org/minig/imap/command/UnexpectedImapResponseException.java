package org.minig.imap.command;

public class UnexpectedImapResponseException extends ImapException {

	public UnexpectedImapResponseException(String message) {
		super(message);
	}

	public UnexpectedImapResponseException(RuntimeException e, String payload) {
		super("payload : " + payload, e);
	}

}
