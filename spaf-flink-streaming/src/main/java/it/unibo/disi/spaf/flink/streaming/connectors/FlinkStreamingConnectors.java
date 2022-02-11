package it.unibo.disi.spaf.flink.streaming.connectors;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.flink.streaming.connectors.spi.FlinkStreamingConnectorsProvider;
import it.unibo.disi.spaf.flink.streaming.exceptions.FlinkStreamingStreamProcessingException;
import it.unibo.disi.spaf.internals.Element;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * TODO: duplicated code, see spaf-spark-streaming SparkStreamingConnectors class
 */
public class FlinkStreamingConnectors {
	
	private final static Logger logger = LoggerFactory.getLogger(FlinkStreamingConnectors.class);
	
	public static final DataStream<Element<Object, Object>> createDataStream(
		StreamExecutionEnvironment environment,
		Application application, 
		Source<?, ?> source
	) {
		String connectorType = source.getType();
		
		return FlinkStreamingConnectors.createConnectorsFactory(connectorType).createInputStream(environment, application, source);
	}
	
	public static final void setupStreamOutput(DataStream<Element<Object, Object>> stream, Sink<?, ?> sink) {
		String connectorType = sink.getType();
		
		FlinkStreamingConnectors.createConnectorsFactory(connectorType).setupStreamOutput(stream, sink);
	}
	
	// TODO: cache connection factories already built
	private static final FlinkStreamingConnectorsFactory createConnectorsFactory(String connectorType) {
		FlinkStreamingConnectorsFactory connectorsFactory = null;
		
		List<FlinkStreamingConnectorsProvider> providers = FlinkStreamingConnectors.getFlinkStreamingConnectorsProviders();
		
		if (providers.isEmpty()) {
			throw new FlinkStreamingStreamProcessingException("No Flink Streaming Connectors provider found");
		}
		
		List<FlinkStreamingConnectorsProvider> connectorsTypeProviders = providers.stream()
			.filter(provider -> provider.getConnectorsType().equals(connectorType))
			.collect(Collectors.toList());
		
		if (connectorsTypeProviders.isEmpty()) {
			throw new FlinkStreamingStreamProcessingException("No Flink Streaming Connectors provider found for type " + connectorType);	
		}
		
		if (connectorsTypeProviders.size() > 1) {
			throw new FlinkStreamingStreamProcessingException("Multiple Flink Streaming Connectors providers found for type " + connectorType);
		}
		
		FlinkStreamingConnectorsProvider connectorsTypeProvider = connectorsTypeProviders.get(0);
		
		connectorsFactory = connectorsTypeProvider.createConnectorsFactory();
		
		return connectorsFactory;
	}
	
	// TODO: cache found providers
	private static final List<FlinkStreamingConnectorsProvider> getFlinkStreamingConnectorsProviders() {
		List<FlinkStreamingConnectorsProvider> providers = new ArrayList<>();
	        
        ServiceLoader<FlinkStreamingConnectorsProvider> serviceLoader = ServiceLoader.load(FlinkStreamingConnectorsProvider.class);

		for (FlinkStreamingConnectorsProvider loadedProvider : serviceLoader) {
			providers.add(loadedProvider);
		}
        
        if (providers.isEmpty()) {
        	logger.warn("No Connectors providers found.");
        }
        
        return providers;
	}
	
}
