package it.unibo.disi.spaf.internals;

import it.unibo.disi.spaf.api.Collector;
import it.unibo.disi.spaf.api.Processor;

/**
 * A Processor wrapper class that allows for one-time processor initialization
 * This class should be used by the provider implementation
 */
public class OneTimeInitProcessor<KIn, VIn, KOut, VOut> implements Processor<KIn, VIn, KOut, VOut> {

	private static final long serialVersionUID = 1L;
	
	private boolean alreadyInitialized = false;
	private final Processor<KIn, VIn, KOut, VOut> processor;
	
	public OneTimeInitProcessor(Processor<KIn, VIn, KOut, VOut> processor) {
		super();
		this.processor = processor;
	}
	
	@Override
	public void init() {
		if (!this.alreadyInitialized) {
			this.processor.init();
			
			this.alreadyInitialized = true;
		}
	}

	@Override
	public void process(KIn key, VIn value, Collector<KOut, VOut> collector) {
		this.processor.process(key, value, collector);
	}
	
}
