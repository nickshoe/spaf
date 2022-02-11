package it.unibo.disi.spaf.internals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unibo.disi.spaf.api.Collector;

public class CollectorImpl<K, V> implements Collector<K, V>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Element<K, V>> collectedElements = new ArrayList<>();

	@Override
	public Iterator<Element<K, V>> iterator() {
		return this.collectedElements.iterator();
	}

	@Override
	public void collect(K key, V value) {
		if (key == null && value == null) {
			return;
		}
		
		Element<K, V> element = new Element<K, V>(key, value);
		
		this.collectedElements.add(element);		
	}

}
