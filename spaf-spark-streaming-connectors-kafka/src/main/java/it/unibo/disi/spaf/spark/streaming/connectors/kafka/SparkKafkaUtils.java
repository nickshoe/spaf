package it.unibo.disi.spaf.spark.streaming.connectors.kafka;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.ConsumerStrategy;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.LocationStrategy;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.connectors.kafka.KafkaSink;
import it.unibo.disi.spaf.api.connectors.kafka.KafkaSource;
import it.unibo.disi.spaf.internals.Element;
import it.unibo.disi.spaf.spark.streaming.connectors.kafka.pooling.LazySerializableObjectPool;
import it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer.BaseSparkKafkaProducerFactory;
import it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer.LazySerializableSparkKafkaProducerPool;
import it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer.PooledSparkKafkaProducerFactory;
import it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer.SparkKafkaProducer;
import it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer.SparkKafkaProducerFactory;

public class SparkKafkaUtils {

	public static final <K, V> JavaDStream<Element<K, V>> createInputDStream(
		JavaStreamingContext javaStreamingContext, 
		Application application, 
		KafkaSource<?, ?> kafkaSource
	) {		
		String boostrapServers = kafkaSource.getBootstrapServers();
		String consumerGroupId = SparkKafkaUtils.generateConsumerGroupId(application.getName());
		String inputTopicName = kafkaSource.getTopicName();
		String keyDeserializer = kafkaSource.getKeyDeserializer();
		String valueDeserializer = kafkaSource.getValueDeserializer();

		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("bootstrap.servers", boostrapServers);
		kafkaParams.put("key.deserializer", keyDeserializer);
		kafkaParams.put("value.deserializer", valueDeserializer);
		kafkaParams.put("group.id", consumerGroupId); // TODO: if consumer-group is present in source properties, use it; otherwise generate it
		kafkaParams.put("auto.offset.reset", "latest"); // TODO: accept config from API
		kafkaParams.put("enable.auto.commit", false); // TODO: accept config from API
		// kafkaParams.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 33554432); // TODO: accept config from API

		Collection<String> topics = Collections.singletonList(inputTopicName);

		// Spark keep cached Kafka consumers on executors.
		// This will distribute partitions evenly across available executors.
		LocationStrategy locationStrategy = LocationStrategies.PreferConsistent();

		// ConsumerStrategies provides an abstraction that allows Spark
		// to obtain properly configured consumers even after restart from checkpoint.
		ConsumerStrategy<K, V> consumerStrategy = ConsumerStrategies.Subscribe(topics, kafkaParams);

		JavaInputDStream<ConsumerRecord<K, V>> inputStream = KafkaUtils
				.createDirectStream(javaStreamingContext, locationStrategy, consumerStrategy);
		
		// Transform Kafka ConsumerRecord into SPAF KV Object
		JavaDStream<Element<K, V>> stream = inputStream.map(record -> new Element<K, V>(record.key(), record.value()));
		
		return stream;
	}

	private static final String generateConsumerGroupId(String appName) {
		return SparkKafkaUtils.slugify(appName).concat("-consumers");
	}

	private static final String slugify(String string) {
		return String.join("-", string.trim().toLowerCase().split(" "));
	}

	public static final <K, V> LazySerializableObjectPool<SparkKafkaProducer<K, V>> createKafkaProducerLazyPool(KafkaSink<?, ?> kafkaSink) {
		String boostrapServers = kafkaSink.getBootstrapServers();
		String outputTopicName = kafkaSink.getTopicName();
		String keySerializer = kafkaSink.getKeySerializer();
		String valueSerializer = kafkaSink.getValueSerializer();

		Properties kafkaProducerProps = new Properties();
		kafkaProducerProps.put("bootstrap.servers", boostrapServers);
		kafkaProducerProps.put("key.serializer", keySerializer);
		kafkaProducerProps.put("value.serializer", valueSerializer);
		kafkaProducerProps.put("acks", "all"); // TODO: make it configurable?
		kafkaProducerProps.put("retries", 0); // TODO: make it configurable?
		kafkaProducerProps.put("linger.ms", 1); // TODO: make it configurable?

		SparkKafkaProducerFactory factory = new BaseSparkKafkaProducerFactory(kafkaProducerProps, outputTopicName);
		PooledObjectFactory<SparkKafkaProducer<K, V>> pooledObjectFactory = new PooledSparkKafkaProducerFactory<>(factory);

		LazySerializableObjectPool<SparkKafkaProducer<K, V>> lazyPool = new LazySerializableSparkKafkaProducerPool<>(pooledObjectFactory);
		
		return lazyPool;
	}
}
