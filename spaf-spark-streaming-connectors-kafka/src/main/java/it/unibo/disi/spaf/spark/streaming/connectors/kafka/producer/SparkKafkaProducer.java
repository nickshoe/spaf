package it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class SparkKafkaProducer<K, V> {
	
	private final String topicName;
	private final KafkaProducer<K, V> kafkaProducer;

	public SparkKafkaProducer(Properties properties, String topicName) {
		super();
		this.topicName = topicName;
		this.kafkaProducer = new KafkaProducer<K, V>(properties);
	}

	public Future<RecordMetadata> send(V value) {
		return this.kafkaProducer.send(new ProducerRecord<>(topicName, value));
	}
	
	public Future<RecordMetadata> send(K key, V value) {
		return this.kafkaProducer.send(new ProducerRecord<>(topicName, key, value));
	}

	public void close() {
		this.kafkaProducer.close();
	}
	
}
