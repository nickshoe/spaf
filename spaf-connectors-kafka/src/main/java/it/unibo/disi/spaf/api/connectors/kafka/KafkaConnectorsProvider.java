package it.unibo.disi.spaf.api.connectors.kafka;

import it.unibo.disi.spaf.connectors.ConnectorsFactory;
import it.unibo.disi.spaf.connectors.spi.ConnectorsProvider;

public class KafkaConnectorsProvider implements ConnectorsProvider {

	@Override
	public String getConnectorsType() {
		return "kafka";
	}

	@Override
	public ConnectorsFactory createConnectorsFactory() {
		return KafkaConnectorsFactory.getInstance();
	}

}
