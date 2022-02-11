package it.unibo.disi.spaf.spark.streaming;

import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Context;
import it.unibo.disi.spaf.api.ContextFactory;

public class SparkStreamingContextFactory implements ContextFactory {

	private static SparkStreamingContextFactory instance;
	
	private SparkStreamingContextFactory() {}
	
	public static final SparkStreamingContextFactory getInstance() {
		if (instance == null) {
			instance = new SparkStreamingContextFactory();
		}
		
		return instance;
	}
	
	@Override
	public Context createContext(Config config) {
		Config contextConfig = config.getSubConfig("context");
		
		return new SparkStreamingContext(contextConfig);
	}
	

}
