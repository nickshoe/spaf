package it.unibo.disi.spaf.samza.exceptions;

import it.unibo.disi.spaf.common.exceptions.StreamProcessingException;

public class SamzaStreamProcessingException extends StreamProcessingException {
	
	private final static long serialVersionUID = 1L;

	public SamzaStreamProcessingException(String message) {
		super(message);
	}

	public SamzaStreamProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public SamzaStreamProcessingException(Throwable cause) {
		super(cause);
	}

}
