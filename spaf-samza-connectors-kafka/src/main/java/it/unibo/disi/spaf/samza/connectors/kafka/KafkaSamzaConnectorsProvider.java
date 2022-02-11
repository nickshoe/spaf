package it.unibo.disi.spaf.samza.connectors.kafka;

import it.unibo.disi.spaf.samza.connectors.SamzaConnectorsFactory;
import it.unibo.disi.spaf.samza.connectors.spi.SamzaConnectorsProvider;

public class KafkaSamzaConnectorsProvider implements SamzaConnectorsProvider {

	@Override
	public String getConnectorsType() {
		return "kafka";
	}

	@Override
	public SamzaConnectorsFactory createConnectorsFactory() {
		return KafkaSamzaConnectorsFactory.getInstance();
	}

}
