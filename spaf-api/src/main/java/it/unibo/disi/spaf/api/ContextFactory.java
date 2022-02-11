package it.unibo.disi.spaf.api;

/**
 * Interface used to interact with the context factory.
 */
public interface ContextFactory {
	/**
     * Create a new <code>Context</code> with the specified properties. 
     * @param config configuration for context
     * @return context instance
     */
	Context createContext(Config config);
}
