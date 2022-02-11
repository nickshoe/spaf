package it.unibo.disi.spaf.api;

import java.io.Serializable;

@FunctionalInterface
public interface Processor<KIn, VIn, KOut, VOut> extends Serializable {
	
	default void init() {}
	
	void process(KIn key, VIn value, Collector<KOut, VOut> collector);
	
}
