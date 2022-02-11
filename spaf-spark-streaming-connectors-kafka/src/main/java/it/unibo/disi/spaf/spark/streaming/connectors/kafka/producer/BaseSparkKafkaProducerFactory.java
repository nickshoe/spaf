package it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer;

import java.util.Properties;

public class BaseSparkKafkaProducerFactory extends SparkKafkaProducerFactory {

	private static final long serialVersionUID = 1L;
	
	private final Properties producerProperties;
	private final String topic;
	
	public BaseSparkKafkaProducerFactory(Properties producerProperties, String topic) {
		super();
		this.producerProperties = producerProperties;
		this.topic = topic;
	}

	@Override
	public <K, V> SparkKafkaProducer<K, V> newInstance() {
		return new SparkKafkaProducer<K, V>(producerProperties, topic);
	}

}
