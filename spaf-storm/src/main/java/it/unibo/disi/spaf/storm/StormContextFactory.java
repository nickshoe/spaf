package it.unibo.disi.spaf.storm;

import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Context;
import it.unibo.disi.spaf.api.ContextFactory;

public class StormContextFactory implements ContextFactory {

	private static StormContextFactory instance;
	
	private StormContextFactory() {}
	
	public static final StormContextFactory getInstance() {
		if (instance == null) {
			instance = new StormContextFactory();
		}
		
		return instance;
	}
	
	@Override
	public Context createContext(Config config) {
		Config contextConfig = config.getSubConfig("context");
		
		return new StormContext(contextConfig);
	}

}
