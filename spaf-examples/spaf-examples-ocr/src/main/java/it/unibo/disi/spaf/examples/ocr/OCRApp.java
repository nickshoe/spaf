package it.unibo.disi.spaf.examples.ocr;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.ConfigFactory;
import it.unibo.disi.spaf.api.Context;
import it.unibo.disi.spaf.api.ContextFactory;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.StreamProcessing;
import it.unibo.disi.spaf.api.Topology;
import it.unibo.disi.spaf.examples.ocr.processors.OCRProcessor;

public class OCRApp {

	public static void main(String[] args) {
		// 1. configurazione e ottenimento dell'ambiente di esecuzione
		Config config = ConfigFactory.load();
		ContextFactory contextFactory = StreamProcessing.createContextFactory();
		Context context = contextFactory.createContext(config);

		// 2. definizione dell'input e dell'output
		Source<String, byte[]> source = StreamProcessing.createSource(config);
		Sink<String, String> sink = StreamProcessing.createSink(config);

		// 3. definizione delle trasformazioni (ovvero, della topologia logica)
		Topology topology = new Topology().setSource("Source", source)
				.addProcessor("OCR", new OCRProcessor(config.getString("application.dataset-path")), "Source").setSink("Sink", sink);

		// 4. creazione dell'applicazione
		Application application = new Application().withName(config.getString("application.name"))
				.withTopology(topology);

		// 5. lancio dell'applicazione
		context.run(application);

		System.exit(0);
	}

}
