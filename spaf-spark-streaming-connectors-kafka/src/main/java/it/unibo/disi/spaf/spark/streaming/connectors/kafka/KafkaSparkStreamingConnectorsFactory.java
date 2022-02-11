package it.unibo.disi.spaf.spark.streaming.connectors.kafka;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import it.unibo.disi.spaf.api.Application;
import it.unibo.disi.spaf.api.Sink;
import it.unibo.disi.spaf.api.Source;
import it.unibo.disi.spaf.api.connectors.kafka.KafkaSink;
import it.unibo.disi.spaf.api.connectors.kafka.KafkaSource;
import it.unibo.disi.spaf.internals.Element;
import it.unibo.disi.spaf.spark.streaming.connectors.SparkStreamingConnectorsFactory;
import it.unibo.disi.spaf.spark.streaming.connectors.kafka.pooling.LazySerializableObjectPool;
import it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer.SparkKafkaProducer;

public class KafkaSparkStreamingConnectorsFactory extends SparkStreamingConnectorsFactory {
	
	private static KafkaSparkStreamingConnectorsFactory instance;
	
	private KafkaSparkStreamingConnectorsFactory() {
		super();
	}
	
	public static final KafkaSparkStreamingConnectorsFactory getInstance() {
		if (instance == null) {
			instance = new KafkaSparkStreamingConnectorsFactory();
		}
		
		return instance;
	}

	@Override
	public <K, V> JavaDStream<Element<K, V>> createInputStream(JavaStreamingContext streamingContext, Application application, Source<?, ?> source) {
		KafkaSource<?, ?> kafkaSource = (KafkaSource<?, ?>) source;
		
		return SparkKafkaUtils.createInputDStream(streamingContext, application, kafkaSource);
	}
	
	@Override
	public <K, V> void setupStreamOutput(JavaStreamingContext streamingContext, JavaDStream<Element<K, V>> stream, Sink<?, ?> sink) {
		KafkaSink<?, ?> kafkaSink = (KafkaSink<?, ?>) sink;
		
		LazySerializableObjectPool<SparkKafkaProducer<K, V>> producerLazyPool = SparkKafkaUtils.createKafkaProducerLazyPool(kafkaSink);
		Broadcast<LazySerializableObjectPool<SparkKafkaProducer<K, V>>> sharedProducerPool = streamingContext.sparkContext().broadcast(producerLazyPool);
	
		// SparkStreaming doesn't have the concept of OutputStream or Sink, you need to realize it using foreachRDD
		stream.foreachRDD(rdd -> rdd.foreachPartition(partition -> { // This code will be executed in the workers
			SparkKafkaProducer<K, V> producer = sharedProducerPool.value().borrowObject();

			// TODO: should null-valued record be sent anyways?
			partition.forEachRemaining(record -> producer.send(record.getKey(), record.getValue()));
			
			sharedProducerPool.value().returnObject(producer);
		}));
	}

}
