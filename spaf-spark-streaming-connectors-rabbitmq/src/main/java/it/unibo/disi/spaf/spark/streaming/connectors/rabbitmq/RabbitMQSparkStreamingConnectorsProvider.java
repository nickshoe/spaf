package it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq;

import it.unibo.disi.spaf.spark.streaming.connectors.SparkStreamingConnectorsFactory;
import it.unibo.disi.spaf.spark.streaming.connectors.spi.SparkStreamingConnectorsProvider;

public class RabbitMQSparkStreamingConnectorsProvider implements SparkStreamingConnectorsProvider {

	@Override
	public String getConnectorsType() {
		return "rabbitmq";
	}

	@Override
	public SparkStreamingConnectorsFactory createConnectorsFactory() {
		return RabbitMQSparkStreamingConnectorsFactory.getInstance();
	}

}
