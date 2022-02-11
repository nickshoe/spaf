package it.unibo.disi.spaf.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.disi.spaf.common.exceptions.StreamProcessingException;
import it.unibo.disi.spaf.connectors.ConnectorsFactory;
import it.unibo.disi.spaf.connectors.spi.ConnectorsProvider;
import it.unibo.disi.spaf.spi.StreamProcessingProvider;

/**
 * Bootstrap class that is used to obtain a {@link ContextFactory} and {@link ConnectorsFactory}.
 */
public class StreamProcessing {
    private static final Logger logger = LoggerFactory.getLogger(StreamProcessing.class);

	public static final ContextFactory createContextFactory() {
		ContextFactory contextFactory = null;
		
		List<StreamProcessingProvider> providers = StreamProcessing.getStreamProcessingProviders();
		
		for (StreamProcessingProvider provider : providers) {
			contextFactory = provider.createContextFactory();
			if (contextFactory != null) {
				break;
			}
		}
		
		if (contextFactory == null) {
			throw new StreamProcessingException("No Stream Processing provider for Context");
		}
		
		return contextFactory;
	}
	

	private static final List<StreamProcessingProvider> getStreamProcessingProviders() {
        List<StreamProcessingProvider> providers = new ArrayList<>();
        
        ServiceLoader<StreamProcessingProvider> serviceLoader = ServiceLoader.load(StreamProcessingProvider.class);
        
        Iterator<StreamProcessingProvider> loadedProviders = serviceLoader.iterator();
        while (loadedProviders.hasNext()) {
        	StreamProcessingProvider loadedProvider = loadedProviders.next();
        	
            providers.add(loadedProvider);
        }
        
        if (providers.isEmpty()) {
        	logger.warn("No providers found.");
        }
        
        if (providers.size() > 1) {
        	logger.warn("Multiple providers found: {}", providers);	
        }

        return providers;
    }
	
	public static final <K, V> Source<K, V> createSource(Config config) {
		Config sourceConfig = config.getSubConfig("source");
		
		ConnectorsFactory connectorsFactory = StreamProcessing.createConnectorsFactory(sourceConfig);
		
		Source<K, V> source = connectorsFactory.createSource(sourceConfig);
		
		return source;
	}
	
	public  static final <K, V> Sink<K, V> createSink(Config config) {
		Config sinkConfig = config.getSubConfig("sink");
		
		ConnectorsFactory connectorsFactory = StreamProcessing.createConnectorsFactory(sinkConfig);
		
		Sink<K, V> sink = connectorsFactory.createSink(sinkConfig);
		
		return sink;
	}
	
	private static final ConnectorsFactory createConnectorsFactory(Config connectorConfig) {
		ConnectorsFactory connectorsFactory = null;
		
		List<ConnectorsProvider> providers = StreamProcessing.getConnectorsProviders();
		
		if (providers.isEmpty()) {
			throw new StreamProcessingException("No Connectors provider found");
		}
		
		String connectorType = connectorConfig.getString("type");
		List<ConnectorsProvider> connectorsTypeProviders = providers.stream()
			.filter(provider -> provider.getConnectorsType().equals(connectorType))
			.collect(Collectors.toList());
		
		if (connectorsTypeProviders.isEmpty()) {
			throw new StreamProcessingException("No Connectors provider found for type " + connectorType);	
		}
		
		if (connectorsTypeProviders.size() > 1) {
			throw new StreamProcessingException("Multiple Connectors providers found for type " + connectorType);
		}
		
		ConnectorsProvider connectorsTypeProvider = connectorsTypeProviders.get(0);
		
		connectorsFactory = connectorsTypeProvider.createConnectorsFactory();
		
		return connectorsFactory;
	}
	
	private static final List<ConnectorsProvider> getConnectorsProviders() {
		 List<ConnectorsProvider> providers = new ArrayList<>();
	        
	        ServiceLoader<ConnectorsProvider> serviceLoader = ServiceLoader.load(ConnectorsProvider.class);
	        
	        Iterator<ConnectorsProvider> loadedProviders = serviceLoader.iterator();
	        while (loadedProviders.hasNext()) {
	        	ConnectorsProvider loadedProvider = loadedProviders.next();
	        	
	            providers.add(loadedProvider);
	        }
	        
	        if (providers.isEmpty()) {
	        	logger.warn("No Connectors providers found.");
	        }
	        
	        return providers;
	}
	
}
