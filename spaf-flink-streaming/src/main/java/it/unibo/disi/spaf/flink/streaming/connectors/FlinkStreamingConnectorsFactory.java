package it.unibo.disi.spaf.flink.streaming.connectors;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.internals.Element;

public abstract class FlinkStreamingConnectorsFactory {

	public abstract DataStream<Element<Object, Object>> createInputStream(StreamExecutionEnvironment environment, Application application, Source<?, ?> source);
	
	public abstract void setupStreamOutput(DataStream<Element<Object, Object>> stream, Sink<?, ?> sink);
	
}
