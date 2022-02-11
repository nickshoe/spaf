package it.unibo.disi.spaf.examples.face.processors.dto;

import java.io.Serializable;

import org.openimaj.image.FImage;
import org.openimaj.math.geometry.shape.Rectangle;

public class FaceDetectionResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private FImage facePatch;
	private Rectangle bounds;
	private float confidence;

	public FaceDetectionResult() {
		super();
	}

	public FImage getFacePatch() {
		return facePatch;
	}

	public void setFacePatch(FImage facePatch) {
		this.facePatch = facePatch;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public float getConfidence() {
		return confidence;
	}

	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}

}
