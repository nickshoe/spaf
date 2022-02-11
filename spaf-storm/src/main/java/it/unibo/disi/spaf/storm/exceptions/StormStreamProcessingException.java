package it.unibo.disi.spaf.storm.exceptions;

import it.unibo.disi.spaf.common.exceptions.StreamProcessingException;

public class StormStreamProcessingException extends StreamProcessingException {
	
	private final static long serialVersionUID = 1L;

	public StormStreamProcessingException(String message) {
		super(message);
	}

	public StormStreamProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public StormStreamProcessingException(Throwable cause) {
		super(cause);
	}

}
