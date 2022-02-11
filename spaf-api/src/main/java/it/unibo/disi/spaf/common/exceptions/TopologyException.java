package it.unibo.disi.spaf.common.exceptions;

/**
 * Indicates a pre-execution error occurred while defining the {@link it.unibo.disi.spaf.api.Topology logical topology}.
 */
public class TopologyException extends StreamProcessingException {

	private static final long serialVersionUID = 1L;

	public TopologyException(final String message) {
        super("Invalid topology" + (message == null ? "" : ": " + message));
    }

    public TopologyException(final String message,
                             final Throwable throwable) {
        super("Invalid topology" + (message == null ? "" : ": " + message), throwable);
    }

    public TopologyException(final Throwable throwable) {
        super(throwable);
    }

}
