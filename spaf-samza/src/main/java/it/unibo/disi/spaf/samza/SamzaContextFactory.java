package it.unibo.disi.spaf.samza;

import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Context;
import it.unibo.disi.spaf.api.ContextFactory;

public class SamzaContextFactory implements ContextFactory {

	private static SamzaContextFactory instance;
	
	private SamzaContextFactory() {}
	
	public static final SamzaContextFactory getInstance() {
		if (instance == null) {
			instance = new SamzaContextFactory();
		}
		
		return instance;
	}
	
	@Override
	public Context createContext(Config config) {
		Config contextConfig = config.getSubConfig("context");
		
		return new SamzaContext(contextConfig);
	}

}
