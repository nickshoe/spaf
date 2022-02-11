package it.unibo.disi.spaf.flink.streaming.exceptions;

import it.unibo.disi.spaf.common.exceptions.StreamProcessingException;

public class FlinkStreamingStreamProcessingException extends StreamProcessingException {
	
	private final static long serialVersionUID = 1L;

	public FlinkStreamingStreamProcessingException(String message) {
		super(message);
	}

	public FlinkStreamingStreamProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public FlinkStreamingStreamProcessingException(Throwable cause) {
		super(cause);
	}

}
