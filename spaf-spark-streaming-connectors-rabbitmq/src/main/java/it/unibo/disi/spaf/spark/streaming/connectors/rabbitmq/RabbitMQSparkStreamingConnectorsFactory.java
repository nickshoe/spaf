package it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq;

import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.connectors.rabbitmq.RabbitMQSink;
import it.unibo.disi.spaf.api.connectors.rabbitmq.RabbitMQSource;
import it.unibo.disi.spaf.internals.Element;
import it.unibo.disi.spaf.spark.streaming.connectors.SparkStreamingConnectorsFactory;
import it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.pooling.LazySerializableObjectPool;
import it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer.SparkRabbitMQProducer;

public class RabbitMQSparkStreamingConnectorsFactory extends SparkStreamingConnectorsFactory {

	private static RabbitMQSparkStreamingConnectorsFactory instance;
	
	private RabbitMQSparkStreamingConnectorsFactory() {
		super();
	}
	
	public static final RabbitMQSparkStreamingConnectorsFactory getInstance() {
		if (instance == null) {
			instance = new RabbitMQSparkStreamingConnectorsFactory();
		}
		
		return instance;
	}
	
	@Override
	public <K, V> JavaDStream<Element<K, V>> createInputStream(JavaStreamingContext streamingContext, Application application, Source<?, ?> source) {
		RabbitMQSource<?, ?> rabbitMQSource = (RabbitMQSource<?, ?>) source;
		
		return SparkRabbitMQUtils.createInputDStream(streamingContext, application, rabbitMQSource);
	}
	
	@Override
	public <K, V> void setupStreamOutput(JavaStreamingContext streamingContext, JavaDStream<Element<K, V>> stream, Sink<?, ?> sink) {
		RabbitMQSink<?, ?> rabbitMQSink = (RabbitMQSink<?, ?>) sink;
		
		LazySerializableObjectPool<SparkRabbitMQProducer<K, V>> producerLazyPool = SparkRabbitMQUtils.createRabbitMQProducerLazyPool(rabbitMQSink);
		Broadcast<LazySerializableObjectPool<SparkRabbitMQProducer<K, V>>> sharedProducerPool = streamingContext.sparkContext().broadcast(producerLazyPool);
	
		// SparkStreaming doesn't have the concept of OutputStream or Sink, you need to realize it using foreachRDD
		stream.foreachRDD(rdd -> rdd.foreachPartition(partition -> { // This code will be executed in the workers
			SparkRabbitMQProducer<K, V> producer = sharedProducerPool.value().borrowObject();

			// TODO: should null-valued record be sent anyways?
			partition.forEachRemaining(record -> producer.send(record.getKey(), record.getValue()));
			
			sharedProducerPool.value().returnObject(producer);
		}));
	}

}
