package it.unibo.disi.spaf.internals;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.api.Processor;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.common.exceptions.TopologyException;

public class TopologyBuilder {
	private static final Logger logger = LoggerFactory.getLogger(TopologyBuilder.class);
	
	private String sourceName;
	private Source<?, ?> source;
	
	private final Map<String, Processor<?, ?, ?, ?>> processors = new LinkedHashMap<>();
	
	private String sinkName;
	private Sink<?, ?> sink;
	
	public TopologyBuilder() {}

	public final <K, V> void setSource(String name, Source<K, V> source) {
		Objects.requireNonNull(name, "source name must not be null");
		
		if (this.source != null) {
			throw new TopologyException("Source is already set.");
		}
		
		logger.debug("Setting a source named {} of type {}", name, source.getType());
		
		this.sourceName = name;
		this.source = source;
	}
	
	public final <KIn, VIn, KOut, VOut> void addProcessor(String name, Processor<KIn, VIn, KOut, VOut> processor, String predecessorName) {
		Objects.requireNonNull(name, "processor name must not be null");
		Objects.requireNonNull(predecessorName, "predecessor name must not be null");
		
		if (this.processors.containsKey(name)) {
			throw new TopologyException("Processor " + name + " is already added");
		}
		
		if (predecessorName.equals(name)) {
			throw new TopologyException("Processor " + name + " cannot be a predecessor of itself");
		}
		if (!this.sourceName.equals(predecessorName) && !this.processors.containsKey(predecessorName)) { // TODO: refactor - treat all nodes as processors
			throw new TopologyException("Predecessor processor " + predecessorName + " is not added yet for " + name);
		}
		if (this.sourceName.equals(predecessorName) && !this.processors.isEmpty()) { // TODO: remove this check when implementing multi-source topology
			throw new TopologyException("Source named " + this.sourceName + " cannot be predecessor of processor " + name + " since it already has a successor");
		}
		
		logger.debug("Adding a processor named {} after {}", name, predecessorName);
		
		this.processors.put(name, processor);
	}
	
	public final <K, V> void setSink(String name, Sink<K, V> sink) {
		Objects.requireNonNull(name, "sink name must not be null");
		
		if (this.sink != null) { // TODO: remove this check when implementing multi-sink topology
			throw new TopologyException("Another sink with name " + this.sinkName + " is already set.");
		}
		
		boolean hasPredecessor = this.source != null || this.processors.size() > 0; // TODO: refactor - treat all nodes as processors
		if (!hasPredecessor) {
			throw new TopologyException("Sink must have a predecessor.");
		}
		
		logger.debug("Setting a sink named {} of type {}", name, sink.getType());
		
		this.sinkName = name;
		this.sink = sink;
	}
	
	public final Source<?,?> getSource() {
		return this.source;
	}
	
	public final List<Processor<?, ?, ?, ?>> getProcessors() {
		return new ArrayList<>(this.processors.values());
	}
	
	public final Sink<?, ?> getSink() {
		return this.sink;
	}
}
