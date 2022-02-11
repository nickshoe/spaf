package it.unibo.disi.spaf.samza.connectors;

import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.samza.connectors.spi.SamzaConnectorsProvider;
import it.unibo.disi.spaf.samza.exceptions.SamzaStreamProcessingException;
import org.apache.samza.application.descriptors.StreamApplicationDescriptor;
import org.apache.samza.application.descriptors.TaskApplicationDescriptor;
import org.apache.samza.operators.KV;
import org.apache.samza.operators.MessageStream;
import org.apache.samza.operators.OutputStream;
import org.apache.samza.system.SystemStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class SamzaConnectors {

	private final static Logger logger = LoggerFactory.getLogger(SamzaConnectors.class);

	public static final <K, V> void setupInputStream(TaskApplicationDescriptor appDescriptor, Source<?, ?> source) {
		String connectorType = source.getType();
		
		SamzaConnectors.createConnectorsFactory(connectorType).setupInputStream(appDescriptor, source);
	}
	
	public static final <K, V> MessageStream<KV<K, V>> getInputStream(StreamApplicationDescriptor appDescriptor, Source<?, ?> source) {
		String connectorType = source.getType();

		return SamzaConnectors.createConnectorsFactory(connectorType).getInputStream(appDescriptor, source);
	}

	public static final <K, V> SystemStream setupOutputStream(TaskApplicationDescriptor appDescriptor, Sink<?, ?> sink) {
		String connectorType = sink.getType();
		
		return SamzaConnectors.createConnectorsFactory(connectorType).setupOutputStream(appDescriptor, sink);
	}
	
	public static final <K, V> OutputStream<KV<K, V>> getOutputStream(StreamApplicationDescriptor appDescriptor, Sink<?, ?> sink) {
		String connectorType = sink.getType();

		return SamzaConnectors.createConnectorsFactory(connectorType).getOutputStream(appDescriptor, sink);
	}

	// TODO: cache connection factories already built
	private static final SamzaConnectorsFactory createConnectorsFactory(String connectorType) {
		SamzaConnectorsFactory connectorsFactory = null;

		List<SamzaConnectorsProvider> providers = SamzaConnectors.getSamzaConnectorsProviders();

		if (providers.isEmpty()) {
			throw new SamzaStreamProcessingException("No Samza Connectors provider found");
		}

		List<SamzaConnectorsProvider> connectorsTypeProviders = providers.stream()
				.filter(provider -> provider.getConnectorsType().equals(connectorType)).collect(Collectors.toList());

		if (connectorsTypeProviders.isEmpty()) {
			throw new SamzaStreamProcessingException(
					"No Samza Connectors provider found for type " + connectorType);
		}

		if (connectorsTypeProviders.size() > 1) {
			throw new SamzaStreamProcessingException(
					"Multiple Samza Connectors providers found for type " + connectorType);
		}

		SamzaConnectorsProvider connectorsTypeProvider = connectorsTypeProviders.get(0);

		connectorsFactory = connectorsTypeProvider.createConnectorsFactory();

		return connectorsFactory;
	}

	// TODO: cache found providers
	private static final List<SamzaConnectorsProvider> getSamzaConnectorsProviders() {
		List<SamzaConnectorsProvider> providers = new ArrayList<>();

		ServiceLoader<SamzaConnectorsProvider> serviceLoader = ServiceLoader.load(SamzaConnectorsProvider.class);

		for (SamzaConnectorsProvider loadedProvider : serviceLoader) {
			providers.add(loadedProvider);
		}

		if (providers.isEmpty()) {
			logger.warn("No Connectors providers found.");
		}

		return providers;
	}

}
