package it.unibo.disi.spaf.examples.face.processors.dto;

import java.io.Serializable;
import java.util.List;

public class FaceDetectionOutputEnvelope implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final byte[] inputImageBytes;
	private final List<FaceDetectionResult> faceDetectionResults;

	public FaceDetectionOutputEnvelope(byte[] inputImageBytes, List<FaceDetectionResult> detectedFaces) {
		super();
		this.inputImageBytes = inputImageBytes;
		this.faceDetectionResults = detectedFaces;
	}

	public byte[] getInputImageBytes() {
		return inputImageBytes;
	}

	public List<FaceDetectionResult> getFaceDetectionResults() {
		return faceDetectionResults;
	}
	
}