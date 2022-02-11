package it.unibo.disi.spaf.internals;

import java.io.Serializable;
import java.util.Objects;

/**
 * A key and value pair.
 *
 * @param <K> type of the key
 * @param <V> type of the value
 */
public final class Element<K, V> implements Serializable {

	private static final long serialVersionUID = 1L;

	private K key;
	private V value;
	
	public Element() {
		super();
	}

	public Element(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Element<?, ?> other = (Element<?, ?>) obj;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "KV [key=" + key + ", value=" + value + "]";
	}

}
