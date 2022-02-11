package it.unibo.disi.spaf.storm.connectors.rabbitmq;

import it.unibo.disi.spaf.storm.connectors.StormConnectorsFactory;
import it.unibo.disi.spaf.storm.connectors.spi.StormConnectorsProvider;

public class RabbitMQStormConnectorsProvider implements StormConnectorsProvider {

	@Override
	public String getConnectorsType() {
		return "rabbitmq";
	}

	@Override
	public StormConnectorsFactory createConnectorsFactory() {
		return RabbitMQStormConnectorsFactory.getInstance();
	}

}
