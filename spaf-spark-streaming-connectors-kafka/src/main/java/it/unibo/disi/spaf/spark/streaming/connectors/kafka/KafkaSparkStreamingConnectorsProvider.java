package it.unibo.disi.spaf.spark.streaming.connectors.kafka;

import it.unibo.disi.spaf.spark.streaming.connectors.SparkStreamingConnectorsFactory;
import it.unibo.disi.spaf.spark.streaming.connectors.spi.SparkStreamingConnectorsProvider;

public class KafkaSparkStreamingConnectorsProvider implements SparkStreamingConnectorsProvider {

	@Override
	public String getConnectorsType() {
		return "kafka";
	}

	@Override
	public SparkStreamingConnectorsFactory createConnectorsFactory() {
		return KafkaSparkStreamingConnectorsFactory.getInstance();
	}

}
