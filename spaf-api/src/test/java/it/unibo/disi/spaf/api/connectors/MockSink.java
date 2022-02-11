package it.unibo.disi.spaf.api.connectors;

import it.unibo.disi.spaf.api.Sink;

public class MockSink<K, V> implements Sink<K, V> {

	private static final long serialVersionUID = 1L;

	@Override
	public String getType() {
		return "mock";
	}

}
