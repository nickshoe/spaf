package it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer;

import java.io.Serializable;

public abstract class SparkRabbitMQProducerFactory implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public abstract <K, V> SparkRabbitMQProducer<K, V> newInstance();
	
}