package it.unibo.disi.spaf.spark.streaming.connectors.spi;

import it.unibo.disi.spaf.spark.streaming.connectors.SparkStreamingConnectors;
import it.unibo.disi.spaf.spark.streaming.connectors.SparkStreamingConnectorsFactory;

/**
 * Interface implemented by the Spark Streaming connectors provider.
 * 
 * <p> It is invoked by the {@link SparkStreamingConnectors} class 
 * to create a {@link SparkStreamingConnectorsFactory}.
 */
public interface SparkStreamingConnectorsProvider {
	
	/**
	 * Called by <code>SparkStreamingConnectors</code> to determine
	 * if the current provider is intended to handle the type of
	 * connectors specified in the config.
	 * 
	 * @return String the value of the connectors type handled by this provider
	 */
	String getConnectorsType();
	
	/**
	 * Called by <code>SparkStreamingConnectors</code> class when a
	 * <code>SparkStreamingConnectorsFactory</code> is to be created.
	 * 
	 * @return SparkStreamingConnectorsFactory or null if the provider is not the right provider
	 */
	SparkStreamingConnectorsFactory createConnectorsFactory();
}
