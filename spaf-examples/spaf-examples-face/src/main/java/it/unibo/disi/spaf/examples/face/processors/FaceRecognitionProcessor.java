package it.unibo.disi.spaf.examples.face.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.experiment.dataset.util.DatasetAdaptors;
import org.openimaj.feature.DoubleFV;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.model.EigenImages;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.processing.resize.filters.BoxFilter;

import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.examples.face.processors.dto.FaceDetectionOutputEnvelope;
import it.unibo.disi.spaf.examples.face.processors.dto.FaceDetectionResult;
import it.unibo.disi.spaf.examples.face.processors.dto.FaceRecognitionOutputEnvelope;
import it.unibo.disi.spaf.examples.face.processors.dto.PersonMatch;

public class FaceRecognitionProcessor implements Processor<String, FaceDetectionOutputEnvelope, String, FaceRecognitionOutputEnvelope> {

	private static final long serialVersionUID = 1L;

	private final Config config;

	public FaceRecognitionProcessor(Config config) {
		super();
		this.config = config;
	}

	private Map<String, DoubleFV[]> personsFeatures;
	private EigenImages eigenImages;

	@Override
	public void init() {
		VFSGroupDataset<FImage> personImagesDataset;
		try {
			String pathToDataset = config.getString("application.dataset-path");

			personImagesDataset = new VFSGroupDataset<FImage>(pathToDataset, ImageUtilities.FIMAGE_READER);
		} catch (FileSystemException e) {
			e.printStackTrace();

			return;
		}

		List<FImage> trainingImages = DatasetAdaptors.asList(personImagesDataset);
		List<FImage> trainingDatasetResizedImages = new ArrayList<FImage>();
		for (FImage image : trainingImages) {
			FImage resizedImage = ResizeProcessor.zoomInplace(image, 60, 60, new BoxFilter());

			trainingDatasetResizedImages.add(resizedImage);
		}

		int eingenComponentsNum = 100;
		EigenImages eigenImages = new EigenImages(eingenComponentsNum);
		eigenImages.train(trainingDatasetResizedImages);

		Map<String, DoubleFV[]> personsFeatures = new HashMap<String, DoubleFV[]>();

		final int PERSON_FACE_IMAGES_NUM = 5;
		for (String person : personImagesDataset.getGroups()) {
			final DoubleFV[] personFeatures = new DoubleFV[PERSON_FACE_IMAGES_NUM];
			for (int i = 0; i < PERSON_FACE_IMAGES_NUM; i++) {
				VFSListDataset<FImage> personImages = personImagesDataset.get(person);

				FImage personImage = personImages.get(i);

				FImage resizedPersonImage = ResizeProcessor.zoomInplace(personImage, 60, 60, new BoxFilter());

				personFeatures[i] = eigenImages.extractFeature(resizedPersonImage);
			}
			personsFeatures.put(person, personFeatures);
		}

		this.personsFeatures = personsFeatures;
		this.eigenImages = eigenImages;
	}

	@Override
	public void process(String imageFileName, FaceDetectionOutputEnvelope envelope, Collector<String, FaceRecognitionOutputEnvelope> collector) {
		byte[] inputImageBytes = envelope.getInputImageBytes();
		List<FaceDetectionResult> faceDetectionResults = envelope.getFaceDetectionResults();

		List<PersonMatch> personMatches = new ArrayList<>();
		for (FaceDetectionResult faceDetectionResult : faceDetectionResults) {
			FImage facePatch = faceDetectionResult.getFacePatch();
			FImage detectedFaceResizedImage = ResizeProcessor.zoomInplace(facePatch, 60, 60, new BoxFilter());
			DoubleFV detectedFaceFeature = eigenImages.extractFeature(detectedFaceResizedImage);

			PersonMatch bestMatch = FaceRecognitionProcessor.searchBestPersonMatch(personsFeatures, detectedFaceFeature);

			bestMatch.setFaceDetectionResult(faceDetectionResult);

			personMatches.add(bestMatch);
		}

		collector.collect(imageFileName, new FaceRecognitionOutputEnvelope(inputImageBytes, personMatches));
	}

	private static final PersonMatch searchBestPersonMatch(
		Map<String, DoubleFV[]> personsFeaturesRepository,
		DoubleFV testFeature
	) {
		String bestPerson = "UNDEFINED";
		double minDistance = Double.MAX_VALUE;

		for (String person : personsFeaturesRepository.keySet()) {
			DoubleFV[] personFeatures = personsFeaturesRepository.get(person);
			for (DoubleFV personFeature : personFeatures) {
				double distance = personFeature.compare(testFeature, DoubleFVComparison.EUCLIDEAN);
				if (distance < minDistance) {
					minDistance = distance;
					bestPerson = person;
				}
			}
		}

		PersonMatch bestMatch = new PersonMatch();

		bestMatch.setPersonId(bestPerson);
		bestMatch.setDistance(minDistance);

		return bestMatch;
	}

}
