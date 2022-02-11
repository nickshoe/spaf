package it.unibo.disi.spaf.api.connectors;

import it.unibo.disi.spaf.api.Source;

public class MockSource<K, V> implements Source<K, V> {

	private static final long serialVersionUID = 1L;

	@Override
	public String getType() {
		return "mock";
	}
	
}
