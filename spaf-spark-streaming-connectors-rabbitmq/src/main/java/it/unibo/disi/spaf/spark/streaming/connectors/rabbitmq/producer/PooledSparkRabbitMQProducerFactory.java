package it.unibo.disi.spaf.spark.streaming.connectors.rabbitmq.producer;

import java.io.Serializable;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class PooledSparkRabbitMQProducerFactory<K, V> extends BasePooledObjectFactory<SparkRabbitMQProducer<K, V>> implements Serializable {

	private static final long serialVersionUID = 1L;

	private final SparkRabbitMQProducerFactory factory;

	public PooledSparkRabbitMQProducerFactory(SparkRabbitMQProducerFactory factory) {
		super();
		this.factory = factory;
	}

	@Override
	public SparkRabbitMQProducer<K, V> create() {
		return this.factory.newInstance();
	}

	@Override
	public PooledObject<SparkRabbitMQProducer<K, V>> wrap(SparkRabbitMQProducer<K, V> obj) {
		return new DefaultPooledObject<SparkRabbitMQProducer<K, V>>(obj);
	}

	@Override
	public void destroyObject(PooledObject<SparkRabbitMQProducer<K, V>> p) throws Exception {
		p.getObject().close();

		super.destroyObject(p);
	}

}