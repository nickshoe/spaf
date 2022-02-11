package it.unibo.disi.spaf.api;

import java.util.List;

import it.unibo.disi.spaf.internals.TopologyBuilder;

// TODO: pay tribute to KafkaStreams Topology
public class Topology {

	protected final TopologyBuilder topologyBuilder;
	
	public Topology() {
		this.topologyBuilder = new TopologyBuilder();
	}
	
	public <K, V> Topology setSource(String name, Source<K, V> source) {
		this.topologyBuilder.setSource(name, source);
		
		return this;
	}
	
	public <KIn, VIn, KOut, VOut> Topology addProcessor(String name, Processor<KIn, VIn, KOut, VOut> processor, String parentName) {
		this.topologyBuilder.addProcessor(name, processor, parentName);
		
		return this;
	}

	public Topology setSink(String name, Sink<?, ?> sink) {
		this.topologyBuilder.setSink(name, sink);
		
		return this;
	}

	public Source<?, ?> getSource() {
		return this.topologyBuilder.getSource();
	}
	
	public List<Processor<?,?, ?, ?>> getProcessors() {
		return this.topologyBuilder.getProcessors();
	}
	
	public Sink<?, ?> getSink() {
		return this.topologyBuilder.getSink();
	}
	
	// TODO: add a method that returns the topology human-readable description (see KafkaStreams)
	
}
