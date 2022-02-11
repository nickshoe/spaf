package it.unibo.disi.spaf.connectors.spi;

import it.unibo.disi.spaf.api.StreamProcessing;
import it.unibo.disi.spaf.connectors.ConnectorsFactory;

/**
 * Interface implemented by the connectors provider.
 * 
 * <p> It is invoked by the {@link StreamProcessing} class 
 * to create a {@link ConnectorsFactory}.
 */
public interface ConnectorsProvider {
	
	/**
	 * Called by <code>StreamProcessing</code> to determine
	 * if the current provider is intended to handle the type of
	 * connectors specified in the config.
	 * 
	 * @return String the value of the connectors type handled by this provider
	 */
	String getConnectorsType();
	
	/**
	 * Called by <code>StreamProcessing</code> class when a
	 * <code>ConnectorsFactory</code> is to be created.
	 * 
	 * @return ConnectorsFactory or null if the provider is not the right provider
	 */
	ConnectorsFactory createConnectorsFactory();
}
