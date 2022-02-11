package it.unibo.disi.spaf.examples.simple;

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

public class SimpleApp {

	public static void main(String[] args) {				
		// 1. configurazione e ottenimento dell'ambiente di esecuzione
		Config config = ConfigFactory.load();
		ContextFactory contextFactory = StreamProcessing.createContextFactory();
		Context context = contextFactory.createContext(config);

		// 2. definizione dell'input e dell'output
		Source<String, String> source = StreamProcessing.createSource(config);
		Sink<String, String> sink = StreamProcessing.createSink(config);
		
		// 3. definizione delle trasformazioni (ovvero, della topologia logica)
		Topology topology = new Topology()
			.setSource("Source", source)
			.addProcessor("Filter", (String key, String value, Collector<String, String> collector) -> {			
				String valueToIgnore = "foo bar";
				
				if (value.equals(valueToIgnore)) {
					return;
				}
				
				String valueToMultiply = "hot coffee";
				if (value.equals(valueToMultiply)) {
					for (int i = 0; i < 10; i++) {
						collector.collect(key, value);
					}
					
					return;
				}

				collector.collect(key, value);
			}, "Source")
			.addProcessor("ToUppercase", (String key, String value, Collector<String, String> collector) -> {				
				String uppercasedString = value.toUpperCase();
 
				collector.collect(key, uppercasedString);
			}, "Filter")
			.addProcessor("ToSneakCase", (String key, String value, Collector<String, String> collector) -> {
				String sneakedString = String.join("-", value.split(" ")); 
				
				collector.collect(key, sneakedString);
			}, "ToUppercase")
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
