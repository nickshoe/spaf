package it.unibo.disi.spaf.api;

import it.unibo.disi.spaf.internals.Element;

public interface Collector<K, V> extends Iterable<Element<K, V>> {
	
	void collect(K key, V value);
	
}
