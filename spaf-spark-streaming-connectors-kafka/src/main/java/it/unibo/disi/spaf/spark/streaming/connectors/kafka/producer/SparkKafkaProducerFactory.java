package it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer;

import java.io.Serializable;

public abstract class SparkKafkaProducerFactory implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public abstract <K, V> SparkKafkaProducer<K, V> newInstance();
	
}
