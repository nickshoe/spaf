package it.unibo.disi.spaf.api.connectors.rabbitmq;

import it.unibo.disi.spaf.connectors.ConnectorsFactory;
import it.unibo.disi.spaf.connectors.spi.ConnectorsProvider;

public class RabbitMQConnectorsProvider implements ConnectorsProvider {

	@Override
	public String getConnectorsType() {
		return "rabbitmq";
	}

	@Override
	public ConnectorsFactory createConnectorsFactory() {
		return RabbitMQConnectorsFactory.getInstance();
	}

}
