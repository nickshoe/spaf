package it.unibo.disi.spaf.flink.streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.ContextFactory;
import it.unibo.disi.spaf.spi.StreamProcessingProvider;

public class FlinkStreamingStreamProcessingProvider implements StreamProcessingProvider {

	private static final Logger logger = LoggerFactory.getLogger(FlinkStreamingStreamProcessingProvider.class);

	@Override
	public ContextFactory createContextFactory() {
		logger.debug("Obtaining the Flink Streaming context factory");

		return FlinkStreamingContextFactory.getInstance();
	}

}
