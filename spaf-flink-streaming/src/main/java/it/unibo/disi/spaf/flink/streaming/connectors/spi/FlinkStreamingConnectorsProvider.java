package it.unibo.disi.spaf.flink.streaming.connectors.spi;

import it.unibo.disi.spaf.flink.streaming.connectors.FlinkStreamingConnectors;
import it.unibo.disi.spaf.flink.streaming.connectors.FlinkStreamingConnectorsFactory;

/**
 * Interface implemented by the Flink Streaming connectors provider.
 * 
 * <p> It is invoked by the {@link FlinkStreamingConnectors} class 
 * to create a {@link FlinkStreamingConnectorsFactory}.
 */
public interface FlinkStreamingConnectorsProvider {
	
	/**
	 * Called by <code>FlinkStreamingContext</code> to determine
	 * if the current provider is intended to handle the type of
	 * connectors specified in the config.
	 * 
	 * @return String the value of the connectors type handled by this provider
	 */
	String getConnectorsType();
	
	/**
	 * Called by <code>FlinkStreamingContext</code> class when a
	 * <code>FlinkStreamingConnectorsFactory</code> is to be created.
	 * 
	 * @return FlinkStreamingConnectorsFactory or null if the provider is not the right provider
	 */
	FlinkStreamingConnectorsFactory createConnectorsFactory();
}
