package com.gamsion.chris.blockhider;

public class ArrayTooShortException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	public ArrayTooShortException() {
		super();
	}
	public ArrayTooShortException(String message) {
		super(message);
	}
	public ArrayTooShortException(String message, Throwable cause) {
		super(message, cause);
	}
	public ArrayTooShortException(Throwable cause) {
		super(cause);
	}
}

