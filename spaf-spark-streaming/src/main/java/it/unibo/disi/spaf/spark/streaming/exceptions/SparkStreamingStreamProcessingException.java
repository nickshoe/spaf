package it.unibo.disi.spaf.spark.streaming.exceptions;

import it.unibo.disi.spaf.common.exceptions.StreamProcessingException;

public class SparkStreamingStreamProcessingException extends StreamProcessingException {
	
	private final static long serialVersionUID = 1L;

	public SparkStreamingStreamProcessingException(String message) {
		super(message);
	}

	public SparkStreamingStreamProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public SparkStreamingStreamProcessingException(Throwable cause) {
		super(cause);
	}

}
