package it.unibo.disi.spaf.samza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.ContextFactory;
import it.unibo.disi.spaf.spi.StreamProcessingProvider;

public class SamzaStreamProcessingProvider implements StreamProcessingProvider {

	private static final Logger logger = LoggerFactory.getLogger(SamzaStreamProcessingProvider.class);
	
	@Override
	public ContextFactory createContextFactory() {
		logger.debug("Obtaining the Samza context factory");
		
		return SamzaContextFactory.getInstance();
	}

}
