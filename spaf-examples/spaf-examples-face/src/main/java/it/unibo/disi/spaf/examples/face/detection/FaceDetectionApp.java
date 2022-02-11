package it.unibo.disi.spaf.examples.face.detection;

import java.util.List;
import java.util.stream.Collectors;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.ConfigFactory;
import it.unibo.disi.spaf.api.Context;
import it.unibo.disi.spaf.api.ContextFactory;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.StreamProcessing;
import it.unibo.disi.spaf.api.Topology;
import it.unibo.disi.spaf.examples.face.processors.FaceDetectionProcessor;
import it.unibo.disi.spaf.examples.face.processors.PersonFaceMarkingProcessor;
import it.unibo.disi.spaf.examples.face.processors.dto.FaceDetectionOutputEnvelope;
import it.unibo.disi.spaf.examples.face.processors.dto.FaceRecognitionOutputEnvelope;
import it.unibo.disi.spaf.examples.face.processors.dto.PersonMatch;

public class FaceDetectionApp {

	public static void main(String[] args) {
		// 1. configurazione e ottenimento dell'ambiente di esecuzione
		Config config = ConfigFactory.load();
		ContextFactory contextFactory = StreamProcessing.createContextFactory();
		Context context = contextFactory.createContext(config);

		// 2. definizione dell'input e dell'output
		Source<String, byte[]> source = StreamProcessing.createSource(config);
		Sink<String, byte[]> sink = StreamProcessing.createSink(config);

		// 3. definizione delle trasformazioni (ovvero, della topologia logica)
		Topology topology = new Topology()
				.setSource("Source", source)
				.addProcessor("FaceDetector", new FaceDetectionProcessor(), "Source")
				.addProcessor("FaceMarkerAdapter", (String imageFileName, FaceDetectionOutputEnvelope envelope, Collector<String, FaceRecognitionOutputEnvelope> collector) -> {
					List<PersonMatch> fakeMatches = envelope.getFaceDetectionResults().stream()
						.map(detectedFace -> {
							PersonMatch personMatch = new PersonMatch();
							
							personMatch.setFaceDetectionResult(detectedFace);
							personMatch.setDistance(100);
							personMatch.setPersonId(Float.toString(detectedFace.getConfidence()));
							
							return personMatch;
						})
						.collect(Collectors.toList());
					
					collector.collect(imageFileName, new FaceRecognitionOutputEnvelope(envelope.getInputImageBytes(), fakeMatches));
				}, "FaceDetector")
				.addProcessor("FaceMarker", new PersonFaceMarkingProcessor(), "FaceDetector")
				.setSink("Sink", sink);

		// 4. creazione dell'applicazione
		Application application = new Application()
				.withName(config.getString("application.name"))
				.withTopology(topology);

		// 5. lancio dell'applicazione
		context.run(application);

		System.exit(0);
	}
	
}
