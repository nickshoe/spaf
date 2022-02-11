package it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.receiver.Receiver;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.connectors.rabbitmq.RabbitMQSink;
import it.unibo.disi.spaf.api.connectors.rabbitmq.RabbitMQSource;
import it.unibo.disi.spaf.internals.Element;
import it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.pooling.LazySerializableObjectPool;
import it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer.BaseSparkRabbitMQProducerFactory;
import it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer.LazySerializableSparkRabbitMQProducerPool;
import it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer.PooledSparkRabbitMQProducerFactory;
import it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer.SparkRabbitMQProducer;
import it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer.SparkRabbitMQProducerFactory;
import it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.receiver.RabbitMQReceiver;


public class SparkRabbitMQUtils {
	
	public static final <K, V> JavaDStream<Element<K, V>> createInputDStream(
		JavaStreamingContext javaStreamingContext, 
		Application application, 
		RabbitMQSource<?, ?> rabbitMQSource
	) {		
		String host = rabbitMQSource.getHost();
		int port = rabbitMQSource.getPort();
		String username = rabbitMQSource.getUsername();
		String password = rabbitMQSource.getPassword();
		String queue = rabbitMQSource.getQueue();
		String idDeserializer = rabbitMQSource.getIdDeserializer();
		String bodyDeserializer = rabbitMQSource.getBodyDeserializer();
		
		StorageLevel storageLevel = StorageLevels.MEMORY_ONLY; // TODO: what's this? parametrize?
		Receiver<Element<K, V>> receiver = new RabbitMQReceiver<>(host, port, username, password, queue, storageLevel, idDeserializer, bodyDeserializer);
		
		JavaInputDStream<Element<K, V>> inputStream = javaStreamingContext.receiverStream(receiver);
		
		return inputStream;
	}
	
	public static final <K, V> LazySerializableObjectPool<SparkRabbitMQProducer<K, V>> createRabbitMQProducerLazyPool(RabbitMQSink<?, ?> rabbitMQSink) {
		String host = rabbitMQSink.getHost();
		int port = rabbitMQSink.getPort();
		String username = rabbitMQSink.getUsername();
		String password = rabbitMQSink.getPassword();
		String queue = rabbitMQSink.getQueue();
		String idSerializer = rabbitMQSink.getIdSerializer();
		String bodySerializer = rabbitMQSink.getBodySerializer();

		SparkRabbitMQProducerFactory factory = new BaseSparkRabbitMQProducerFactory(host, port, username, password, queue, idSerializer, bodySerializer);
		PooledObjectFactory<SparkRabbitMQProducer<K, V>> pooledObjectFactory = new PooledSparkRabbitMQProducerFactory<>(factory);

		LazySerializableObjectPool<SparkRabbitMQProducer<K, V>> lazyPool = new LazySerializableSparkRabbitMQProducerPool<>(pooledObjectFactory);
		
		return lazyPool;
	}

}
