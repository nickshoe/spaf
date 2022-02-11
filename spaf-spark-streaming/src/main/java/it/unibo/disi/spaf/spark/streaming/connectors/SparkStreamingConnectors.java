package it.unibo.disi.spaf.spark.streaming.connectors;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.internals.Element;
import it.unibo.disi.spaf.spark.streaming.connectors.spi.SparkStreamingConnectorsProvider;
import it.unibo.disi.spaf.spark.streaming.exceptions.SparkStreamingStreamProcessingException;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class SparkStreamingConnectors {
	
	private final static Logger logger = LoggerFactory.getLogger(SparkStreamingConnectors.class);
	
	public static final <K, V> JavaDStream<Element<K, V>> createJavaDStream(
		JavaStreamingContext javaStreamingContext, 
		Application application, 
		Source<?, ?> source
	) {
		String connectorType = source.getType();
		
		return SparkStreamingConnectors.createConnectorsFactory(connectorType).createInputStream(javaStreamingContext, application, source);
	}
	
	public  static final <K, V> void setupStreamOutput(
		JavaStreamingContext javaStreamingContext,
		JavaDStream<Element<K, V>> stream,
		Sink<?, ?> sink
	) {
		String connectorType = sink.getType();
		
		SparkStreamingConnectors.createConnectorsFactory(connectorType).setupStreamOutput(javaStreamingContext, stream, sink);
	}
	
	// TODO: cache connection factories already built
	private static final SparkStreamingConnectorsFactory createConnectorsFactory(String connectorType) {
		SparkStreamingConnectorsFactory connectorsFactory = null;
		
		List<SparkStreamingConnectorsProvider> providers = SparkStreamingConnectors.getSparkStreamingConnectorsProviders();
		
		if (providers.isEmpty()) {
			throw new SparkStreamingStreamProcessingException("No Spark Streaming Connectors provider found");
		}
		
		List<SparkStreamingConnectorsProvider> connectorsTypeProviders = providers.stream()
			.filter(provider -> provider.getConnectorsType().equals(connectorType))
			.collect(Collectors.toList());
		
		if (connectorsTypeProviders.isEmpty()) {
			throw new SparkStreamingStreamProcessingException("No Spark Streaming Connectors provider found for type " + connectorType);	
		}
		
		if (connectorsTypeProviders.size() > 1) {
			throw new SparkStreamingStreamProcessingException("Multiple Spark Streaming Connectors providers found for type " + connectorType);
		}
		
		SparkStreamingConnectorsProvider connectorsTypeProvider = connectorsTypeProviders.get(0);
		
		connectorsFactory = connectorsTypeProvider.createConnectorsFactory();
		
		return connectorsFactory;
	}
	
	// TODO: cache found providers
	private static final List<SparkStreamingConnectorsProvider> getSparkStreamingConnectorsProviders() {
		List<SparkStreamingConnectorsProvider> providers = new ArrayList<>();
	        
        ServiceLoader<SparkStreamingConnectorsProvider> serviceLoader = ServiceLoader.load(SparkStreamingConnectorsProvider.class);

		for (SparkStreamingConnectorsProvider loadedProvider : serviceLoader) {
			providers.add(loadedProvider);
		}
        
        if (providers.isEmpty()) {
        	logger.warn("No Connectors providers found.");
        }
        
        return providers;
	}
	
}
