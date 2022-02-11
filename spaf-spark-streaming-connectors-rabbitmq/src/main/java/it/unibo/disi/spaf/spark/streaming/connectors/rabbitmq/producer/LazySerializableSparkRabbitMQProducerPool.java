package it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.pooling.LazySerializableObjectPool;

public class LazySerializableSparkRabbitMQProducerPool<K, V> extends LazySerializableObjectPool<SparkRabbitMQProducer<K, V>> {

	private static final long serialVersionUID = 1L;
	
	private final PooledObjectFactory<SparkRabbitMQProducer<K, V>> pooledObjectFactory;

	public LazySerializableSparkRabbitMQProducerPool(PooledObjectFactory<SparkRabbitMQProducer<K, V>> pooledObjectFactory) {
		super();
		this.pooledObjectFactory = pooledObjectFactory;
	}

	@Override
	protected ObjectPool<SparkRabbitMQProducer<K, V>> createDelegate() {
		GenericObjectPoolConfig<SparkRabbitMQProducer<K, V>> config = new GenericObjectPoolConfig<SparkRabbitMQProducer<K, V>>();
		
		// TODO: pass in a configuration?
		final int maxNumOfProducers = 10;
		config.setMaxTotal(maxNumOfProducers);
		config.setMaxIdle(maxNumOfProducers);
		
		return new GenericObjectPool<SparkRabbitMQProducer<K, V>>(this.pooledObjectFactory, config);
	}
	
}
