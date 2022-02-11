package it.unibo.disi.spaf.connectors;

import it.unibo.disi.spaf.api.Config;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;

public abstract class ConnectorsFactory {

	public abstract <K, V> Source<K, V> createSource(Config config);
	
	public abstract <K, V> Sink<K, V> createSink(Config config);
	
}
