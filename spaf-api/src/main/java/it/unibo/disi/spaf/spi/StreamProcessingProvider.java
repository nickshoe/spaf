package it.unibo.disi.spaf.spi;

import it.unibo.disi.spaf.api.ContextFactory;
import it.unibo.disi.spaf.api.StreamProcessing;

/**
 * Interface implemented by the stream processing provider.
 * 
 * <p> It is invoked by the {@link StreamProcessing} class 
 * to create a {@link ContextFactory}.
 */
public interface StreamProcessingProvider {
	/**
	 * Called by <code>StreamProcessing</code> class when a
	 * <code>ContextFactory</code> is to be created.
	 * 
	 * @return ContextFactory or null if the provider is not the right provider
	 */
	ContextFactory createContextFactory();
}
