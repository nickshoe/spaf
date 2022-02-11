package it.unibo.disi.spaf.storm.connectors;

import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.storm.connectors.spi.StormConnectorsProvider;
import it.unibo.disi.spaf.storm.exceptions.StormStreamProcessingException;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class StormConnectors {

    private final static Logger logger = LoggerFactory.getLogger(StormConnectors.class);

    public static final void setupSourceSpout(TopologyBuilder topologyBuilder, Source<?, ?> source, String sourceName) {
        String connectorType = source.getType();

        StormConnectors.createConnectorsFactory(connectorType).setupSourceSpout(topologyBuilder, source, sourceName);
    }

    public static final void setupSinkBolt(TopologyBuilder topologyBuilder, Sink<?, ?> sink, String sinkName, String previousComponentId) {
        String connectorType = sink.getType();

        StormConnectors.createConnectorsFactory(connectorType).setupSinkBolt(topologyBuilder, sink, sinkName, previousComponentId);
    }

    // TODO: cache connection factories already built
    private static final StormConnectorsFactory createConnectorsFactory(String connectorType) {
        StormConnectorsFactory connectorsFactory;

        List<StormConnectorsProvider> providers = StormConnectors
                .getStormConnectorsProviders();

        if (providers.isEmpty()) {
            throw new StormStreamProcessingException("No Storm Connectors provider found");
        }

        List<StormConnectorsProvider> connectorsTypeProviders = providers.stream()
                .filter(provider -> provider.getConnectorsType().equals(connectorType)).collect(Collectors.toList());

        if (connectorsTypeProviders.isEmpty()) {
            throw new StormStreamProcessingException(
                    "No Storm Connectors provider found for type " + connectorType);
        }

        if (connectorsTypeProviders.size() > 1) {
            throw new StormStreamProcessingException(
                    "Multiple Storm Connectors providers found for type " + connectorType);
        }

        StormConnectorsProvider connectorsTypeProvider = connectorsTypeProviders.get(0);

        connectorsFactory = connectorsTypeProvider.createConnectorsFactory();

        return connectorsFactory;
    }

    // TODO: cache found providers
    private static final List<StormConnectorsProvider> getStormConnectorsProviders() {
        List<StormConnectorsProvider> providers = new ArrayList<>();

        ServiceLoader<StormConnectorsProvider> serviceLoader = ServiceLoader
                .load(StormConnectorsProvider.class);

        for (StormConnectorsProvider loadedProvider : serviceLoader) {
            providers.add(loadedProvider);
        }

        if (providers.isEmpty()) {
            logger.warn("No Connectors providers found.");
        }

        return providers;
    }

}
