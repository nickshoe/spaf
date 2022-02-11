package it.unibo.disi.spaf.spark.streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.ContextFactory;
import it.unibo.disi.spaf.spi.StreamProcessingProvider;

public class SparkStreamingStreamProcessingProvider implements StreamProcessingProvider {
	private static final Logger logger = LoggerFactory.getLogger(SparkStreamingStreamProcessingProvider.class);
	
	@Override
	public ContextFactory createContextFactory() {
		logger.debug("Obtaining the Spark Streaming context factory");
		
		return SparkStreamingContextFactory.getInstance();
	}
}
