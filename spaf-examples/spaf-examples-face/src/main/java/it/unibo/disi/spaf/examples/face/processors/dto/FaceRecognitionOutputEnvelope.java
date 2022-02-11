package it.unibo.disi.spaf.examples.face.processors.dto;

import java.io.Serializable;
import java.util.List;

public class FaceRecognitionOutputEnvelope implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final byte[] inputImageBytes;
	private final List<PersonMatch> personMatches;

	public FaceRecognitionOutputEnvelope(byte[] inputImageBytes, List<PersonMatch> personMatches) {
		super();
		this.inputImageBytes = inputImageBytes;
		this.personMatches = personMatches;
	}

	public byte[] getInputImageBytes() {
		return inputImageBytes;
	}
	
	public List<PersonMatch> getPersonMatches() {
		return personMatches;
	}
	
}