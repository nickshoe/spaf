package it.unibo.disi.spaf.api;

import java.io.Serializable;

//TODO: is it ok that it is serializable?
public interface Source<K, V> extends Serializable {

	String getType();
	
}
