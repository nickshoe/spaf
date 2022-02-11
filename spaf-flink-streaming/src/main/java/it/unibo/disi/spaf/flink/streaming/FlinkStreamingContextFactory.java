package it.unibo.disi.spaf.flink.streaming;

import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Context;
import it.unibo.disi.spaf.api.ContextFactory;

public class FlinkStreamingContextFactory implements ContextFactory {

	private static FlinkStreamingContextFactory instance;
	
	private FlinkStreamingContextFactory() {}
	
	public static final FlinkStreamingContextFactory getInstance() {
		if (instance == null) {
			instance = new FlinkStreamingContextFactory();
		}
		
		return instance;
	}
	
	@Override
	public Context createContext(Config config) {
		Config contextConfig = config.getSubConfig("context");
		
		return new FlinkStreamingContext(contextConfig);
	}

}
