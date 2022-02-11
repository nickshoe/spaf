package it.unibo.disi.spaf.samza.connectors.spi;

import it.unibo.disi.spaf.samza.connectors.SamzaConnectors;
import it.unibo.disi.spaf.samza.connectors.SamzaConnectorsFactory;

/**
 * Interface implemented by the Samza connectors provider.
 * 
 * <p> It is invoked by the {@link SamzaConnectors} class 
 * to create a {@link SamzaConnectorsFactory}.
 */
public interface SamzaConnectorsProvider {
	
	/**
	 * Called by <code>SamzaContext</code> to determine
	 * if the current provider is intended to handle the type of
	 * connectors specified in the config.
	 * 
	 * @return String the value of the connectors type handled by this provider
	 */
	String getConnectorsType();
	
	/**
	 * Called by <code>SamzaContext</code> class when a
	 * <code>SamzaConnectorsFactory</code> is to be created.
	 * 
	 * @return SamzaConnectorsFactory or null if the provider is not the right provider
	 */
	SamzaConnectorsFactory createConnectorsFactory();
}
