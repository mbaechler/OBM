package org.obm.push.exception.activesync;


public class XMLValidationException extends ActiveSyncException {

	public XMLValidationException() {
		super();
	}

	public XMLValidationException(String message) {
		super(message);
	}
	
	public XMLValidationException(Exception e) {
		super(e);
	}

}
