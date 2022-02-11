package it.unibo.disi.spaf.storm.connectors;

import org.apache.storm.topology.TopologyBuilder;

import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;

public abstract class StormConnectorsFactory {

	public abstract void setupSourceSpout(TopologyBuilder topologyBuilder, Source<?, ?> source, String sourceName);

	public abstract void setupSinkBolt(TopologyBuilder topologyBuilder, Sink<?, ?> sink, String sinkName, String previousComponentId);

}
