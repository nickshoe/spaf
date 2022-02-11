package it.unibo.disi.spaf.spark.streaming.connectors.kafka.producer;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import it.unibo.disi.spaf.spark.streaming.connectors.kafka.pooling.LazySerializableObjectPool;

public class LazySerializableSparkKafkaProducerPool<K, V> extends LazySerializableObjectPool<SparkKafkaProducer<K, V>> {

	private static final long serialVersionUID = 1L;
	
	private final PooledObjectFactory<SparkKafkaProducer<K, V>> pooledObjectFactory;

	public LazySerializableSparkKafkaProducerPool(PooledObjectFactory<SparkKafkaProducer<K, V>> pooledObjectFactory) {
		super();
		this.pooledObjectFactory = pooledObjectFactory;
	}

	@Override
	protected ObjectPool<SparkKafkaProducer<K, V>> createDelegate() {
		GenericObjectPoolConfig<SparkKafkaProducer<K, V>> config = new GenericObjectPoolConfig<SparkKafkaProducer<K, V>>();
		
		// TODO: pass in a configuration?
		final int maxNumOfProducers = 10;
		config.setMaxTotal(maxNumOfProducers);
		config.setMaxIdle(maxNumOfProducers);
		
		return new GenericObjectPool<SparkKafkaProducer<K, V>>(this.pooledObjectFactory, config);
	}
	
}
