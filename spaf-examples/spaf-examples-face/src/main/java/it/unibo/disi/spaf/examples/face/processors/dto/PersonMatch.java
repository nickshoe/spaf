package it.unibo.disi.spaf.examples.face.processors.dto;

import java.io.Serializable;

public class PersonMatch implements Serializable {

	private static final long serialVersionUID = 1L;

	private String personId;
	private FaceDetectionResult faceDetectionResult;
	private double distance;

	public PersonMatch() {
		super();
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public FaceDetectionResult getFaceDetectionResult() {
		return faceDetectionResult;
	}

	public void setFaceDetectionResult(FaceDetectionResult faceDetectionResult) {
		this.faceDetectionResult = faceDetectionResult;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

}