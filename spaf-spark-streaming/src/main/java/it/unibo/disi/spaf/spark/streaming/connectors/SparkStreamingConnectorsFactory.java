package it.unibo.disi.spaf.spark.streaming.connectors;

import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.internals.Element;

public abstract class SparkStreamingConnectorsFactory {

	public abstract <K, V> JavaDStream<Element<K, V>> createInputStream(JavaStreamingContext streamingContext, Application application, Source<?, ?> source);
	
	public abstract <K, V> void setupStreamOutput(JavaStreamingContext streamingContext, JavaDStream<Element<K, V>> stream, Sink<?, ?> sink);
	
}
