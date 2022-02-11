package it.unibo.disi.spaf.samza.connectors.rabbitmq;

import it.unibo.disi.spaf.samza.connectors.SamzaConnectorsFactory;
import it.unibo.disi.spaf.samza.connectors.spi.SamzaConnectorsProvider;

public class RabbitMQSamzaConnectorsProvider implements SamzaConnectorsProvider {

	@Override
	public String getConnectorsType() {
		return "rabbitmq";
	}

	@Override
	public SamzaConnectorsFactory createConnectorsFactory() {
		return RabbitMQSamzaConnectorsFactory.getInstance();
	}

}
