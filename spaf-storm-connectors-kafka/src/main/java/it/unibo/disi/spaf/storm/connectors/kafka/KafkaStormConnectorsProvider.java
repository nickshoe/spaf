package it.unibo.disi.spaf.storm.connectors.kafka;

import it.unibo.disi.spaf.storm.connectors.StormConnectorsFactory;
import it.unibo.disi.spaf.storm.connectors.spi.StormConnectorsProvider;

public class KafkaStormConnectorsProvider implements StormConnectorsProvider {

	@Override
	public String getConnectorsType() {
		return "kafka";
	}

	@Override
	public StormConnectorsFactory createConnectorsFactory() {
		return KafkaStormConnectorsFactory.getInstance();
	}

}
