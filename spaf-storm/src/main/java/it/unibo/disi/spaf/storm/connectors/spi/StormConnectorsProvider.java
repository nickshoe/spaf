package it.unibo.disi.spaf.storm.connectors.spi;

import it.unibo.disi.spaf.storm.connectors.StormConnectors;
import it.unibo.disi.spaf.storm.connectors.StormConnectorsFactory;

/**
 * Interface implemented by the Storm connectors provider.
 * 
 * <p> It is invoked by the {@link StormConnectors} class 
 * to create a {@link StormConnectorsFactory}.
 */
public interface StormConnectorsProvider {
	
	/**
	 * Called by <code>StormConnectors</code> to determine
	 * if the current provider is intended to handle the type of
	 * connectors specified in the config.
	 * 
	 * @return String the value of the connectors type handled by this provider
	 */
	String getConnectorsType();
	
	/**
	 * Called by <code>StormConnectors</code> class when a
	 * <code>StormConnectorsFactory</code> is to be created.
	 * 
	 * @return StormConnectorsFactory or null if the provider is not the right provider
	 */
	StormConnectorsFactory createConnectorsFactory();
}
