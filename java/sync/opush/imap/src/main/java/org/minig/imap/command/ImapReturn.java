package org.minig.imap.command;

public class ImapReturn<T> {

	private final ImapError error;
	private final T value;

	public ImapReturn(ImapError error) {
		this.error = error;
		this.value = null;
	}
	
	public ImapReturn(T value) {
		this.value = value;
		this.error = null;
	}
	
	public boolean isError() {
		return error != null;
	}
	
	public ImapError getError() {
		return error;
	}
	
	public T getValue() {
		return value;
	}
}
