package org.obm.funambol.exception;

public class ConvertionException extends Exception {

	public ConvertionException(String message) {
		super(message);
	}
	
	public ConvertionException(String message, Throwable e) {
		super(message, e);
	}

}
