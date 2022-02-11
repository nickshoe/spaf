package it.unibo.disi.spaf.examples.face.processors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;

import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.examples.face.processors.dto.FaceDetectionOutputEnvelope;
import it.unibo.disi.spaf.examples.face.processors.dto.FaceDetectionResult;

public class FaceDetectionProcessor implements Processor<String, byte[], String, FaceDetectionOutputEnvelope> {

	private static final long serialVersionUID = 1L;

	private static final int MIN_SEARCH_WINDOW_SIZE = 20;

	private FaceDetector<DetectedFace, FImage> faceDetector;

	@Override
	public void init() {
		this.faceDetector = new HaarCascadeDetector(MIN_SEARCH_WINDOW_SIZE);
	}

	@Override
	public void process(String imageFileName, byte[] inputImageBytes, Collector<String, FaceDetectionOutputEnvelope> collector) {
		FImage inputImage;
		try {
			inputImage = ImageUtilities.readF(new ByteArrayInputStream(inputImageBytes));
		} catch (IOException e) {
			e.printStackTrace();

			return;
		}

		List<DetectedFace> detectedFaces = this.faceDetector.detectFaces(inputImage);
		
		List<FaceDetectionResult> faceDetectionResults = detectedFaces.stream().map(detectedFace -> {
			FaceDetectionResult faceDetectionResult = new FaceDetectionResult();
			
			faceDetectionResult.setBounds(detectedFace.getBounds());
			faceDetectionResult.setFacePatch(detectedFace.getFacePatch());
			faceDetectionResult.setConfidence(detectedFace.getConfidence());
			
			return faceDetectionResult;
		}).collect(Collectors.toList());

		collector.collect(imageFileName, new FaceDetectionOutputEnvelope(inputImageBytes, faceDetectionResults));
	}

}
