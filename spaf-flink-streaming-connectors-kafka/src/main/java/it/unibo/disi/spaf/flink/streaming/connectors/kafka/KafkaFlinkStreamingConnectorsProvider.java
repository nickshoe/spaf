package it.unibo.disi.spaf.flink.streaming.connectors.kafka;

import it.unibo.disi.spaf.flink.streaming.connectors.FlinkStreamingConnectorsFactory;
import it.unibo.disi.spaf.flink.streaming.connectors.spi.FlinkStreamingConnectorsProvider;

public class KafkaFlinkStreamingConnectorsProvider implements FlinkStreamingConnectorsProvider {

	@Override
	public String getConnectorsType() {
		return "kafka";
	}

	@Override
	public FlinkStreamingConnectorsFactory createConnectorsFactory() {
		return KafkaFlinkStreamingConnectorsFactory.getInstance();
	}

}
