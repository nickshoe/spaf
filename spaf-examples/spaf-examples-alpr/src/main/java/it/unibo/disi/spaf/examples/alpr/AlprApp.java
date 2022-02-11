package it.unibo.disi.spaf.examples.alpr;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONObject;

import com.openalpr.jni.AlprPlateResult;
import com.openalpr.jni.AlprResults;

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
import it.unibo.disi.spaf.examples.alpr.processors.AlprProcessor;

public class AlprApp {

	public static void main(String[] args) {
		// 1. configurazione e ottenimento dell'ambiente di esecuzione
		Config config = ConfigFactory.load();
		ContextFactory contextFactory = StreamProcessing.createContextFactory();
		Context context = contextFactory.createContext(config);

		// 2. definizione dell'input e dell'output
		Source<String, byte[]> source = StreamProcessing.createSource(config);
		Sink<String, String> sink = StreamProcessing.createSink(config);

		// 3. definizione delle trasformazioni (ovvero, della topologia logica)
		Topology topology = new Topology()
				.setSource("Source", source)
				.addProcessor("Alpr", new AlprProcessor(config), "Source")
				.addProcessor("ToDTO", (String imageName, AlprResults alprResults, Collector<String, String> collector) -> {
					List<AlprPlateResult> recognizedPlates = alprResults.getPlates();
					float totalProcessingTimeMs = alprResults.getTotalProcessingTimeMs();
					String nowIsoDatetime = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);

					String plate;
					if (recognizedPlates.size() == 0) {
						plate = "NO PLATE FOUND!";
					} else {
						plate = recognizedPlates.get(0).getTopNPlates().get(0).getCharacters();
					}
					
					JSONObject result = new JSONObject()
						.put("image-name", imageName)
						.put("plate", plate)
						.put("timestamp", nowIsoDatetime)
						.put("processing-time-ms", totalProcessingTimeMs);
					
					String resultJson = result.toString(2);
					
					collector.collect(imageName, resultJson);
				}, "Alpr")
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
