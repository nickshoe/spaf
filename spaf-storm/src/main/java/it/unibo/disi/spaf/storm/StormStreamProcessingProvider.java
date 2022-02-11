package it.unibo.disi.spaf.storm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.ContextFactory;
import it.unibo.disi.spaf.spi.StreamProcessingProvider;

public class StormStreamProcessingProvider implements StreamProcessingProvider {

	private static final Logger logger = LoggerFactory.getLogger(StormStreamProcessingProvider.class);
	
	@Override
	public ContextFactory createContextFactory() {
		logger.debug("Obtaining the Storm context factory");
		
		return StormContextFactory.getInstance();
	}

}
